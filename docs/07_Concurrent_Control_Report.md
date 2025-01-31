# Concurrent Control Report


### 목차

1. 동시성 제어란?
2. 동시성 제어 기법
    1. Application Level
    2. DB Level
    3. Redis Distributed Lock
    4. Kafka
3. 동시성 이슈 발생 시나리오와 해결방법
    1. 포인트 충전
    2. 결제
    3. 좌석 예약
  
---
<br>

## 동시성 제어란?

동시성 제어란, 서비스 운영 중 여러 쓰레드가 동일한 자원에 접근하여 사용할 때 발생할 수 있는 이슈들을 관리하고 해결하는 기술적 방법을 의미합니다. 

이는 자원 충돌, 데이터 불일치, 중복 처리 등으로 인해 서비스의 안정성과 신뢰성이 저하되는 문제를 방지하기 위해 필수적입니다.

예를 들어, 명절 기차표 예매 서비스에서는 많은 사용자가 동시에 같은 좌석에 예매를 시도하는 경우가 발생합니다. 

만약 동시성 제어가 구현되지 않은 경우, 여러 사용자(쓰레드)가 동일한 기차표 좌석(자원)를 성공적으로 예매하는 상황이 발생할 수 있습니다. 

이는 자원의 중복 할당 문제를 야기하며, 서비스 사용자들에게 혼란을 줄 뿐만 아니라 기업의 신뢰도에도 부정적인 영향을 미칠 수 있습니다.

---
<br>

## 동시성 제어 기법

동시성 제어 기법은 여러가지가 존재하며, 크게 3가지 정도로 구분 지을 수 있다.

### 1. Application Level
어플리케이션 레벨의 동시성 제어는 하나의 서버(노드) 내에서 동시성을 제어하는 방법이다.
대표적인 제어 방법으로는 `synchronized`, `concurrentHashMap` 가 있다.

- #### `synchronized`
    - `Java` 에서 제공하는 동시성 제어 라이브러리 메서드 이며, 해당 키워드를 메서드 또는 블록 단위로 선언할수있다.
    - 해당 키워드가 선언된 Scope 내에서는 해당 메서드 또는 블록이 종료되기 전까지 다른 스레드가 실행하지 못한다.
    - 장점
        - 구현 방법이 간편하다.
    - 단점
        - 하나의 스레드가 하나의 메서드 또는 블록을 실행하기 때문에, 성능이 낮으며 분산환경에서 적용이 제한적이다.

- #### `ConcurrentHashMap`
    - `Java` 에서 제공하는 라이브러리 객체 이며, Thread-safe 하며 다중 스레드에서 사용이 가능하다.
    - `ConcurrentHashMap`은 구현체 내 Entry 별로 락을 걸어 멀티 스레드 환경에서 동시성 제어가 가능하다.
    - 장점
        - 구현 방법이 간편하며, `synchronized` 보다 성능이 좋다.
    - 단점
        - 어플리케이션 환경에서만 사용되는 객체이기 때문에, 분산환경에 적용이 제한적이다.

- #### `Optimistic Lock (낙관적 락)`
    - 동시성 이슈가 많이 발생하지 않을 것으로 낙관적으로 가정하는 제어 기법
    - 자원에 `Lock`을 걸지 않고, `Version`을 관리하여 트랜잭션 간 동시에 동일한 자원 수정이 발생하였을 때 조회 시 읽어온 `Version`이 일치하는 지 검증 후 처리 한다.
    - 장점
        - 동시성 이슈가 많이 발생하지 않는다는 가정하에, 별도의 DB 데이터에 `Lock`을 걸지 않기 때문에 성능적 이점을 가져갈 수 있다.
    - 단점
        - `Version` 불 일치로 인한, Update실패 시 재 시도 로직을 직접 구현해야하며, 낙관적 락 적용한 로직에 동시성 이슈가 빈번하게 발생 시에, 롤백 및 재시도가 발생하기 때문에 성능이 저하될 수 있다.

### 2. Database Level

