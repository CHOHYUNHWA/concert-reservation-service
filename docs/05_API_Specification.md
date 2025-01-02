# 콘서트 예약 서비스 API 명세서

## 1. 대기열 토큰 발급

### 설명

- 대기열 큐에 유저를 추가하고 토큰을 발급한다.

### Request

- URI : `/api/queue/token`
- Method : POST
- Headers :
    - `Content-Type` : application/json
- Body :
```json
{
  "userId" : 1
}
```

### Response

```json
{
  "token": "7b3366bc-6c19-41d8-a97f-6ac312479aa9",
  "createdAt" : "2025-01-01 00:00:00",
  "expiredAt" : "2025-01-01 00:10:00"
}
```

- **Response**:
    - `token`: String (토큰 UUID)
    - `createdAt`: DateTime (생성 시각)
    - `expiredAt`: DateTime (만료 시각)

### Error
```json
{
  "code" : 404,
  "message" : "User Not Found"
}
```

## 2. 대기열 상태 조회

### 설명

- 대기열 상태를 조회한다.

### Request

- URI : `/api/queue/status?userId={userId}`
- Method : GET
- Query Params :
    - `userId` : Long (유저 ID)
- Headers :
    - `Token` : String (유저 토큰 UUID)

### Response

```json
{
  "status" : "WAITING",
  "remainingQueueCount" : 5
}
```
- **Response**:
    - `status`: Enum (`WAITING` : 대기)
    - `remainingQueueCount`: Long (남은 대기열 수)

### Error
```json
{
  "code" : 404,
  "message" : "User Not Found"
}
```
```json
{
  "code" : 401,
  "message" : "Invalid Token"
}
```

## 3. 콘서트 예약 가능 일정 조회

### 설명

- 콘서트 예약 가능 일정을 조회

### Request

- URI : `/api/concerts/{concertId}/schedule`
- Method : GET
- Path Variable :
    - `concertId` : Long (콘서트 ID)
- Headers :
    - `Token` : String (유저 토큰 UUID)

### Response

```json
{
  "concertId" : 1,
  "schedule" : [
    {
      "scheduleId" : 1,
      "concertTime" : "2025-01-02 00:00:00",
      "availableReservationTime" : "2025-01-02 23:00:00"
    }
  ]
}
```
- **Response**:
    - `concertId`: Long (콘서트 ID)
    - `schedule`: List (콘서트 스케쥴 리스트)
        - `scheduleId`: Long (콘서트 스케쥴 ID)
        - `concertTime`: LocalDateTime (콘서트 시간)
        - `availableReservationTime`: LocalDateTime (예약 가능 마감 시간)

### Error
```json
{
  "code" : 401,
  "message" : "Invalid Token"
}
```
```json
{
  "code" : 404,
  "message" : "Concert Not Found"
}
```


## 4. 콘서트 예약 가능 좌석 조회

### 설명

- 콘서트 예약 가능 좌석을 조회

### Request

- URI : `/api/concert/{concertId}/schedule{concertScheduleId}/seats`
- Method : GET
- Path Variable :
    - `concertId` : Long (콘서트 ID)
    - `concertScheduleId` : Long (스케쥴 ID)
- Headers :
    - `Token` : String (유저 토큰 UUID)

### Response

```json
{
  "concertId": 1,
  "concertTime": "2025-01-02 00:00:00",
  "totalSeats": 50,
  "seats": [
    {
      "seatId": 1,
      "seatNumber" : 1,
      "status" : "AVAILABLE",
      "seatPrice" : 100000
    },{
      "seatId": 2,
      "seatNumber" : 2,
      "status" : "AVAILABLE",
      "seatPrice" : 100000
    }
  ]
}
```
- **Response**:
    - `concertId`: Long (콘서트 ID)
    - `concertTime`: LocalDateTime (콘서트 시간)
    - `totalSeats`: Long (총 좌석 수)
    - `seats`: List (좌석 목록)
        - `seatId`: Long (좌석 ID)
        - `seatNumber`: Long (좌석 번호)
        - `status`: Enum (`AVAILABLE` : 예약 가능)
        - `seatPrice`: Long (좌석 가격)

### Error
```json
{
  "code": 401,
  "message": "Invalid Token"
}
```
```json
{
  "code" : 404,
  "message" : "User Not Found"
}
```
```json
{
  "code" : 404,
  "message" : "Concert Not Found"
}
```
```json
{
  "code" : 404,
  "message" : "Concert Schedule Not Found"
}
```

