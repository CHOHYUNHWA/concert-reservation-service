# 분산 트랜잭션 (Feat. Spring application Event)

>**개요**
>
>서비스가 확장됨에 따라 각 기능을 독립적인 서비스로 분리하고, 이에 따른 트랜잭션 관리 방안을 고려해야 한다.<br>
>단일 데이터베이스에서 관리되는 트랜잭션과 달리, 분산 환경에서는 여러 서비스 및 데이터 소스(DB, 메시지 큐, 외부 API 등) 를 아우르는 트랜잭션 관리가 필요하다.<br>
>이러한 한계를 해결하기 위해 분산 트랜잭션이 활용되며, 많은 기업에서 이를 적용하고 있다.<br>
>이번 장에서는 현재 콘서트 시스템을 분석하고, 확장성을 고려한 트랜잭션 분할 및 적용 방안을 설계해본다.

## 분산 트랜잭션이란?
- 2개 그 이상의 네트워크 상의 시스템 간의 트랜잭션을 말한다.
    - 쉽게말해, 여러 서비스의 기능들이 하나의 트랜잭션으로 묶이는 것이 아니라, 서비스 확장으로 인한 시스템 분리로 트랜잭션이 나뉘는 것을 말한다.
    - 서버의 분할이 될 수도 있고, DB의 분할이 될 수도 있고, 외부 API로 인한 분리가 될 수도 있다.

## 분산트랜잭션이 필요한 경우

> 작은 규모의 서비스나 트래픽이 많지 않은 서비스의 경우, 분산 트랜잭션이 필요하지 않다.<br>
> 그럼에도 분산 트랜잭션이 필요한 이유는 아래와 같다.

- 모든 도메인을 개발자가 직접 알아야 하는 문제 발생(도메인별로 각각의 팀에서 나눠서 개발하는 경우)
    - 서비스가 커질수록 서로의 도메인을 모두 알아야 하는 부담 증가
- 서비스가 여러 개로 나뉘었을 떄 -> MSA
    - "예약 서비스"와 "결제 서비스"가 서로 다른 서버에서 동작할 경우
        - 한번의 트랜잭션으로 처리할 수 없음 -> 분산 트랜잭션 필요
- DB가 여러 개로 분리되었을 때
    - 사용자 정보는 MySQL, 결제정보는 PostgreSQL
    - 트랜잭션이 각각 다른 DB에서 처리됨 -> 분산 트랜잭션 필요
- 외부 API가 포함될 떄
    - 결제 승인 요청을 PG사에 보내야 하는 경우
    - 비동기 처리시 PG사의 응답을 기다려야 하며, 트랜잭션이 보장되지 않음 -> 분산 트랜잭션 필요

## 현재 시스템을 분석하고 분산 트랜잭션 설계 해보기

> 일반적으로 분산트랜잭션이 적용되는 부분은 데이터가 INSERT, UPDATE, DELETE가 되는 기능들이다.<br>
> 현재의 시스템에서, 분산트랜잭션이 적용되어야할 기능들을 찾고 설계해보자.<br>
>
> **참고: 이번 학습에선 예약 혹은 결제 기능 성공 시 알림 외부 API를 호출해야하는 것을 가정한다.**

분산 트랜잭션이 적용될 수 있는 서비스는 다음과 같다.

- #### 콘서트 좌석 예약
    - 콘서트 정보 조회
    - 콘서트 스케쥴 조회
    - 좌석 정보 조회
    - 예약 정보 INSERT
    - 좌석 정보 UPDATE
    - 예약 완료 외부 알림 API 발송
- #### 콘서트 예약 결제
    - 예약 정보 조회
    - 좌석 정보 조회
    - 사용자 포인트 정보 조회
    - 결제 정보 INSERT
    - 예약 정보 UPDATE
    - 포인트 정보 UPDATE
    - 예약 완료 외부 알림 API 발송
- #### 포인트 충전
    - 사용자 정보 조회
    - 포인트 정보 UPDATE
    - 포인트 완료 외부 알림 API 발송


### 현재 시스템의 트랜잭션 범위

현재 설계된 트랜잭션의 범위는 하나의 Facade 전체가 Transaction으로 묶여 있다.

<br>

#### 콘서트 좌석 예약
```java
//예약
@Transactional
public ReservationHttpDto.ReservationCompletedResponse reservationWithOptimisticLock(ReservationHttpDto.ReservationRequest reservationRequest) {

    //콘서트 조회
    Concert concert = concertService.getConcert(reservationRequest.getConcertId());

    //콘서트 스케쥴 조회
    ConcertSchedule concertScheduleInfo = concertService.getConcertScheduleInfo(reservationRequest.getConcertScheduleId());

    //좌석 조회
    Seat findSeat = concertService.getSeatWithOptimisticLock(reservationRequest.getSeatId());

    //콘서트,좌석 유효성 검증
    concertService.isAvailableReservationSeat(concertScheduleInfo, findSeat);

    //예약 생성
    Reservation reservation = reservationService.createReservation(concertScheduleInfo, findSeat, findSeat.getId());

    //좌석 상태 변경
    concertService.assignSeat(findSeat);


    ReservationHttpDto.ReservationCompletedSeatDto seat = ReservationHttpDto.ReservationCompletedSeatDto.of(findSeat.getSeatNumber(), findSeat.getSeatPrice());
    return ReservationHttpDto.ReservationCompletedResponse.of(
            reservation.getId(),
            concert.getId(),
            concert.getTitle(),
            concertScheduleInfo.getConcertTime(),
            seat.getSeatPrice(),
            reservation.getStatus(),
            seat
    );
}
```