- #### `Pessimistic Lock(비관적 락)`
    - 동시성 이슈가 많이 발생할 것으로 비관적으로 가정하고, 이를 방지하기 위해 자원에 락을 설정하는 제어 기법
    - `Shared Lock(공유 락)`: 락에 걸린 자원에 대해서 여러 트랜잭션이 **읽기는 가능**하고, **쓰기는 불가능**한 `Lock`
    - `Exclusive Lock(배타 락)`: 락에 걸린 자원에 대해서 여러 트랜잭션이 **읽기와 쓰기 모두 불가능**한 `Lock`
    - 장점
        - 자원에 락을 설정함으로써, 다른 트랜잭션의 동시 수정을 차단하여 데이터의 일관성과 무결성을 보장하는 데 유리하다.
        - 동시 수정으로 인한 충돌을 방지하여 데이터 무결성을 유지할 수 있다.
    - 단점
        - 자원에 락이 설정된 동안, 다른 트랜잭션은 해당 자원에 접근하지 못하고 대기 상태로 전환됨에 따라, 동시성 처리량이 낮아지고 시스템 성능이 저하될 수 있다.
        - 여러 트랜잭션이 서로 다른 자원을 잠그고, 서로의 락 해제를 기다리는 상황에서 데드락(교착상태)이 발생할 수 있다.

### 3. Redis Distributed Lock(레디스를 이용한 분산락)
- `Distributed Lock`은 분산 환경(다수의 서버 또는 프로세스)에서 공유 자원에 동시에 접근할 떄, 효율적으로 일관된 락을 제공하기 위한 동시성 제어 기법
- `Redis Client`는 대표적으로 `Redisson`,`Lettuce`가 있다.
    - `Redisson`
        - `RLock`이라는 락을 위한 인터페이스를 제공한다.
        - `RLock`은 Pub/Sub 기반으로 `Lock`을 관리한다. (Message Broker)
            - `Lock을 점유하고 있는 Thread`가 `Lock 해제`를 알려주고, `다음 순번의 Thread가 Lock`을 획득을 시도 방식이다.
        - `RLock` 인터페이스를 통해, Lock 획득 대기시간, 점유시간과 같은 설정이 간편한다.
        - `Spin Lock`방식에 비해 `pub/sub 방식`으로 `Redis`부하가 덜 하다.
    - `Lettuce`
        - 분산락 기능을 제공하지 않기 때문에 직접 `SETNX 명령어`를 통해, 직접 락을 구현해야 한다. (Set if Not exist 값이 존재하지 않을 경우 Set 시킨다.)
        - `Lock`을 획득하지 못하는 경우, 획득할 때까지 계속해서 요청을 보내는 `Spin Lock`으로 구현할 경우 반복된 요청으로 `Redis`에 부하가 발생할 수 있다.
- 장점
    - 분산 환경에서도, 하나의 `Redis`서버가 `Lock`에 대한 관리를 하기 때문에, 원자적 연산이 가능하다.
    - `Redis`는 `InMemory DB`로 매우 빠른 읽기 쓰기 속도를 제공하여 보다 빠른 Lock 획득 및 해제가 가능하다.
- 단점
    - `Redis`서버가 다운될 경우, 해당 서버가 장애 혹은 다운 되는 경우 전체 데이터의 일관성과 무결성이 깨질 수 있다.
        - 이로인한 복구가 어려울 수 있다.
    - `DB Level` 또는 `Application Level`의 동시성 제어 보다 구현이 복잡하다.

### 4. Kafka
- `Apache Kafka`는 분산 스트리밍 플랫폼으로, 실시간 데이터 처리와 로그 관리, 메시지 브로커 역할을 수행한다.
- 높은 처리량, 확장성, 내구성을 제공하며, 주로 이벤트 기반 아키텍쳐와 실시간 데이터 스트림 처리에 사용된다.
- `Pub/Sub` 모델의 메시지 큐 형태로 동작하며, 분산환경에 특화되어 있다.
- Kafka 구성요소
    - 토픽
        - 데이터가 저장되는 논리적 단위
    - 파티션
        - 토픽은 여러 파티션으로 나뉘며, 각 파티션은 메시지 순서를 보장
        - 파티션은 데이터 병렬 처리를 가능하게 하여 높은 처리량을 제공
    - 브로커
        - `Kafka` 클러스터의 구성요소로, 데이터를 저장하고 프로듀서 / 컨슈머와 통신
    - 컨슈머 그룹
        - 같은 그룹에 속한 컨슈머는 서로 다른 파티션을 할당받아 병렬 처리
        - 한 컨슈머 그룹은 특정 메시지를 한 번만 처리하도록 보장
    - 프로듀서
        - 데이터를 `Kafka`로 보내는 클라이언트
    - 컨슈머
        - `Kafka`에서 데이터를 읽는 클라이언트
