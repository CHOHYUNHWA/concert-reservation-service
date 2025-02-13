# Database Indexing을 통한 서비스 성능 개선

## Index 란?

### 인덱스는 **테이블의 특정 컬럼을 정렬된 상태로 저장**하여 **검색 속도를 향상**시킬 수 있는 자료 구조
- 단일 컬럼 또는 여러 개의 컬럼(복합 인덱스)으로 인덱스를 설정할 수 있다.
- 컬럼별 정렬 기준(오름차순/내림차순)을 설정하여 검색 최적화가 가능하다.

### 인덱스를 사용하는 이유
- **검색 성능 향상**: 테이블 전체를 스캔하는 **Full Table Scan**을 피하고, **Index Scan**을 통해 원하는 데이트럴 빠르게 찾을 수 있다.
- **조회 성능 최적화**: 잦은 검색이 발생하는 컬럼에 인덱스를 설정하면, 불필요한 연산을 줄여 성능을 개선할 수 있다.
- **정렬 속도 개선**: ORDER BY 절을 자주 사용하는 경우, 인덱스를 활용하여 정렬 속도를 빠르게 할 수 있다.

### 인덱스의 종류
- Clustered Index(물리적인 테이블 정렬 순서)
  - 설명
    - 테이블 자체의 정렬 순서를 결정하는 인덱스
    - 테이블 데이터를 Clustered Index 컬럼 기준으로 정렬하여 저장
    - 실제 테이블 데이터 자체가 인덱스처럼 동작
  - 특징
    - 한 테이블당 하나만 가짐
    - Primary Key(PK)를 생성하면 자동으로 Clustered Index가 됨 (MySQL InnoDB 기준)
      - Oracle, PostgreSQL등 타 DB는 아예 지원하지 않거나, 별도로 지정해주어야함
- Non-Clustered Index
  - 설명
    - 데이터와 별도로 저장되는 정렬된 인덱스
    - 실제 테이블 데이터와 물리적으로 분리된 인덱스 구조를 가짐
    - 테이블 데이터 자체는 정렬되지 않지만, 인덱스만 정렬된 상태로 유지됨
  - 특징
    - 한 테이블에 여러개의 인덱스를 가질 수 있음
    - 인덱스에는 컬럼 값과 함께 해당 데이터의 위치(레코드 위치, ROWID 또는 PK)가 저장됨
    - CREATE INDEX를 통해 생성된 인덱스는 기본적으로 모두 Non-Clustered Index
    - 별도의 저장공간이 필요하다.
    - 테이블에 데이터가 삽입, 수정, 삭제될 때 추가적인 유지 비용(오버헤드)이 발생함(테이블 데이터 수정과 함께 인덱스 수정도 일어나기 때문)

### 대표적인 인덱스 동작 방식

대부분의 RDBMS에서는 **B-Tree** 기반 인덱스를 기본적으로 사용하며,
**Hash, Full-Text, Bitmap 인덱스** 등은 특정 데이터베이스에서만 제공되거나 별도 설정이 필요하다.

- **B-Tree*(Balanced Tree)* 인덱스 기반 검색
  - 설명
    - B-Tree는 트리(Tree) 구조를 이용하여 데이터를 정렬된 상태로 저장하는 인덱스 방식
    - 검색 시 이진 탐색(Binary Search)을 수행하여 데이터를 빠르게 찾음
    - 대부분의 RDBMS(MySQL, Oracle, PostgreSQL등)에서 기본적으로 사용
  - 특징
    - 빠른 검색 속도를 가지며 **O(log N)의 시간 복잡도**로 데이터를 조회 가능
    - **WHERE 조건 (=, >, <, BETWEEN)**에서 성능 최적화 가능
    - **ORDER BY, GROUP BY, JOIN** 최적화에 유용
    - **데이터 변경(INSERT, UPDATE, DELETE)** 시 **B-Tree의 균형(정렬상태)를 유지**해야 하므로, 쓰기 성능에 영향을 줄 수 있음

### 인덱스 적용기준
인덱스 적용 기준을 위해서는, **카디널리티(Cardinality)**라는 개념에 대해서 알아야 한다.
- **카디널리티란(Cardinality)?**,
  - 데이터베이스에서 특정 컬럼(속성)에 저장된 **고유한 값의 개수(Unique Value Count)** 이다.
  - 쉽게 말해, **테이블의 특정 컬럼에 얼마나 다양한 값이 들어 있는지** 를 나타내는 개념
  - e.g) **카디널리티가 높을 수록 다양한 값이 많고** , **낮을수록 중복된 값이 많음** .

인덱스를 적용할때는 적용하려는 컬럼의 카디널리티를 고려하여야한다.

일반적으로, 카디널리티가 높은 기준으로 인덱스를 적용하면 좋다.
```text
e.g)콘서트 예약 시스템에 유저 정보가 있다고 가정해보자.
1) 유저 정보에는 `ID, 이름, 나이, 휴대폰번호, 성별 정보`를 가지고 있다.
2) 해당 테이블 컬럼의 카디널리티가 높은 순서는 일반적으로 ID, 휴대폰번호 > 이름 > 나이 > 성별 순 이라고 할 수 있다.
3) 일반적으로 카디널리티가 높은 ID나 휴대폰번호를 인덱스에 추가하고 해당 컬럼을 WHERE 조건에 설정하였을 때 높은 조회 성능을 기대할 수 있다.
```