<br>

#### 콘서트  결제

```java
//결제
@Transactional
public PaymentHttpDto.PaymentCompletedResponse paymentWithPessimisticLock(String token, Long reservationId, Long userId) {
    //토큰 조회
    Queue queue = queueService.getToken(token);
    
    //예약 조회
    Reservation reservation = reservationService.validateReservationWithPessimisticLock(reservationId, userId);
    
    //좌석 조회
    Seat seat = concertService.getSeatWithoutLock(reservation.getSeatId());
    
    //포인트 조회
    Point point = pointService.getPointWithPessimisticLock(userId);

    //포인트 차감
    point.usePoint(seat.getSeatPrice());
    
    //예약 정보 변경
    reservation.changeCompletedStatus();
    
    //토큰 만료
    queueService.expireToken(queue);

    //결제 생성
    Payment completedPayment = paymentService.createPayment(reservationId, userId, seat.getSeatPrice());

    return PaymentHttpDto.PaymentCompletedResponse.of(completedPayment.getId(), completedPayment.getAmount(), completedPayment.getPaymentStatus());
}
```

<br>

#### 포인트 충전

```java
//충전
@Transactional
public PointHttpDto.ChargePointResponseDto chargePointWithPessimisticLock(Long userId, Long chargeAmount) {
    //유저 조회
    userService.existsUser(userId);

    //포인트 충전
    Point chargedPoint = pointService.chargePointWithPessimisticLock(userId, chargeAmount);

    return PointHttpDto.ChargePointResponseDto.of(chargedPoint);
}
```

<br>

### 분산 트랜잭션 나누기(Step.01)