- 장점
    - 높은 병렬처리 능력으로 빠른 성능
    - 데이터 내구성 보장
    - 높은 확장성
- 단점
    - 높은 구현 비용
    - `Redis 분산락`과 마찬가지로, `Kafka` 클러스터가 장애 시, 시스템 전체에 영향을 줄 수 있다.

---
<br>

## 동시성 이슈 발생 시나리오와 해결방법
> **참고 사항**
> - 컴퓨터 고성능 이슈로 인하여, 성능 테스트의 수치를 가시화하기 위해서 극적인 환경으로 테스트를 진행하였습니다.
> - DBCP: 100
> - 예약,결제 ThreadCount:1000
> - 포인트 충전 ThreadCount: 100
> - 분산락 락 획득 대기시간 10초 (테스트 시간 내 무조건 대기 후 획득을 위해)
### 1. 포인트 충전

#### 동시성 이슈 발생 시나리오 및 예측/분석
```text
- 계정을 공유하여, 포인트를 충전해서사용하는 경우 한 계정에 여러 사용자가 포인트를 충전할 수 있다.
- 그러므로, 동시에 여러 포인트 충전 요청의 경우 모두 성공하여야 한다.
```
- #### 분석
  - 포인트 충전의 경우,모든 요청이 성공해야만 함으로 낙관적 락을 사용하여 성공할 때 까지 재시도 하는 것보다 요청별로 락을 획득하고, 반환하는 형태가 적합해보인다.
  - 낙관적락 사용 시 Version 충돌로 인하여, 불 필요하게 많은 재시도가 발생하고 그 과정에서 성능이 저하 될 수 있다.

#### 성능 및 효율성 테스트
- 소요시간: 낙관적락 > 분산락 > 비관적락