- 인덱스 적용 기준
  - 자주 사용되는 검색 조건 컬럼(WHERE , < , >, BETWEEN, JOIN, ORDER BY, GROUP BY 등)에 적용되는 컬럼에 인덱스 적용
  - 일반적으로 중복이 적고, 선택도가 높은 컬럼을 인덱스로 지정(Feat. 카디널리티)
  - 조회 성능이 중요한 테이블에서 사용
  - 자주 변경되지 않는 컬럼(INSERT, UPDATE, DELETE)에 적용

### 인덱스 적용 시 주의 사항
- 인덱스를 적용 할 때, **반드시 카디널리티가 높은 순으로 인덱스를 적용해야 하는 것은 아니다.**
- 쿼리의 특성과 검색 조건에 따라 카디널리티가 높은 컬럼이 우선되어야 할 수도 있고, 그렇지 않을 수도 있다.
- 인덱스 컬럼의 값과 타입을 그대로 사용해야한다.
  - PRICE 컬럼 인덱스 적용
    - WHERE PRICE > 10000 / 100 -> 인덱스 조회
    - WHERE PRICE * 100 > 10000 -> 인덱스 미 조회
- WHERE절에서 BETWEEN, > , < 과 같은 범위 연산 다음 컬럼은 인덱스를 조회 하지 않는다.
  - PRODUCT_ID, ORDERED_AT, STATUS 컬럼 복합 인덱스 적용
    - WHERE PRODUCT_ID = ? AND ORDERED_AT > ? AND STATUS = ?
      - 비교연산 다음 동등연산의 STATUS는 인덱스로 등록되어있으나, STATUS 조건을 조회할 떄는 인덱스를 조회 하지 않는다.
      - ID, DATETIME과 같이 카디널리티가 높은 컬럼 이후에는 복합 인덱스를 추가하여도 의미없는 인덱스가 될 확률이 매우 높다.