[//]: # (Spring에서 제공하는 ApplicationEventPublisher, @EventListener 를 이용하여, Facade의 트랜잭션을 분리하여 설계 해보기)
- 대표적으로 PaymentFacade를 수도코드로 변환하여, 설계를 진행해보자.


#### 1. 현재상태

```java
@Transactional
class 결제_파사드{
    포인트차감();
    예약정보변경();
    결제생성();
    }
```

<br>

#### 2. ApplicationEventPublisher, EventListener 활용한 분산트랜잭션 적용

```java
//트랜잭션을 제거하고, 서비스 레이어에서 트랜잭션을 각각 실행 시킨다.
class 결제_파사드{
    
    콘서트결제() {
        포인트차감();
        if (포인트차감실패) {
            포인트차감실패이벤트발행();
            return;
        }
        포인트차감성공이벤트발행();
        예약정보변경();
        if (예약정보변경실패) {
            예약정보변경실패이벤트발행();
            return;
        }
        예약정보변경성공이벤트발행();
        결제생성();
        if (결제생성실패) {
            결제생성실패이벤트발행();
            return;
        }
        결제생성성공이벤트발행();
    }
}

//ApplicationEventPublisher 생성자 주입
@Component
class 포인트이벤트Publisher(){
    포인트차감성공이벤트발행();
    포인트차감실패이벤트발행();
}

//ApplicationEventPublisher 생성자 주입
@Component
class 예약이벤트Publisher(){
    예약정보변경성공이벤트발행();
    예약정보변경실패이벤트발행();
}

//ApplicationEventPublisher 생성자 주입
@Component
class 결제이벤트Publisher(){
    결제생성성공이벤트발행();
    결제생성실패이벤트발행();
}

//결제이벤트Publisher 생성자 주입
@EventListener
class 포인트이벤트Listener(){
    포인트복구이벤트handle(){
        //별도로 복구할 데이터가 없음
        에러발생();
    }
}

//결제이벤트Publisher 생성자 주입
@EventListener
class 예약이벤트Listener(){
    예약정보변경성공이벤트handle();
    예약정보변경실패이벤트handle(){
        포인트롤백();
    }
}

//결제이벤트Publisher 생성자 주입
@EventListener
class 결제이벤트Listener(){
    결제생성성공이벤트handle();
    결제생성실패이벤트handle(){
        포인트롤백();
        예약상태롤백();
    }
}


```

### 위와같이 트랜잭션을 나눴을때 문제점(Step.02)
- 트랜잭션을 분리하기 위하여, Facade 메서드 안이 조건 문과 서비스 메서드의 혼재로, 매우 복잡하다.
- 동기적으로 수행되어, 앞의 메서드의 수행시간이 오래걸리면 다음 로직이 그동안 대기하게 됨
  - 결과적으로 API 응답 속도가 매우 느릴 수 있다.

### Spring Event기반 SAGA(Choreography) 패턴 적용과 장/단점(Step.03)

####위와 같은 문제점을 해결하기 위해, SAGA 패턴(Feat. Choreography)을 활용할 수 있다.
- 이벤트 기반(Choreography 패턴)으로 위의 문제점(서비스 간 높은 결합도)을 해결할 수 있다.
- 하지만 비동기 처리시 Spring의 ApplicationEvent Pub/Sub으로는 순서를 보장할 수 없어 한계가있다.
  - Spring은 멀티쓰레드 기반이기 때문에, Redis나 Kafka같은 메시지 브로커가 필요하다
- 또한, 각각의 서비스 레이어의 트랜잭션이 실행되기때문에 별도의 Facade는 사라지게 되어, 비즈니스 로직의 흐름을 파악하기 어려울 수 있다.

#### 흐름도
```text
1️⃣ 결제 요청 (사용자가 요청)
2️⃣ 포인트 차감 트랜잭션 실행
    └ 포인트 차감 성공 이벤트 발행
3️⃣ 예약 변경 트랜잭션 실행
    └ 예약 변경 성공 이벤트 발행
4️⃣ 결제 생성 트랜잭션 실행
    └ 결제 생성 성공 이벤트 발행
5️⃣ (❌ 실패 시) 각 서비스에서 보상 트랜잭션 실행
    ├─ 예약 실패 → 포인트 롤백
    ├─ 결제 실패 → 포인트 롤백 + 예약 롤백
```

```java
//각각의 서비스는 eventPublisher를 주입받음
//Publisher , Listener 의 파라미터는 생략
@Service
class 포인트Service{ 
    @Transactional
    포인트차감() {
        try {
            //포인트차감 후
            포인트차감성공이벤트발행();
        } catch (Exception e){
            포인트차감실패이벤트발행();
        }
    }
}

@Service
class 예약Service{
    @Transactional
    예약정보변경(){
        try {
            예약정보변경성공이벤트발행();
        } catch (Exception e){
            예약정보변경실패이벤트발행();
        }
    }
}

@Service
class 결제Service{
    @Transactional
    결제생성(){
        try {
            //결제 처리
        } catch (Exception e){
            결제생성성공이벤트발행();
        }
    }
}

//ApplicationEventPublisher 생성자 주입
@Component
class 포인트이벤트Publisher(){
    
    포인트차감성공이벤트발행(){
        예약정보변경();
    }
    포인트차감실패이벤트발행();
}

//ApplicationEventPublisher 생성자 주입
@Component
class 예약이벤트Publisher(){

    예약정보변경성공이벤트발행(){
        결제생성();
    }
    예약정보변경실패이벤트발행();
}

//ApplicationEventPublisher 생성자 주입
@Component
class 결제이벤트Publisher(){

    결제생성성공이벤트발행(){
        //다음로직 실행 알림 등등~~
    }
    결제생성실패이벤트발행();
}

//결제이벤트Publisher 생성자 주입
@EventListener
class 포인트이벤트Listener(){


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    포인트차감성공이벤트handle(){
      예약정보변경();
    }
    
    포인트복구이벤트handle(){
        //별도로 복구할 데이터가 없음
        에러발생();
    }
}

//결제이벤트Publisher 생성자 주입
@EventListener
class 예약이벤트Listener(){

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    예약정보변경성공이벤트handle(){
        결제생성();
    }
    예약정보변경실패이벤트handle(){
        포인트롤백();
    }
}

//결제이벤트Publisher 생성자 주입
@EventListener
class 결제이벤트Listener(){

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    결제생성성공이벤트handle(){
      //다음로직 실행 알림 등등~~
    }
    결제생성실패이벤트handle(){
        포인트롤백();
        예약상태롤백();
    }
}
```

## 정리

- 이번 학습에서는 Spring에서 제공하는 Event Pub/Sub 기반으로 분산 트랜잭션을 SAGA 패턴 중 Choreography 패턴으로 설계하였다.
- 이벤트를 이용하여 서비스 간의 결합도를 낮추고, 서비스는 이벤트에게 자신의 수행 결과를 알리기만 하면 된다.
- 각 서비스의 트랜잭션이 독립적으로 실행되며, 이벤트를 통해 다음 서비스로 트랜잭션이 이어짐.
- 실패 시 보상 트랜잭션을 실행하여, 이전 트랜잭션을 롤백할 수 있도록 구현하였다.
- 더 나은 성능을 위해 Redis나 Kafka와 같은 메시지 브로커를 활용할 수도 있지만, Spring의 ApplicationEventPublisher만으로도 기본적인 이벤트 기반 분산 트랜잭션 처리가 가능하다.