![PointChargeTest](https://github.com/user-attachments/assets/e99b6219-bb7b-4f61-bda6-df3f427eda08)


### 2. 결제

#### 동시성 이슈 발생 시나리오 및 분석
```text
- 예상치 못한 더블클릭이나 사용자의 여러 요청이 존재할 수 있다.
- 동시성 문제 발생 시 하나의 결제 요청만 성공해야만 한다.
```
- ##### 분석
  - 비관적락,분산락과 같이 락을 점유하여 다음 요청이 대기하는 것 보다 낙관적락을 사용하여, 하나의 요청이 성공하면 이후 요청은 재시도하지 않고 실패하도록 하는 것이 적합해 보인다.
  - 비관적락,분산락 사용 시 락을 획득하기 위한 시간 소요로 인한 성능이 저하 될 수 잇다.


#### 성능 및 효율성 테스트
- 소요시간: 분산락 > 비관적락 > 낙관적락

![PaymentTest](https://github.com/user-attachments/assets/e3e423e6-1292-42eb-b5f3-8aff13514e62)

### 3. 좌석 예약

#### 동시성 이슈 발생 시나리오 및 분석
```text
- 예상치 못한 더블클릭이나 사용자의 여러 요청이 존재할 수 있다.
- 동시성 문제 발생 시 하나의 좌석 예약 요청만 성공해야 한다.
```

- ##### 분석
    - 비관적락,분산락과 같이 락을 점유하여 다음 요청이 대기하는 것 보다 낙관적락을 사용하여, 하나의 요청이 성공하면 이후 요청은 재시도하지 않고 실패하도록 하는 것이 적합해 보인다.
    - 비관적락,분산락 사용 시 락을 획득하기 위한 시간 소요로 인한 성능이 저하 될 수 잇다.

#### 성능 및 효율성 테스트
- 소요시간: 분산락 > 비관적락 > 낙관적락

![ReservationTest](https://github.com/user-attachments/assets/9d96402e-0f9d-4c38-8ee6-f8cd50bc5918)

### 결론 및 정리
- 낙관적 락
  - 동시에 발생한 모든 요청이 성공해야하는 경우 가장 높은 성능을 보였다.
- 비관적 락
  - 동시에 발생한 요청 중 일부 또는 하나만 성공해야하는 경우 가장 높은 성능을 보였다.
- 레디스 분산락(Redisson)
  - 모든 테스트에서 가장 낮은 성능을 보였다.
    - Application과 Redis와의 통신 시간(비용) 그리고 Redisson의 Pub/Sub방식을 통한 대기시간 소요
  - 서비스 구조가 분산환경일 경우 적합하다.

분산락의 경우 아래와 같이 Redis Lock을 획득하고 해제해야 다음 요청이 다시 Lock을 획득하고 해제하는 것을 확인할 수 있다.
여기서 말하는 Lock은 DB락이 아닌, Redis의 RLock이다.

```text
2025-01-23T15:35:03.776Z  INFO 7450 --- [hhplus] [ool-2-thread-91] k.h.b.s.s.a.RedisDistributedLockAspect   : Lock 획득 성공 = LOCK:chargePoint:1
2025-01-23T15:35:03.815Z  INFO 7450 --- [hhplus] [ool-2-thread-91] k.h.b.s.application.facade.PointFacade   : userId Of Point =1
2025-01-23T15:35:03.822Z  INFO 7450 --- [hhplus] [ool-2-thread-91] k.h.b.s.s.a.RedisDistributedLockAspect   : Lock 해제 성공 = LOCK:chargePoint:1
2025-01-23T15:35:03.831Z  INFO 7450 --- [hhplus] [ool-2-thread-69] k.h.b.s.s.a.RedisDistributedLockAspect   : Lock 획득 성공 = LOCK:chargePoint:1
2025-01-23T15:35:03.834Z  INFO 7450 --- [hhplus] [ool-2-thread-69] k.h.b.s.application.facade.PointFacade   : userId Of Point =1
2025-01-23T15:35:03.836Z  INFO 7450 --- [hhplus] [ool-2-thread-69] k.h.b.s.s.a.RedisDistributedLockAspect   : Lock 해제 성공 = LOCK:chargePoint:1
2025-01-23T15:35:03.837Z  INFO 7450 --- [hhplus] [ool-2-thread-59] k.h.b.s.s.a.RedisDistributedLockAspect   : Lock 획득 성공 = LOCK:chargePoint:1
2025-01-23T15:35:03.840Z  INFO 7450 --- [hhplus] [ool-2-thread-59] k.h.b.s.application.facade.PointFacade   : userId Of Point =1
2025-01-23T15:35:03.842Z  INFO 7450 --- [hhplus] [ool-2-thread-59] k.h.b.s.s.a.RedisDistributedLockAspect   : Lock 해제 성공 = LOCK:chargePoint:1
2025-01-23T15:35:03.843Z  INFO 7450 --- [hhplus] [ool-2-thread-98] k.h.b.s.s.a.RedisDistributedLockAspect   : Lock 획득 성공 = LOCK:chargePoint:1
2025-01-23T15:35:03.845Z  INFO 7450 --- [hhplus] [ool-2-thread-98] k.h.b.s.application.facade.PointFacade   : userId Of Point =1
2025-01-23T15:35:03.847Z  INFO 7450 --- [hhplus] [ool-2-thread-98] k.h.b.s.s.a.RedisDistributedLockAspect   : Lock 해제 성공 = LOCK:chargePoint:1
2025-01-23T15:35:03.848Z  INFO 7450 --- [hhplus] [ool-2-thread-43] k.h.b.s.s.a.RedisDistributedLockAspect   : Lock 획득 성공 = LOCK:chargePoint:1
2025-01-23T15:35:03.851Z  INFO 7450 --- [hhplus] [ool-2-thread-43] k.h.b.s.application.facade.PointFacade   : userId Of Point =1
2025-01-23T15:35:03.853Z  INFO 7450 --- [hhplus] [ool-2-thread-43] k.h.b.s.s.a.RedisDistributedLockAspect   : Lock 해제 성공 = LOCK:chargePoint:1
2025-01-23T15:35:03.853Z  INFO 7450 --- [hhplus] [ool-2-thread-13] k.h.b.s.s.a.RedisDistributedLockAspect   : Lock 획득 성공 = LOCK:chargePoint:1
2025-01-23T15:35:03.855Z  INFO 7450 --- [hhplus] [ool-2-thread-13] k.h.b.s.application.facade.PointFacade   : userId Of Point =1
2025-01-23T15:35:03.858Z  INFO 7450 --- [hhplus] [ool-2-thread-13] k.h.b.s.s.a.RedisDistributedLockAspect   : Lock 해제 성공 = LOCK:chargePoint:1
```

---