[//]: # (### 서비스 로직 Index 적용 효율성 테스트)

[//]: # ()
[//]: # ()
[//]: # (인덱스는 저장된 데이터의 컬럼에 카디널리티와 범위에 따라서, 최적의 인덱스 컬럼이 달라진다.)

[//]: # ()
[//]: # ()
[//]: # (<details>)

[//]: # (<summary>스키마 및 데이터</summary>)

[//]: # ()
[//]: # (```mysql)

[//]: # (CREATE TABLE `order` &#40;)

[//]: # (    orderId INT PRIMARY KEY AUTO_INCREMENT,)

[//]: # (    productId INT NOT NULL,)

[//]: # (    orderCnt INT NOT NULL DEFAULT 1,)

[//]: # (    status ENUM&#40;'주문완료', '배송중', '배송완료', '취소'&#41; NOT NULL,)

[//]: # (    orderedAt DATETIME NOT NULL)

[//]: # (&#41;;)

[//]: # ()
[//]: # (-- INSERT * 20번 실행시켜 데이터 표본을 140 * 20 = 2800개로 한다.)

[//]: # (INSERT INTO `order` &#40;productId, orderCnt, status, orderedAt&#41; VALUES)

[//]: # (&#40;1, 1, '주문완료', '2025-02-05 00:00:00'&#41;,)

[//]: # (&#40;2, 1, '배송중', '2025-02-05 01:00:00'&#41;,)

[//]: # (&#40;3, 1, '배송완료', '2025-02-05 02:00:00'&#41;,)

[//]: # (&#40;4, 1, '취소', '2025-02-05 03:00:00'&#41;,)

[//]: # (&#40;5, 1, '주문완료', '2025-02-05 04:00:00'&#41;,)

[//]: # (&#40;6, 1, '배송중', '2025-02-05 05:00:00'&#41;,)

[//]: # (&#40;7, 1, '배송완료', '2025-02-05 06:00:00'&#41;,)

[//]: # (&#40;8, 1, '취소', '2025-02-05 07:00:00'&#41;,)

[//]: # (&#40;9, 1, '주문완료', '2025-02-05 08:00:00'&#41;,)

[//]: # (&#40;10, 1, '배송중', '2025-02-05 09:00:00'&#41;,)

[//]: # (&#40;11, 1, '배송완료', '2025-02-05 10:00:00'&#41;,)

[//]: # (&#40;12, 1, '취소', '2025-02-05 11:00:00'&#41;,)

[//]: # (&#40;13, 1, '주문완료', '2025-02-05 12:00:00'&#41;,)

[//]: # (&#40;14, 1, '배송중', '2025-02-05 13:00:00'&#41;,)

[//]: # (&#40;15, 1, '배송완료', '2025-02-05 14:00:00'&#41;,)

[//]: # (&#40;16, 1, '취소', '2025-02-05 15:00:00'&#41;,)

[//]: # (&#40;17, 1, '주문완료', '2025-02-05 16:00:00'&#41;,)

[//]: # (&#40;18, 1, '배송중', '2025-02-05 17:00:00'&#41;,)

[//]: # (&#40;19, 1, '배송완료', '2025-02-05 18:00:00'&#41;,)

[//]: # (&#40;20, 1, '취소', '2025-02-05 19:00:00'&#41;,)

[//]: # (&#40;1, 1, '주문완료', '2025-02-05 20:00:00'&#41;,)

[//]: # (&#40;2, 1, '배송중', '2025-02-05 21:00:00'&#41;,)

[//]: # (&#40;3, 1, '배송완료', '2025-02-05 22:00:00'&#41;,)

[//]: # (&#40;4, 1, '취소', '2025-02-05 23:00:00'&#41;,)

[//]: # (&#40;5, 1, '주문완료', '2025-02-06 00:00:00'&#41;,)

[//]: # (&#40;6, 1, '배송중', '2025-02-06 01:00:00'&#41;,)

[//]: # (&#40;7, 1, '배송완료', '2025-02-06 02:00:00'&#41;,)

[//]: # (&#40;8, 1, '취소', '2025-02-06 03:00:00'&#41;,)

[//]: # (&#40;9, 1, '주문완료', '2025-02-06 04:00:00'&#41;,)

[//]: # (&#40;10, 1, '배송중', '2025-02-06 05:00:00'&#41;,)

[//]: # (&#40;11, 1, '배송완료', '2025-02-06 06:00:00'&#41;,)

[//]: # (&#40;12, 1, '취소', '2025-02-06 07:00:00'&#41;,)

[//]: # (&#40;13, 1, '주문완료', '2025-02-06 08:00:00'&#41;,)

[//]: # (&#40;14, 1, '배송중', '2025-02-06 09:00:00'&#41;,)

[//]: # (&#40;15, 1, '배송완료', '2025-02-06 10:00:00'&#41;,)

[//]: # (&#40;16, 1, '취소', '2025-02-06 11:00:00'&#41;,)

[//]: # (&#40;17, 1, '주문완료', '2025-02-06 12:00:00'&#41;,)

[//]: # (&#40;18, 1, '배송중', '2025-02-06 13:00:00'&#41;,)

[//]: # (&#40;19, 1, '배송완료', '2025-02-06 14:00:00'&#41;,)

[//]: # (&#40;20, 1, '취소', '2025-02-06 15:00:00'&#41;,)

[//]: # (&#40;1, 1, '주문완료', '2025-02-06 16:00:00'&#41;,)

[//]: # (&#40;2, 1, '배송중', '2025-02-06 17:00:00'&#41;,)

[//]: # (&#40;3, 1, '배송완료', '2025-02-06 18:00:00'&#41;,)

[//]: # (&#40;4, 1, '취소', '2025-02-06 19:00:00'&#41;,)

[//]: # (&#40;5, 1, '주문완료', '2025-02-06 20:00:00'&#41;,)

[//]: # (&#40;6, 1, '배송중', '2025-02-06 21:00:00'&#41;,)

[//]: # (&#40;7, 1, '배송완료', '2025-02-06 22:00:00'&#41;,)

[//]: # (&#40;8, 1, '취소', '2025-02-06 23:00:00'&#41;,)

[//]: # (&#40;9, 1, '주문완료', '2025-02-07 00:00:00'&#41;,)

[//]: # (&#40;10, 1, '배송중', '2025-02-07 01:00:00'&#41;,)

[//]: # (&#40;11, 1, '배송완료', '2025-02-07 02:00:00'&#41;,)

[//]: # (&#40;12, 1, '취소', '2025-02-07 03:00:00'&#41;,)

[//]: # (&#40;13, 1, '주문완료', '2025-02-07 04:00:00'&#41;,)

[//]: # (&#40;14, 1, '배송중', '2025-02-07 05:00:00'&#41;,)

[//]: # (&#40;15, 1, '배송완료', '2025-02-07 06:00:00'&#41;,)

[//]: # (&#40;16, 1, '취소', '2025-02-07 07:00:00'&#41;,)

[//]: # (&#40;17, 1, '주문완료', '2025-02-07 08:00:00'&#41;,)

[//]: # (&#40;18, 1, '배송중', '2025-02-07 09:00:00'&#41;,)

[//]: # (&#40;19, 1, '배송완료', '2025-02-07 10:00:00'&#41;,)

[//]: # (&#40;20, 1, '취소', '2025-02-07 11:00:00'&#41;,)

[//]: # (&#40;1, 1, '주문완료', '2025-02-07 12:00:00'&#41;,)

[//]: # (&#40;2, 1, '배송중', '2025-02-07 13:00:00'&#41;,)

[//]: # (&#40;3, 1, '배송완료', '2025-02-07 14:00:00'&#41;,)

[//]: # (&#40;4, 1, '취소', '2025-02-07 15:00:00'&#41;,)

[//]: # (&#40;5, 1, '주문완료', '2025-02-07 16:00:00'&#41;,)

[//]: # (&#40;6, 1, '배송중', '2025-02-07 17:00:00'&#41;,)

[//]: # (&#40;7, 1, '배송완료', '2025-02-07 18:00:00'&#41;,)

[//]: # (&#40;8, 1, '취소', '2025-02-07 19:00:00'&#41;,)

[//]: # (&#40;9, 1, '주문완료', '2025-02-07 20:00:00'&#41;,)

[//]: # (&#40;10, 1, '배송중', '2025-02-07 21:00:00'&#41;,)

[//]: # (&#40;11, 1, '배송완료', '2025-02-07 22:00:00'&#41;,)

[//]: # (&#40;12, 1, '취소', '2025-02-07 23:00:00'&#41;,)

[//]: # (&#40;13, 1, '주문완료', '2025-02-08 00:00:00'&#41;,)

[//]: # (&#40;14, 1, '배송중', '2025-02-08 01:00:00'&#41;,)

[//]: # (&#40;15, 1, '배송완료', '2025-02-08 02:00:00'&#41;,)

[//]: # (&#40;16, 1, '취소', '2025-02-08 03:00:00'&#41;,)

[//]: # (&#40;17, 1, '주문완료', '2025-02-08 04:00:00'&#41;,)

[//]: # (&#40;18, 1, '배송중', '2025-02-08 05:00:00'&#41;,)

[//]: # (&#40;19, 1, '배송완료', '2025-02-08 06:00:00'&#41;,)

[//]: # (&#40;20, 1, '취소', '2025-02-08 07:00:00'&#41;,)

[//]: # (&#40;1, 1, '주문완료', '2025-02-08 08:00:00'&#41;,)

[//]: # (&#40;2, 1, '배송중', '2025-02-08 09:00:00'&#41;,)

[//]: # (&#40;3, 1, '배송완료', '2025-02-08 10:00:00'&#41;,)

[//]: # (&#40;4, 1, '취소', '2025-02-08 11:00:00'&#41;,)

[//]: # (&#40;5, 1, '주문완료', '2025-02-08 12:00:00'&#41;,)

[//]: # (&#40;6, 1, '배송중', '2025-02-08 13:00:00'&#41;,)

[//]: # (&#40;7, 1, '배송완료', '2025-02-08 14:00:00'&#41;,)

[//]: # (&#40;8, 1, '취소', '2025-02-08 15:00:00'&#41;,)

[//]: # (&#40;9, 1, '주문완료', '2025-02-08 16:00:00'&#41;,)

[//]: # (&#40;10, 1, '배송중', '2025-02-08 17:00:00'&#41;,)

[//]: # (&#40;11, 1, '배송완료', '2025-02-08 18:00:00'&#41;,)

[//]: # (&#40;12, 1, '취소', '2025-02-08 19:00:00'&#41;,)

[//]: # (&#40;13, 1, '주문완료', '2025-02-08 20:00:00'&#41;,)

[//]: # (&#40;14, 1, '배송중', '2025-02-08 21:00:00'&#41;,)

[//]: # (&#40;15, 1, '배송완료', '2025-02-08 22:00:00'&#41;,)

[//]: # (&#40;16, 1, '취소', '2025-02-08 23:00:00'&#41;,)

[//]: # (&#40;17, 1, '주문완료', '2025-02-09 00:00:00'&#41;,)

[//]: # (&#40;18, 1, '배송중', '2025-02-09 01:00:00'&#41;,)

[//]: # (&#40;19, 1, '배송완료', '2025-02-09 02:00:00'&#41;,)

[//]: # (&#40;20, 1, '취소', '2025-02-09 03:00:00'&#41;,)

[//]: # (&#40;1, 1, '주문완료', '2025-02-09 04:00:00'&#41;,)

[//]: # (&#40;2, 1, '배송중', '2025-02-09 05:00:00'&#41;,)

[//]: # (&#40;3, 1, '배송완료', '2025-02-09 06:00:00'&#41;,)

[//]: # (&#40;4, 1, '취소', '2025-02-09 07:00:00'&#41;,)

[//]: # (&#40;5, 1, '주문완료', '2025-02-09 08:00:00'&#41;,)

[//]: # (&#40;6, 1, '배송중', '2025-02-09 09:00:00'&#41;,)

[//]: # (&#40;7, 1, '배송완료', '2025-02-09 10:00:00'&#41;,)

[//]: # (&#40;8, 1, '취소', '2025-02-09 11:00:00'&#41;,)

[//]: # (&#40;9, 1, '주문완료', '2025-02-09 12:00:00'&#41;,)

[//]: # (&#40;10, 1, '배송중', '2025-02-09 13:00:00'&#41;,)

[//]: # (&#40;11, 1, '배송완료', '2025-02-09 14:00:00'&#41;,)

[//]: # (&#40;12, 1, '취소', '2025-02-09 15:00:00'&#41;,)

[//]: # (&#40;13, 1, '주문완료', '2025-02-09 16:00:00'&#41;,)

[//]: # (&#40;14, 1, '배송중', '2025-02-09 17:00:00'&#41;,)

[//]: # (&#40;15, 1, '배송완료', '2025-02-09 18:00:00'&#41;,)

[//]: # (&#40;16, 1, '취소', '2025-02-09 19:00:00'&#41;,)

[//]: # (&#40;17, 1, '주문완료', '2025-02-09 20:00:00'&#41;,)

[//]: # (&#40;18, 1, '배송중', '2025-02-09 21:00:00'&#41;,)

[//]: # (&#40;19, 1, '배송완료', '2025-02-09 22:00:00'&#41;,)

[//]: # (&#40;20, 1, '취소', '2025-02-09 23:00:00'&#41;,)

[//]: # (&#40;1, 1, '주문완료', '2025-02-10 00:00:00'&#41;,)

[//]: # (&#40;2, 1, '배송중', '2025-02-10 01:00:00'&#41;,)

[//]: # (&#40;3, 1, '배송완료', '2025-02-10 02:00:00'&#41;,)

[//]: # (&#40;4, 1, '취소', '2025-02-10 03:00:00'&#41;,)

[//]: # (&#40;5, 1, '주문완료', '2025-02-10 04:00:00'&#41;,)

[//]: # (&#40;6, 1, '배송중', '2025-02-10 05:00:00'&#41;,)

[//]: # (&#40;7, 1, '배송완료', '2025-02-10 06:00:00'&#41;,)

[//]: # (&#40;8, 1, '취소', '2025-02-10 07:00:00'&#41;,)

[//]: # (&#40;9, 1, '주문완료', '2025-02-10 08:00:00'&#41;,)

[//]: # (&#40;10, 1, '배송중', '2025-02-10 09:00:00'&#41;,)

[//]: # (&#40;11, 1, '배송완료', '2025-02-10 10:00:00'&#41;,)

[//]: # (&#40;12, 1, '취소', '2025-02-10 11:00:00'&#41;,)

[//]: # (&#40;13, 1, '주문완료', '2025-02-10 12:00:00'&#41;,)

[//]: # (&#40;14, 1, '배송중', '2025-02-10 13:00:00'&#41;,)

[//]: # (&#40;15, 1, '배송완료', '2025-02-10 14:00:00'&#41;,)

[//]: # (&#40;16, 1, '취소', '2025-02-10 15:00:00'&#41;,)

[//]: # (&#40;17, 1, '주문완료', '2025-02-10 16:00:00'&#41;,)

[//]: # (&#40;18, 1, '배송중', '2025-02-10 17:00:00'&#41;,)

[//]: # (&#40;19, 1, '배송완료', '2025-02-10 18:00:00'&#41;,)

[//]: # (&#40;20, 1, '취소', '2025-02-10 19:00:00'&#41;;)

[//]: # (```)

[//]: # (</details>)

[//]: # ()
[//]: # (5가지 복합 인덱스 조합으로 테스트)

[//]: # (```mysql)

[//]: # (-- 1. orderedAt, status)

[//]: # (CREATE INDEX idx_orderedAt_status ON `order` &#40;orderedAt, status&#41;;)

[//]: # (DROP INDEX idx_orderedAt_status ON `order`;)

[//]: # ()
[//]: # (-- 2. status, orderedAt)

[//]: # (CREATE INDEX idx_status_orderedAt ON `order` &#40;status, orderedAt&#41;;)

[//]: # (DROP INDEX idx_status_orderedAt ON `order`;)

[//]: # ()
[//]: # (-- 3. status, orderedAt, productId)

[//]: # (CREATE INDEX idx_status_orderedAt_productId ON `order` &#40;status, orderedAt, productId&#41;;)

[//]: # (DROP INDEX idx_status_orderedAt_productId ON `order`;)

[//]: # ()
[//]: # (-- 4. status, productId)

[//]: # (CREATE INDEX idx_status_productId ON `order` &#40;status, productId&#41;;)

[//]: # (DROP INDEX idx_status_productId ON `order`;)

[//]: # ()
[//]: # (-- 5. status, productId, orderedAt)

[//]: # (CREATE INDEX idx_status_productId_orderedAt ON `order` &#40;status, productId, orderedAt&#41;;)

[//]: # (DROP INDEX idx_status_productId_orderedAt ON `order`;)

[//]: # (```)







### 인덱스의 장단점
- 장점
  - 인덱스를 설정 시에 특정 조건의 조회 시 조회 성능을 폭발적으로 증가 시킬 수 있다.
    - WHERE, ORDER BY, GROUP BY, JOIN 등
    - B-Tree 기반 인덱스 시 O(log N)의 시간복잡도로 빠른 조회
    - 인덱스를 통한 데이터 필터링: Index Range Scan(범위 조회), Index Lookup(필터링)
- 단점
  - 너무 많은 인덱스 생성시 쓰기 성능이 감소한다.(Trade-Off 발생)
    - 하지만, 일반적으로 쓰기성능 감소로 인한 성능저하에 비하여 조회 성능 향상에 이점이 훨씬 큼
    - 데이터 INSERT,UPDATE,DELETE시 인덱스 정렬 유지를 위한 변경이 함께 발생하여 비용 발생

### MySql Index 설정
```mySql
-- 1. 단일 인덱스 등록
CREATE INDEX ${인덱스명} ON ${테이블명} (${컬럼명});

-- 2. 복합 인덱스 등록
CREATE INDEX ${인덱스명} ON ${테이블명} (${컬럼명1, 컬럼명2});

-- 3. 인덱스 삭제
DROP INDEX ${인덱스명} ON ${테이블명};
```

### JPA Index 설정

- JPA에서 명시적으로 Index를 지정하고, 인덱스를 자동생성할 수 있는 기능을 제공한다.
  - 해당기능은 ddl-auto 설정이 create, create-drop 일때만 동작하며 update, validate, none 일땐 동작하지 않는다.
  - 하지만 실제 서비스에선 create, create-drop을 사용하지 않기 때문에 인덱스 자동생성 기능은 거의 사용하지 않는다고 볼 수 있다.
  - 따라서, 해당 기능은 테스트 설정일때 혹은, 테이블에 걸려있는 인덱스를 명시적으로 사용하기 위해서 작성한다고 볼 수 있다.


```java
@Entity
@Table(name = "${테이블명}", indexes = @Index(name = "${인덱스명}", columnList = "${컬럼명1},${컬럼명}2"))
public class 엔티티{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "${컬럼명1}")
  private String 컬럼명1;

  @Column(name = "${컬럼명2}")
  private String 컬럼명2;
}
```

---

## API 쿼리 분석 및 인덱스 개선점

> 테스트 환경
>
> 테스트 데이터로, 예약과 결제 및 좌석의 정합성이 일치하지 않을 수 있습니다.
> - 콘서트: 50개
    >   - OPEN: 10개
>   - CLOSED: 40개
> - 콘서트 일정: 1,000개
    >   - OPEN 콘서트 일정 : 200개
>   - CLOSED 콘서트 일정 : 800개
> - 좌석: 총 200,000개
    >   - AVAILABLE 좌석: 40,000개
>   - UN_AVAILABLE 좌석: 160,000개
> - 예약: 총 200,000개
    >   - EXPIRED 예약: 20,000개
>   - PAYMENT_COMPLETED 예약: 180,000개
> - 결제: 총 160,000개
    >   - COMPLETED 결제: 160,000개

### API List

1. **콘서트**
- 콘서트 조회: `GET` `/api/concerts/`
  - 실행 빈도: 매우높음
  - 수행 복잡도: 낮음
  - 실행 쿼리
```sql
-- 콘서트 정보 조회
select
  id,
  description,
  status,
  title
from
  concert
```

- 예약가능 콘서트스케쥴 조회: `GET` `/api/concerts/{concertId}/schedules`
  - 실행 빈도: 매우높음
  - 수행 복잡도: 보통
  - 실행 쿼리
```sql
-- 콘서트 정보 조회
select
  id,
  description,
  status,
  title
from concert
where id=?;

-- 콘서트 스케줄 조회
select
  id,
  concert_id,
  available_reservation_time,
  concert_time
from concert_schedule
where concert_id=? and
  available_reservation_time < ? and concert_time > ?
```

- 예약가능 콘서트좌석 조회: `GET` `/api/concerts/{concertId}/schedules/{concertScheduleId}/seats`
  - 실행 빈도: 매우높음
  - 수행 복잡도: 높음
  - 실행 쿼리
```sql

-- 콘서트 정보 조회
select id,
       description,
       status,
       title
from concert
where id=?;

-- 콘서트 스케줄 조회
select
  id,
  available_reservation_time,
  concert_id,
  concert_time
from concert_schedule
where id=?;

-- 좌석 정보 조회
select id,
       concert_schedule_id,
       reserved_at,
       seat_number,
       seat_price,
       status
from seat
where concert_schedule_id = ? and status = ?

```
2. **예약**
- 콘서트 예약: `POST` `/api/reservations`
  - 실행 빈도: 높음
  - 수행 복잡도: 높음
  - 실행 쿼리
```sql
-- 콘서트 정보 조회
SELECT
  id,
  description,
  status,
  title
FROM concert
WHERE id = ?;

-- 콘서트 스케줄 조회
SELECT
  id,
  available_reservation_time,
  concert_id,
  concert_time
FROM concert_schedule
WHERE id = ?;

-- 좌석 정보 조회
SELECT
  id,
  concert_schedule_id,
  reserved_at,
  seat_number,
  seat_price,
  status
FROM seat
WHERE id = ?
  FOR UPDATE;

-- 예약 추가
INSERT INTO reservation (
  concert_id,
  concert_schedule_id,
  reserved_at,
  seat_id,
  status,
  user_id
) VALUES (?, ?, ?, ?, ?, ?);

-- 좌석 정보 업데이트
UPDATE seat
SET
  concert_schedule_id = ?,
  reserved_at = ?,
  seat_number = ?,
  seat_price = ?,
  status = ?
WHERE id = ?
```
3. **결제**
- 콘서트 결제: `POST` `/api/payments/`
  - 실행 빈도: 높음
  - 수행 복잡도: 높음
  - 실행 쿼리
```sql
-- 예약 정보 조회
SELECT
  id,
  concert_id,
  concert_schedule_id,
  reserved_at,
  seat_id,
  status,
  user_id
FROM reservation
WHERE id = ?;

-- 좌석 정보 조회
SELECT
  id,
  concert_schedule_id,
  reserved_at,
  seat_number,
  seat_price,
  status
FROM seat
WHERE id = ?;

-- 사용자 포인트 정보 조회
SELECT
  id,
  amount,
  updated_at,
  user_id
FROM point
WHERE user_id = ?;

-- 결제 정보 추가
INSERT INTO payment (
  amount,
  payment_at,
  payment_status,
  reservation_id,
  user_id
) VALUES (?, ?, ?, ?, ?);

-- 예약 정보 업데이트
UPDATE reservation
SET
  concert_id = ?,
  concert_schedule_id = ?,
  reserved_at = ?,
  seat_id = ?,
  status = ?,
  user_id = ?
WHERE id = ?;

-- 포인트 정보 업데이트
UPDATE point
SET
  amount = ?,
  updated_at = ?,
  user_id = ?
WHERE id = ?
```

4. **포인트**
- 포인트 충전: `POST` `/api/users/{userId}/point`
  - 실행 빈도: 보통
  - 수행 복잡도: 낮음
  - 실행 쿼리
```sql
-- 사용자 존재 여부 확인
SELECT COUNT(*)
FROM user
WHERE id = ?;

-- 사용자 포인트 정보 조회
SELECT
  id,
  amount,
  updated_at,
  user_id
FROM point
WHERE user_id = ?
  FOR UPDATE;

-- 포인트 정보 업데이트
UPDATE point
SET
  amount = ?,
  updated_at = ?,
  user_id = ?
WHERE id = ?
```
- 포인트 조회: `GET` `/api/users/{userId}/point`
  - 실행 빈도: 보통
  - 수행 복잡도: 낮음
  - 실행 쿼리
```sql
-- 사용자 포인트 정보 조회
SELECT
  id,
  amount,
  updated_at,
  user_id
FROM point
WHERE user_id = ?;
```


### 테이블 별 필요 인덱스 분석

#### Concert
- 별도의 검색 API가 없어 현재 상태는 인덱스가 불필요함
- 콘서트 특성상 데이터 생성 빈도가 매우 낮아, 인덱스를 고려할 만큼의 상황은 나타나지 않을 가능성이 높다.
- 제목 및 오픈 상태로 검색할 수 있는 경우를 대비한다면 title(제목) 또는 status(상태)의 단일 인덱스를 고려해볼 수 있다.
- 사용되는 조건
  - `where id = ?`
#### ConcertSchedule
- 일정 검색의 경우, concert_id와 시간 범위의 조합을 통하여 매우 잦은 조회가 발생한다.
- 사용되는 조건
  - `where concert_id = ?`
    - FK키 인덱싱 - `concert_id`
  - `where concert_id=? and available_reservation_time < ? and concert_time > ?`
    - 현재 시나리오에서는 콘서트의 데이터 범위가 1~50건으로, 매우 낮기 때문에 복합 인덱스를 설정할 떄
    - `index(available_reservation_time , concert_time, concert_id)` 보단(Look-up)
    - `index(concert_id, available_reservation_time , concert_time)` 가(Range Scan + Look-Up) 효율적이다.
#### Seat
- 사용되는 조건
  - `WHERE id = ?`
    - PK 조회임으로 인덱스 불필요
  - `where concert_schedule_id = ? and status = ?`
    - 현재, 데이터 시나리오 상 콘서트 스케쥴의 경우
      - `index(status, concert_schedule_id, status)` 보단(Look-up)
      - `index(concert_schedule_id, status)` 가(Range Scan + Look-Up) 효율적이다.
#### Point
- 단순 user_id로 조회 쿼리만 존재
- 사용되는 조건
  - `where user_id = ?`
    - FK키 인덱싱 - `user_id`
#### Reservation
- 단순 PK 조회 쿼리만 존재함으로 별도 인덱싱 불 필요
- 사용되는 조건
  - `where id = ?`
#### Payment
- 조회 쿼리 없음
- 사용되는 조건
  - 조회 쿼리 없음

### 인덱스 성능 테스트

#### 조회 성능 비용이 가장 높게 예상되는 쿼리 성능 테스트

>참고
> - 동일 DB 테스트 함으로, 시간 성능 비교는 Buffer Pool로 인한 차이가 날 수 있습니다.
> - 전체 테스트가 아닌 대표적으로 조회 비용이 많이 발생하는 **예약 가능 일정 조회**와 **예약 가능 좌석 조회**를 테스트 진행

<br/>

- **좌석정보 조회(복합 인덱스 - `concertSchedule_id, status`)**
```mysql
explain analyze
select id,
       concert_schedule_id,
       reserved_at,
       seat_number,
       seat_price,
       status
from seat
where concert_schedule_id = ? and status = ?

-- 인덱스 미 존재
-- -> Filter: ((seat.`status` = 'UNAVAILABLE') and (seat.concert_schedule_id = 400))  (cost=20167 rows=9967) (actual time=32.6..66.9 rows=200 loops=1)
--   -> Table scan on seat  (cost=20167 rows=199342) (actual time=0.515..42.3 rows=200000 loops=1)

-- 인덱스 존재
-- Index lookup on seat using seat_concert_schedule_id_status_index (concert_schedule_id=400, status='UNAVAILABLE'), 
-- with index condition: (seat.`status` = 'UNAVAILABLE')  (cost=70 rows=200) (actual time=0.207..0.901 rows=200 loops=1)
```

| 비교 항목      | 인덱스 미존재 (Full Table Scan) | 인덱스 존재 (Index Lookup) |
|--------------|--------------------------------|-----------------------|
| **쿼리 실행 방식** | 테이블 풀스캔 (Full Table Scan) | 인덱스 탐색 (Index Lookup) |
| **쿼리 실행 비용 (cost)** | `20167` (매우 높음) | `70` (약 288배 개선)      |
| **처리된 행 개수 (rows)** | `200,000`개 조회 | `200`개 조회 (1000배 개선)  |
| **쿼리 실행 시간 (actual time)** | `66.9ms` | `0.910ms`  (73배 개선)   |


<br>

- **예약 가능 콘서트 일정 조회(복합 인덱스 - `concert_id, available_reservation_time, concert_time`)**
```mysql
select
  id,
  concert_id,
  available_reservation_time,
  concert_time
from concert_schedule
where concert_id= ? and
  available_reservation_time < ? and concert_time > ?

-- 인덱스 미 존재
#   -> Filter: ((concert_schedule.concert_id = 183) 
#   and (concert_schedule.available_reservation_time < TIMESTAMP'2025-01-11 16:00:00') 
#   and (concert_schedule.concert_time > TIMESTAMP'2025-01-13 12:00:00'))  (cost=1984 rows=218) (actual time=1.28..7.8 rows=1 loops=1)
#     -> Table scan on concert_schedule  (cost=1984 rows=19594) (actual time=0.352..6.1 rows=20000 loops=1)


# 인덱스 존재 1번- 단일 인덱스(concert_id)
#   -> Filter: ((concert_schedule.available_reservation_time < TIMESTAMP'2025-01-11 16:00:00') 
#   and (concert_schedule.concert_time > TIMESTAMP'2025-01-13 12:00:00'))  (cost=5.22 rows=2.22) (actual time=0.0735..0.0752 rows=1 loops=1)
#     -> Index lookup on concert_schedule using concert_schedule_concert_id_index (concert_id=183)  (cost=5.22 rows=20) (actual time=0.0651..0.07 rows=20 loops=1)

# 인덱스 존재 2번(concert_id, concert_time)
# -> Filter: (concert_schedule.available_reservation_time < TIMESTAMP'2025-01-11 16:00:00')  (cost=9.26 rows=6.67) (actual time=0.085..0.1 rows=1 loops=1)
#   -> Index range scan on concert_schedule using concert_schedule_concert_id_concert_time_index over (concert_id = 183 AND '2025-01-13 12:00:00.000000' < concert_time)
#   , with index condition: ((concert_schedule.concert_id = 183) and (concert_schedule.concert_time > TIMESTAMP'2025-01-13 12:00:00'))  (cost=9.26 rows=20) (actual time=0.0838..0.0964 rows=20 loops=1)

# 인덱스 존재 3번(concert_id, available_reservation_time)
#   -> Filter: (concert_schedule.concert_time > TIMESTAMP'2025-01-13 12:00:00')  (cost=0.71 rows=0.333) (actual time=0.0527..0.0619 rows=1 loops=1)
#     -> Index range scan on concert_schedule using concert_schedule_concert_id_available_reservation_time_index over (concert_id = 183 AND available_reservation_time < '2025-01-11 16:00:00.000000')
#     , with index condition: ((concert_schedule.concert_id = 183) and (concert_schedule.available_reservation_time < TIMESTAMP'2025-01-11 16:00:00'))  (cost=0.71 rows=1) (actual time=0.0507..0.0596 rows=1 loops=1)

# 인덱스 존재 4번(concert_id, available_reservation_time, concert_time)
#   -> Filter: ((concert_schedule.concert_id = 183) and (concert_schedule.available_reservation_time < TIMESTAMP'2025-01-11 16:00:00') 
#   and (concert_schedule.concert_time > TIMESTAMP'2025-01-13 12:00:00'))  (cost=1.19 rows=0.333) (actual time=0.0581..0.0664 rows=1 loops=1)
# -> Covering index range scan on concert_schedule using reservation_time_concert_time_index over 
# (concert_id = 183 AND available_reservation_time < '2025-01-11 16:00:00.000000')  (cost=1.19 rows=1) (actual time=0.0524..0.0604 rows=1 loops=1)
```


| 비교 항목 | **인덱스 미존재** (Full Table Scan) | **단일 인덱스 (`concert_id`)** | **복합 인덱스 (`concert_id, concert_time`)** | **복합 인덱스 (`concert_id, available_reservation_time`)** | **복합 인덱스 (`concert_id, available_reservation_time, concert_time`)** |
|--------------|-------------------------------|------------------------------|-----------------------------------------|-------------------------------------------------------|---------------------------------------------------------------|
| **쿼리 실행 방식** | 테이블 풀스캔 (Full Table Scan) | 인덱스 탐색 (Index Lookup) | 인덱스 범위 스캔 (Index Range Scan) | 인덱스 범위 스캔 (Index Range Scan) | **커버링 인덱스 스캔 (Covering Index Scan) ** |
| **쿼리 실행 비용 (cost)** | `1984` (매우 높음) | `5.22` (약 **380배 개선**) | `9.26` (약 **214배 개선**) | `0.71` (약 **2794배 개선**) | **`1.19` (약 **1667배 개선**)** |
| **처리된 행 개수 (rows)** | `20,000`개 조회 | `20`개 조회 (약 **1000배 감소**) | `20`개 조회 (약 **1000배 감소**) | `1`개 조회 (약 **20,000배 감소**) | **`1`개 조회 (최적, 약 **20,000배 감소**)** |
| **쿼리 실행 시간 (actual time)** | `0.352ms ~ 6.1ms` | `0.0651 ~ 0.07ms` (약 **47.76배 개선**) | `0.0838ms ~ 0.0964ms` (약 **35.80배 개선**) | `0.0507ms ~ 0.0596ms` (약 **58.50배 개선**) | **`0.0524ms ~ 0.0604ms` (약 **57.20배 개선**)** |

**분석**
- 예약 가능 콘서트 스케쥴을 조회하는 쿼리의 경우
  - 복합 인덱스의 경우 where 조건의 순서가 중요하기 때문에, `concert_id, concert_time` 는 제대로 작동하지 않음
  - 인덱스(`concert_id, available_reservation_time`) 와 인덱스(`concert_id, available_reservation_time, concert_time`)의 경우
    - 인덱스(`concert_id, available_reservation_time`)가 더 나은 성능을 발휘 했다.
    - 이유는, concert_id에서 필터링 된 결과 조건이 20개인 것과 available_reservation_time의 경우, 서비스 정책상 concert_time 보다 항상 2일 빠르기 때문에 별도의 정렬이 의미가 없기 때문
      - 단, 여기서 concert_id의 결과가 20개가 아닌 수백개에 달하는 경우 인덱스(`concert_id, available_reservation_time, concert_time`)는 `concert_schedule`의 모든 필드 데이터를 다 가지고 있기때문에 `Covering Index`로 동작함으로 내부 데이터 적재 유형에 따라서 더 높은 성능을 나타낼 수도 있다.