## 5. 콘서트 예약

### 설명

- 콘서트를 예약

### Request

- URI : `/api/consert/reservation`
- Method : POST
- Headers :
    - `Token` : String (유저 토큰 UUID)
    - `Content-Type` : application/json
- Body :
```json
{
  "userId" : 1,
  "concertId" : 1,
  "concertScheduleId" : 1,
  "seatId" : 1
}
```
* userId : Long (유저 ID)
* concertId : Long (콘서트 ID)
* concertScheduleId : Long (콘서트 일정 ID)
* seatId : Long (좌석 ID)

### Response
```json
{
  "reservationId" : 1,
  "concertId" : 1,
  "title": "콘서트",
  "concertTime" : "2025-01-03 00:00:00",
  "seat" : [
    {
      "seatNumber" : 1, 
      "seatPrice" : 100000
    }
  ],
  "totalPrice" : 100000,
  "reservationStatus" : "PAYMENT_WAITING"
}
```
- **Response**:
    - `reservationId`: Long (예약 ID)
    - `concertId`: Long (콘서트 ID)
    - `title`: String (콘서트 이름)
    - `concertTime`: LocalDateTime (콘서트 시간)
    - `seat`: List (좌석 목록)
        - `seatNumber`: Long (좌석 번호)
        - `seatPrice`: Long (좌석 가격)
    - `totalPrice`: Long (총 가격)
    - `reservationStatus`: Enum (`PAYMENT_WAITING` : 결제 대기)

### Error
```json
{
  "code": 401,
  "message": "Invalid Token"
}
```
```json
{
  "code" : 404,
  "message" : "User Not Found"
}
```
```json
{
  "code" : 404,
  "message" : "Concert Not Found"
}
```
```json
{
  "code" : 404,
  "message" : "Concert Schedule Not Found"
}
```
```json
{
  "code" : 404,
  "message" : "Seat Not Found"
}
```
```json
{
  "code" : 500,
  "message" : "Reservation Failed"
}
```

## 6. 결제

### 설명

- 콘서트 좌석 예매 결제

### Request

- URI : `/api/concert/payment`
- Method : POST
- Headers :
    - `Token` : String (유저 토큰 UUID)
    - `Content-Type` : application/json
- Body :
```json
{
  "userId" : 1,
  "reservationId" : 1
}
```
* userId : Long (유저 ID)
* reservationId : Long (예약 ID)

### Response

```json
{
  "paymentId" : 1,
  "amount" : 100000,
  "paymentStatus" : "COMPLETED"
}
```
- **Response**:
    - `paymentId`: Long (결제 ID)
    - `amount`: Long (결제 금액)
    - `paymentStatus`: Enum (`COMPLETED` : 완료됨)

### Error
```json
{
  "code" : 400,
  "message" : "Not Enough Point"
}
```
```json
{
  "code": 401,
  "message": "Invalid Token"
}
```
```json
{
  "code" : 404,
  "message" : "User Not Found"
}
```
```json
{
  "code" : 404,
  "message" : "Reservation Not Found"
}
```
```json
{
  "code" : 500,
  "message" : "Payment Failed"
}
```

## 7. 잔액 충전

### 설명

- 유저의 잔액을 충전

### Request

- URI : `/api/users/{userId}/point`
- Method : PATCH
- Headers :
    - `Content-Type` : application/json
- Path Variable :
    - `userId` : Long (유저 ID)

- Body :
```json
{
  "amount" : 100000
}
```
- amount : Long (충전할 금액)

### Response

```json
{
  "userId" : 1,
  "currentAmount" : 100000
}
```
- **Response**:
    - `userId`: Long (유저 ID)
    - `currentAmount`: Long (충전 후 금액)

### Error
```json
{
  "code" : 400,
  "message" : "Invalid Charge Amount"
}
```
```json
{
  "code" : 404,
  "message" : "User Not Found"
}
```
```json
{
  "code" : 500,
  "message" : "Charge Failed"
}
```

## 8. 잔액 조회

### 설명

- 유저의 잔액을 조회

### Request

- URI : `/api/users/{userId}/point`
- Method : GET
- Headers :
    - `Content-Type` : application/json
- Path Variable :
    - `userId` : Long (유저 ID)

### Response

```json
{
  "userId" : 1,
  "currentAmount" : 100000
}
```
- **Response**:
    - `userId`: Long (유저 ID)
    - `currentAmount`: Long (충전 후 금액)

### Error
```json
{
  "code" : 404,
  "message" : "User Not Found"
}
```
