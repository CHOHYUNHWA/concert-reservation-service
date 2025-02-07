# 캐싱을 이용한 서비스 개선

## Cache

### About Cache

#### 캐시란, 자주 사용되거나 반복적으로 조회되는 데이터를, 일반 저장소(예: 하드디스크, 네트워크 스토리지)보다 훨씬 빠른 접근 속도를 가진 저장소(예: 메모리, SSD 등)에 임시로 저장하여 응답 속도와 처리 성능을 향상시키는 기법



- **캐시의 특징**
  - 빠른 I/O 속도: 캐시는 메모리와 같이 입출력이 빠른 저장소를 활용
  - 데이터 접근 최적화: 빈번하게 요청되는 데이터를 미리 보관함으로써 데이터 조회 시간을 단축


- **참고사항**
  - 레디스는 캐시가 아니다.(**Redis != Cache**) 
    - 레디스는 NoSql기반의 In-memory DB로써 HDD 나 SDD보다 조회가 빠른 Storage이지 캐시가 아니다.
    - 쉽게 말해서, Redis는 캐시기법을 사용하기 위한 Data 저장소이지 Cache 자체가 아님
  - **캐시는 반드시 RAM을 사용하는 것은 아니다.**
    - 캐시는 데이터를 빠르게 접근하기 위한 기술로 일반적으로 RAM을 많이 이용하지만, 반드시 휘발성 메모리(RAM)만을 사용하지는 않는다.
    - 예를 들어, 웹 브라우저에서 웹 페이지에 접근할 때, CSS나 JS와 같은 정적 리소스는 브라우저 캐시 또는 Local Storage에 저장된다. 이 경우, 해당 데이터는 디스크에 저장되지만, 네트워크를 통해 매번 다운로드 받지 않고 로컬에서 빠르게 읽어올 수 있으므로 캐싱의 효과를 누릴 수 있다. 
    - 따라서, 캐시는 메모리뿐만 아니라 디스크 등 다양한 저장 매체를 활용할 수 있으며, 중요한 것은 데이터에 빠르게 접근할 수 있도록 하는 점이다.
---

## Local Cache 와 Global Cache

### 1. Local Cache

- **특징**
  - Application Layer Cache는 어플리케이션 서버 내부에서 동작하는 캐시로, 주로 메모리 기반 캐시(Java/Spring 기준 예: Ehcache, Caffeine, Guava Cache 등)를 사용
  - 어플리케이션 프로세스 내부에서 직접 데이터를 캐싱하므로, 접근 속도가 매우 빠름
  - 로컬 서버의 자원(메모리, 경우에 따라 디스크)을 활용하여 데이터를 저장
- **장점**
  - 빠른 I/O 성능: 캐시 데이터가 애플리케이션 내부에 존재하므로, 네트워크 지연 없이 매우 빠르게 읽고 쓰기 가능
  - 네트워크 비용 없음: 별도의 외부 네트워크 호출 없이 로컬 자원으로 캐싱 처리가 가능하여, 네트워크 오버헤드가 발생하지 않음
  - 비교적 간단한 구현 및 관리: 복잡한 분산 시스템 구성 없이 애플리케이션 내부에서 바로 사용 가능
- **단점**
  - 데이터 일관성 문제: 분산환경에서 각 서버마다 로컬 캐시가 존재하는 경우, 서버 간 캐시의 갱신 주기나 업데이트 타이밍이 달라 데이터의 일관성이 깨질 수 있음
  - 서버 장애 시 데이터 손실: 로컬 캐시는 애플리케이션 프로세스의 메모리를 사용하므로, 서버가 재시작되거나 장애가 발생하면 캐시 데이터가 소실될 수 있음
  - 메모리 제한: 서버에 할당된 메모리 용량이 낮은 경우, 캐시할 수 있는 데이터의 양이 제한될 수 있음

### 2. Global Cache
- **특징**
  - 중앙 집중형 캐시: 여러 애플리케이션 서버나 서비스(분산환경)에서 하나의 공유 캐시 서버를 통해 데이터를 캐싱하는 시스템
  - 대표적인 솔루션: Redis, Memcached등이 있으며, In-memory 데이터 저장소를 사용
  - 애플리케이션 서버 간에 캐시를 공유하여 데이터의 일관성을 보자 쉽게 유지 가능
- **장점**
  - 데이터 일관성 유지 용이: 모든 어플리케이션 서버가 동일한 캐시 서버를 사용하므로, 데이터 변경 시 일관된 결과를 제공 가능
  - 확장성: 글로벌 캐시 시스템은 클러스터링이나 복제 구성(Replication)을 통해 확장이 가능
  - 중앙 관리 용이: 캐시 정책, 만료 시간, 갱신 전략등을 중앙에서 통일하여 관리 가능
- **단점**
  - 추가 인프라 비용 발생: 별도의 캐시 서버를 구축하고 운영해야 하므로, 관리 포인트가 늘어나고 비용 증가
  - 네트워크 지연: 애플리케이션 서버와 캐시 서버 간의 네트워크 통신이 필요하므로, 로컬 캐시에 비해 지연(latency)가 발생
  - 단일 장애점(SPOF) 가능성: 캐시 서버가 단일 장애점이 될 수 있으므로, 고가용성(HA) 구성이나 복제, 페일오버(failover) 전략을 마련해야함


## 캐싱 전략과 주의사항

#### 캐싱 전략은 크게 읽기전략과 쓰기전략으로 나뉘고 읽/쓰기 전략을 적절히 조합하여 사용한다.

### 1. 읽기 전략

<img src="/docs/img/cache/LookAside.png"  style="background: white" alt="Look Aside Cache Strategy" width="670">

- **Look Aside(Cache Aside) 전략 특징**
  - 애플리케이션이 캐시를 먼저 조회
  - Cache Miss 발생 시 애플리케이션이 DB에서 데이터를 조회 후 캐시에 저장
  - 캐시 갱신 시, 애플리케이션이 명시적으로 캐시를 업데이트해야 함
- **장점**
  - 캐시 메모리 효율적 사용: 자주 사용되는 데이터만 캐시에 저장
  - DB 트래픽 감소: 한 번 캐시에 저장되면 이후에 DB 조회 없이 빠르게 응답
  - 단순한 구현: 캐시 정책을 애플리케이션 레벨에서 관리 가능
- **단점**
  - Cache Miss 발생 시 초기 성능 저하: 첫 번째 요청은 무조건 DB에서 가져와야 함으로 지연 발생
  - 캐시 일관성 문제: 데이터 변경 시 캐시를 업데이트하거나 기존 캐시를 삭제해야 함

<br/>

<img src="/docs/img/cache/ReadThrough.png" style="background: white" alt="Look Aside Cache Strategy" width="670">

- **Read Through 전략 특징**
  - 애플리케이션은 항상 캐시 서버를 조회
  - Cache Miss 발생 시, 캐시 서버가 직접 DB에서 데이터를 가져와 캐시에 저장
  - 애플리케이션은 캐시에 있는 데이터만 사용하고, DB에는 접근하지 않음
- **장점**
  - 캐시 관리 자동화: 애플리케이션 레벨에서 캐시를 관리할 필요 없음
  - 데이터 일관성 향상: 캐시가 자동으로 최신 데이터를 유지
  - DB 부하 감소: 캐시 서버가 DB에서 데이터를 가져오기 때문에 DB 트래픽 감소
- **단점**
  - 캐시 서버 장애 시 데이터 조회 불가: 캐시 서버가 다운되면 데이터 조회에 문제가 발생할 수 있어 적절한 FailOver 설정이 필요
  - 복잡한 설정 필요: 캐시 서버가 DB와 동기화 되도록 하기 위해 추가적인 설정이 필요(RedisGear, 별도의 Worker 서버 셋팅)

<br/>

### 2. 쓰기 전략

<img src="/docs/img/cache/WriteBack.png" style="background: white" alt="Look Aside Cache Strategy" width="670">

- **Write Back 전략 특징**
  - 애플리케이션이 먼저 캐시에 데이터를 저장하고 즉시 쓰기 완료 응답
  - 캐시 서버는 저장된 데이터들을 가지고 있다가, 배치 프로세스(스케쥴링)등을 통해 DB에 한번에 데이터 저장
  - 캐시 서버가 쓰기 작업을 DB로 비동기적 처리
- **장점**
  - 매우 빠른 쓰기 성능: 애플리케이션은 DB를 거치치 않으므로 빠른 응답 가능
  - DB 부하 감소: 주기적인 배치로 일괄 쓰기가 작동되므로 부하를 최소화 할 수 있음
  - 빠른 데이터 읽기: 최신 데이터가 항상 캐시에 존재
- **단점**
  - 데이터 유실 가능성: 캐시 서버 장애 시, DB에 반영되지 않은 데이터 손실 위험 존재
  - 데이터 일관성 문제: 캐시 서버와 DB가 비동기적으로 동기화되므로, DB 저장 전 데이터가 최신 데이터인지 확인할 수 없음

<br/>

<img src="/docs/img/cache/WriteThrough.png" style="background: white" alt="Look Aside Cache Strategy" width="670">

- **Write Through 전략 특징**
  - 애플리케이션이 캐시에 데이터를 저장
  - 캐시 서버가 동기적으로 DB에도 데이터를 저장
  - 데이터 일관성이 매우 중요할 때 적용
- **장점**
  - 데이터 일관성 유지:
  - 빠른 데이터 읽기: 최신 데이터가 항상 캐시에 존재
  - 데이터 유실 위험 없음: 데이터가 DB에 즉시 저장되므로 상대적으로 안전
- **단점**
  - 쓰기 성능 저하: 캐시와 DB에 동시에 데이터를 저장하므로 쓰기 속도가 저하될 수 있음
  - DB 부하 증가: 쓰기 요청 시 마다 DB는 요청을 수행하기 때문에 다수의 트랜잭션 발생으로 인한 DB 부하가 증가할 수 있음

<br/>

<img src="/docs/img/cache/WriteAround.png" style="background: white" alt="Look Aside Cache Strategy" width="670">

- **Write Around 전략 특징**
  - 애플리케이션이 항상 DB에 직접 데이터를 저장
  - 쓰기시 캐시에 데이터를 저장하지 않음
  - 쓰기 부하가 높은 시스템에서 효과적이지만, 읽기 성능이 중요한 시스템에서는 비효율적
- 장점
  - 캐시 부하 감소: 모든 데이터를 캐시에 저장하지 않기 때문에, 캐시 서버의 메모리 사용이 최적화
  - 자주 사용되지 않는 데이터에 효과적: 읽히지 않을 가능성이 높은 데이터는 캐시에 적재할 필요가 없으므로 불필요한 캐시 메모리 낭비 방지
- 단점
  - Cache Miss 증가: 별도의 캐시,DB 간의 동기화가 없으면 Cache Miss 발생이 증가
  - 읽기 성능 저하 가능성: 자주 조회되는 데이터의 경우 캐시에서 바로 가져올 수 없을 가능성 존재

### 3. 주의할 점

- **Cache Stampede**
  - Cache Stampede란, 캐시가 무효화(삭제)될 때 동시에 많은 요청이 캐시에 접근하여, 모든 요청이 동시에 DB를 조회하는 현상
  - DB의 부하가 급증하고, 성능 저하(트래픽 폭주)가 발생할 수 있음
  - 예를들어, 증권사 애플리케이션에서 장이 열리는 시간에 한꺼번에 요청이 들어와서 동일한 TTL 만료로 인해 다시 데이터를 조회하는 경우 모든 요청은 Cache를 뚫고 DB로 조회가 몰릴 것 이다.
- 해결방안
  - Mutex Lock
    - 캐시가 만료된 경우 DB를 동시에 조회하는 것을 방지하기 위해, 하나의 요청만 DB 조회를 수행하도록 설정(분산락)
    - 하나의 요청만 DB를 조회하여, DB 부하를 크게 줄일 수 있음
    - DB를 조회하는 데이터를 대기하는 시간 발생
  - Early Revalidation
    - 캐시 만료 전 백그라운드에서 미리 데이터를 갱신(배치)
    - 캐시가 만료되더라도 기존 데이터를 유지하면서 새로운 데이터를 미리 로드 시킴
    - 불필요한 데이터 갱신 가능성 존재
    - 변경되지 않은 데이터도 계속 갱신될 수 있음
  - Random TTL
    - 캐시 데이터별로 랜덤하게 TTL을 설정하여, 동시에 캐시 만료를 방지
    - TTL이 달라, 데이터 동기화가 어려울 수 있음
  - TTL Reset
    - 캐시 데이터가 조회될 때마다 TTL을 리셋시켜, 자주 조회되는 캐시 데이터의 경우, 캐시 만료를 방지
    - 핫 데이터(조회가 잦고 많은)는 항상 캐시 유지가능(Cache Miss 방지)
    - 오래된 데이터가 캐시에 남아 있을 수 있음
  - **추가로 고려해보면 좋을 해결 방안**
    - 멀티 Layer Caching
      - Local , Global 과 같이 레이어 별 캐싱 전략 적용
    - 캐시 서버 클러스터링 구축

---

## Redis

### Redis란?

- **Remote Dictionary Server**의 약자로, 고성능 오픈 소스 **인메모리 키-값 데이터 구조 저장소**이다.
- **데이터베이스, 캐시, 메시지 브로커** 등으로 사용되며, 주로 **빠른 데이터 액세스가 필요한 애플리케이션**에서 활용된다.


- **Redis 특징**
  - **인메모리 저장**: 모든 데이터를 메모리에 저장하여 **빠른 읽기와 쓰기 성능**을 제공
  - **다양한 자료 구조**: String, Lists, Sets, Hash, Sorted Set 등 다양한 자료 구조를 지원
  - **영속성(AOF, RDB 지원)**: 데이터를 디스크에 저장하여 **서버 장애 발생 시에도 복구 가능**
  - **싱글 스레드**: 기본적으로 **단일 스레드 이벤트 루프**로 동작하며, **원자적 연산을 보장**하여 데이터의 일관성 유지에 용이
  - **고가용성 및 확장성 지원**: Redis Sentinel을 통한 **고가용성(HA)** 및 Redis Cluster를 통한 **수평적 확장(Sharding)** 가능

### Redis 자료구조

- Redis는 다양한 자료 구조를 제공하여 데이터 저장 및 처리를 효율적으로 수행할 수 있도록 지원한다. 각 자료 구조는 특정한 용도와 장점을 가지고 있으며, 애플리케이션의 요구사항에 따라 적절하게 선택할 수있다.

<figure>
  <img src="/docs/img/cache/RedisDataStructure.png" style="background: white" alt="Redis Data Structure" width="670">
  <figcaption style="text-align: center; font-size: 14px; color: gray;">
    출처: <a href="https://ryu-e.tistory.com/9/" target="_blank">Tistory-Blog</a>
  </figcaption>
</figure>

#### 대표적으로 사용되는 Redis 자료 구조

1. String
   - 가장 기본적인 자료 구조로, 일반적인 키-값 저장 방식
   - 최대 512MB까지 저장 가능
   - 사용 예: 캐싱, 세션 저장, 카운터 등
2. List
   - 순서가 있는 문자열 리스트(Linked List 기반)
   - 요소를 왼쪽(LPUSH, LPOP) 또는 오른쪽(RPUSH, RPOP) 에서 추가/저게 가능
   - 사용 예: 작업 대기열(Queue), 메시지 브로커 등
3. Set
   - 중복을 허용하지 않는 고유한 값들의 집합
   - 요소 추가/삭제가 빠르고, 교집합/합집합 등의 집합 연산 지원
   - 사용 예: 태그 저장, 팔로우/팔로워 관계 관리 등
4. Sorted Set(ZSet)
   - Set과 유사하지만, 각 요소에 점수를(score)를 부여하여 정렬된 상태로 저장
   - 높은 성능의 랭킹 시스템 구현 가능
   - 사용 예: 리더보드, 추천 시스템
5. Hash
   - Key-Value 쌍을 저장하는 자료 구조 (JSON과 유사)
   - 하나의 키에 여러 필드와 값을 저장 가능 (HSET, HGET)
   - 사용 예: 사용자 프로필 정보 저장
6. Bitmap
   - 비트(bit) 단위로 데이터를 저장하는 구조
   - 매우 효율적인 공간 활용이 가능하며, 개별 비트를 설정/조회할 수 있음
   - 사용 예: 출석 체크, 활성 유저 기록
7. HyperLogLog
   - 대략적인 유니크 카운팅(Approximate Counting) 알고리즘을 사용
   - 매우 작은 공간(12KB)으로 수십억 개의 고유 항목을 추정 가능
   - 사용 예: 방문자 수 계산, 로그 데이터 분석

  
---

## Redis 주요 자료구조 및 명령어 실습

### 1. String
- String 주요 명령어
  - `SET {key} {value}`
    - 키와 문자열 값 저장
  - `GET {key} {value}`
    - 가져온 키의 문자열 값 반환
  - `INCR {key}`
    - 키의 숫자 증가(only integer)
  - `DECR {key}`
    - 키의 숫자 감소(only integer)
```
//키와 값 저장
127.0.0.1:6379> SET redisKey "redisValue"
OK

//키에 해당하는 값 불러오기
127.0.0.1:6379> GET redisKey
"redisValue"

//숫자 값 저장
127.0.0.1:6379> SET counter 100
OK

//값 증가
127.0.0.1:6379> INCR counter
(integer) 101

//값 감소
127.0.0.1:6379> DECR counter
(integer) 100

//값이 문자열일 경우 에러가 난다.
127.0.0.1:6379> SET test key
127.0.0.1:6379> INCR test
(error) ERR value is not an integer or out of range
```


### 2. List


- **List 주요 명령어**
  - `LPUSH {key} {value}`
    - 왼쪽(앞)에 값 추가
  - `RPUSH {key} {value}`
    - 오른쪽(뒤)에 값 추가
  - `LPOP {key}`
    - 왼쪽에서 값 꺼내기
  - `RPOP {key}`
    - 오른쪽에서 값 꺼내기
  - `LRANGE {key} {start} {stop}`
    - 전체 범위 값 조회
    - 0은 첫 번째 요소(왼쪽)
      - 0, 1, 2 순으로 왼쪽으로부터 범위를 좁힐 수 있다.
    - -1은 마지막 요소(오른쪽)
      - -1, -2, -3 순으로 오른쪽으로부터 범위를 좁힐 수 있다.
  - `LLEN {key}`
    - 리스트의 길이 조회
```

//List의 왼쪽(앞)에 값 추가
127.0.0.1:6379> LPUSH mylist "Task 1"
(integer) 1

//List의 왼쪽(앞)에 값 추가
127.0.0.1:6379> LPUSH mylist "Task 2"
(integer) 2

//List의 오른쪽(뒤)에 값 추가
127.0.0.1:6379> RPUSH mylist "Task 3"
(integer) 3

//List 의 0(왼쪽으로 부터 처음) 부터 -1(오른쪽으로 부터 처음) 까지 조회
//0 -1 은 왼쪽을 기준으로 처음부터 끝까지 이다.
127.0.0.1:6379> LRANGE mylist 0 -1
1) "Task 2"
2) "Task 1"
3) "Task 3"

//0번째 인덱스 값 조회
127.0.0.1:6379> LINDEX mylist 0
"Task 2"

//리스트의 길이 조회
127.0.0.1:6379> LLEN mylist
(integer) 3

//가장 왼쪽(앞) 값 반환 후 제거
127.0.0.1:6379> LPOP mylist
"Task 2"

//기존 가장 왼쪽(앞) 값 "Task 2"가 제거 되었다.
127.0.0.1:6379> LRANGE mylist 0 -1
1) "Task 1"
2) "Task 3"

//가장 오른쪽(뒤) 값 반환 후 제거
127.0.0.1:6379> RPOP mylist
"Task 3"

//기존 가장 오른쪽(뒤) 값 "Task 1"가 제거 되었다.
127.0.0.1:6379> LRANGE mylist 0 -1
1) "Task 1"

// 왼쪽으로 부터 "Task 1"이 1개 삭제됨(2일 경우 2개가 삭제됨)
// -1은 오른쪽부터 1개 삭제 이다. 
// 2인 경우 -> 왼쪽에서 2개 삭제
// -2인 경우 -> 오른쪽에서 2개 삭제
127.0.0.1:6379> LREM mylist 1 "Task 1"
(integer) 1

//모든 데이터가 삭제되어 빈 배열을 반환
127.0.0.1:6379> LRANGE mylist 0 -1
(empty array)
```


### 3. Set
- **Set 주요 명령어**
  - `SADD {key} {value}`
    - 값 추가
  - `SREM {key} {value}`
    - 값 제거
  - `SMEMBERS {key}`
    - 모든 요소 가져오기
  - `SCARD {key}`
    - 요소 개수 확인
  - `SISMEMBER {key} {value}`
    - 값 존재 확인
```

//Set 값 추가
127.0.0.1:6379> SADD setKey "setValue1"
(integer) 1

//Set은 중복을 허용하지 않기때문에 0을 반환
127.0.0.1:6379> SADD setKey "setValue1"
(integer) 0

//Set 값 추가
127.0.0.1:6379> SADD setKey "setValue2"
(integer) 1

//Set 값 추가
127.0.0.1:6379> SADD setKey "setValue3"
(integer) 1

//Set의 모든 값 조회
127.0.0.1:6379> SMEMBERS setKey
1) "setValue1"
2) "setValue2"
3) "setValue3"

//"setValue2"를 삭제
127.0.0.1:6379> SREM setKey "setValue2"
(integer) 1

//Set의 모든 값 조회 "setValue2"가 삭제 되었다.
127.0.0.1:6379> SMEMBERS setKey
1) "setValue1"
2) "setValue3"

//Set의 크기 조회
127.0.0.1:6379> SCARD setKey
(integer) 2

//Set에 해당 "setValue1"가 존재하는 지 조회 
127.0.0.1:6379> SISMEMBER setKey "setValue1"
(integer) 1

//Set에 해당 "setValue2"가 존재하는 지 조회
//SREM으로 삭제 되었으므로 0을 반환
127.0.0.1:6379> SISMEMBER setKey "setValue2"
(integer) 0
```

### 4. Sorted Set(ZSet)
- **Sorted Set(ZSet) 주요 명령어**
  - `ZADD {key} {score} {value}`
    - 값 추가(점수 포함)
  - `ZREM {key} {value}`
    - 값 제거
  - `ZRANGE {key} {start} {stop} WITHSCORES`
    - 점수가 낮은 순으로 정렬하여 조회
  - `ZREVRANGE {key} {start} {stop} WITHSCORES`
    - 점수가 높은 순으로 정렬하여 조회
  - `ZSCORE {key} {value}`
    - 값의 점수 조회
  - `ZCARD {key}`
    - 요소 개수 확인
  - `ZRANK {key} {value}`
    - 값의 순위(오름차순)
  - `ZREVRANK {key} {value}`
    - 값의 순위(내림차순)
```

//SortedSet에 스코어는 1 값은 "score1" 저장
127.0.0.1:6379> ZADD sortedSetKey 1 "score1"
(integer) 1

//SortedSet에 스코어는 2 값은 "score2" 저장
127.0.0.1:6379> ZADD sortedSetKey 2 "score2"
(integer) 1

//SortedSet에 스코어는 3 값은 "score3" 저장
127.0.0.1:6379> ZADD sortedSetKey 3 "score3"
(integer) 1

//SortedSet에 스코어는 100 값은 "score100" 저장
127.0.0.1:6379> ZADD sortedSetKey 100 "score100"
(integer) 1

//SortedSet을 오름차순으로 조회
// 0은 왼쪽 시작
// -1은 오른쪽 시작
127.0.0.1:6379> ZRANGE sortedSetKey 0 -1 WITHSCORES
1) "score1"
2) "1"
3) "score2"
4) "2"
5) "score3"
6) "3"
7) "score100"
8) "100"

//SortedSet을 내림차순으로 조회
// 0은 왼쪽 시작
// -1은 오른쪽 시작
127.0.0.1:6379> ZREVRANGE sortedSetKey 0 -1 WITHSCORES
1) "score100"
2) "100"
3) "score3"
4) "3"
5) "score2"
6) "2"
7) "score1"
8) "1"

//SortedSet 요소 크기 확인
127.0.0.1:6379> ZCARD sortedSetKey
(integer) 4

//"score100"의 score 조회
127.0.0.1:6379> ZSCORE sortedSetKey "score100"
"100"

//"score100"의 순위 확인(내림차순)
127.0.0.1:6379> ZRANK sortedSetKey "score100"
(integer) 3

//"score100"의 순위 확인(오름차순)
127.0.0.1:6379> ZREVRANK sortedSetKey "score100"
(integer) 0

//"score100" 제거
127.0.0.1:6379> ZREM sortedSetKey "score100"
(integer) 1

//"score100"이 제거되어 내림차순 정렬시 사라짐
127.0.0.1:6379> ZRANGE sortedSetKey 0 -1 WITHSCORES
1) "score1"
2) "1"
3) "score2"
4) "2"
5) "score3"
6) "3"
```

### 5. Hash
- **Hash 주요 명령어**
  - `HSET {key} {field} {value}`
    - 특정 필드에 값 저장
  - `HGET {key} {field}`
    - 특정 필드 값 가져오기
  - `HGETALL {key}`
    - 모든 필드 가져오기
```

//특정 필드와 값을 등록
127.0.0.1:6379> HSET hashKey name "valueName"
(integer) 1

//name 필드를 조회
127.0.0.1:6379> HGET hashKey name
"valueName"

//필드-age 와 값 30을 등록 
127.0.0.1:6379> HSET hashKey age 30
(integer) 1

//age 필드 조회
127.0.0.1:6379> HGET hashKey age
"30"

//모든 필드와 값을 조회
127.0.0.1:6379> HGETALL hashKey
1) "name"
2) "valueName"
3) "age"
4) "30"
```

### 6. 유효기간(Time To Live)
- **TTL 설정 주요 명령어**
  - 데이터 생성과 동시에 TTL설정
    - `SET {key} {value} EX {seconds}`
      - 초 단위 유효시간 설정
    - `SET {key} {value} PX {milliseconds}`
      - 밀리초 단위 유효시간 설정
  - 기존 키에 TTL 설정
    - `EXPIRE key {seconds}`
      - 초 단위 유효시간 설정
    - `PEXPIRE key {milliseconds}`
      - 밀리초 단위 유효시간 설정
  - TTL 확인(남은 유효기간 조회)
    - `TTL {key}`
      - 초 단위 남은 유효시간 조회
    - `PTTL {key}`
      - 밀리초 단위 남은 유효시간 조회
  - 유효시간 제거(영구저장)
    - `PERSIST {key}`
```
//TTL이 없는 값 등록
127.0.0.1:6379> SET noTTLKey "noTTLvalue"
OK

//TTL조회 시 영구저장임으로 -1을 반환 -2를 반환할 경우 키가 없는 경우
127.0.0.1:6379> TTL noTTLKey
(integer) -1

//500SecondsTTL 키에 500초의 TTL 등록
127.0.0.1:6379> SET 500SecondsTTL "500SecondsTTL" EX 500
OK

//TTL 조회시 남은 초 반환
127.0.0.1:6379> TTL 500SecondsTTL
(integer) 486

//PTTL 조회시 남은 밀리초 반환
127.0.0.1:6379> PTTL 500SecondsTTL
(integer) 476903

//PERSIST 명령어로 해당 key 영구저장 상태로 변경
127.0.0.1:6379> PERSIST 500SecondsTTL
(integer) 1

//TTL조회 시 영구저장
127.0.0.1:6379> TTL 500SecondsTTL
(integer) -1

//EXPIRE을 통해 이미 저장된 키에 TTL 등록
127.0.0.1:6379> EXPIRE 500SecondsTTL 500
(integer) 1

//TTL 조회 시 TTL 설정 확인
127.0.0.1:6379> TTL 500SecondsTTL
(integer) 498

//만약 해당 키에 다시 TTL을 적용하면
127.0.0.1:6379> EXPIRE 500SecondsTTL 500
(integer) 1

//아래처럼 지정한 TTL이 Reset된다.
127.0.0.1:6379> TTL 500SecondsTTL
(integer) 499
```

---

## Redis를 이용한 콘서트 대기열 기능 개선

### AS-IS

**이전의 대기열은 RDB(MySql)을 데이터베이스로 사용하여 대기열 시스템을 관리**

1. 대기열 토큰 생성
   1. 사용자가 대기열 토큰생성을 요청
   2. RDB에서 활성 토큰의 개수를 조회하여 30개 미만의 경우 활성토큰 발급, 이상의 경우 대기토큰 발급
2. 대기열 토큰 정보 조회
   1. 사용자가 대기열 정보 조회 요청
   2. RDB에서 토큰 정보를 조회하고 반환
      - 만료상태의 토큰일 경우 에러 반환
      - 대기상태의 토큰일 경우 대기자 수를 조회 후 반환
3. 대기열 토큰 -> 활성화 토큰 전환(스케쥴러)
   1. 스케쥴러가 RDB에서 활성 상태의 토큰 수 조회
   2. 활성 상태 토큰이 30개 미만인 경우, 필요 수 만큼 대기 중인 토큰을 RDB에서 조회
   3. 대기열 토큰을 활성 상태로 변경
4. 만료 토큰 처리(스케쥴러)
   1. 스케쥴러가 RDB에서 주기적으로 만료된 토큰을 조회 후 만료 상태 변경
5. 결제 완료
   1. 결제완료 시 요청 시 받은 활성 토큰의 상태를 만료


### TO-BE(Look Aside - Write Around 전략)

### 기본적으로 대기열 토큰 저장소가 RDB -> Redis로 변경
- 대기열을 조회하는 메서드를 5초간 로컬캐싱하고 로컬캐시 Cache Miss시 Redis를 조회후 캐시 저장(Look Aside 전략)
- 활성상태 변경 시 Redis에 직접 쓰기가 실행되고, 조회는 캐싱이 만료되어 다시 Redis를 조회했을때 읽어올 수 있음(Write Around 전략)

### 1. 대기열 관리: RDB -> Redis Sorted Set
- **변경 사항**: Sorted Set을 활용하여, 대기열 토큰이 발급된 시간을 수치화(score)하여, score에 따라 순차적으로 데이터를 저장
- **장점**
  - Sorted Set은 데이터의 score에 따라 순서대로 저장되므로, 대기 상태의 토큰 순번을 계산하는데 용이
  - Redis는 메모리 기반 데이터베이스로 빠른 읽기/쓰기를 제공하여, 대기열 관련 작업 성능 향상

```java
//QueueRepositoryImpl
@Override
public void saveWaitingToken(String token) {
    redisTemplate.opsForZSet().add(WAITING_TOKEN_KEY, token, System.currentTimeMillis());
}

@Override
public List<String> retrieveAndRemoveWaitingToken(long count) {

    //Sorted Set으로 이미 정렬되어 저장되기 때문에 다음 순번으로 활성시킬 토큰 조회가 빠름
    Set<String> tokens = redisTemplate.opsForZSet().range(WAITING_TOKEN_KEY, 0, count - 1);
    if(tokens != null && !tokens.isEmpty()) {
        redisTemplate.opsForZSet().remove(WAITING_TOKEN_KEY, tokens.toArray());
        return tokens.stream().toList();
    }
    return List.of();
}

```

### 2. 활성 토큰 관리: RDB -> Redis Sorted Set
- **변경 사항**: 기존 RDB에서 관리하던 활성 토큰을 Redis ZSET(Sorted Set) 으로 변경하고 각 토큰의 만료 시간을 score값으로 저장하여 TTL을 관리
- **장점**
  - Redis는 메모리 기반 데이터베이스로 빠른 읽기/쓰기를 제공하여, 대기열 관련 작업 성능 향상
  - ZSet의 score을 이용하여, 개별 만료 시간 설정으로 관리에 용이
  - 활성 토큰을 Redis에서 실시간으로 변경할 수 있어 RDB에서 미 필요

```java
//QueueRepositoryImpl
//토큰을 저장할때 ZSet에 TTL Score를 저장
@Override
public void saveActiveToken(String token) {
    long expireAt = System.currentTimeMillis() + TOKEN_TTL.toMillis(); // 현재 시간 + TTL(10분)
    redisTemplate.opsForZSet().add(ACTIVE_TOKEN_KEY, token, expireAt);
}

//토큰 검증 시 score로 설정된 TTL을 검증
@Override
public boolean activeTokenExist(String token) {
    Double expireAt = redisTemplate.opsForZSet().score(ACTIVE_TOKEN_KEY, token);
    return expireAt != null && expireAt > System.currentTimeMillis(); // 현재 시간과 비교
}

//스케쥴러에서 사용되는 메서드로 TTL 이 지난 토큰 제거
@Override
public void removeExpiredTokens() {
    long now = System.currentTimeMillis();
  
    Set<String> expiredTokens = redisTemplate.opsForZSet().rangeByScore(ACTIVE_TOKEN_KEY, 0, now);
  
    if (expiredTokens != null && !expiredTokens.isEmpty()) {
      redisTemplate.opsForZSet().remove(ACTIVE_TOKEN_KEY, expiredTokens.toArray());
    }
}
```

### 3. 카운터 관리: RDB -> 활성 토큰 추적 시 Redis Sorted Set(zCard)
- **변경사항**: 기존 RDB에서 활성 토큰 개수를 관리하던 방식을 Redis ZSET(ZCARD)를 활용하여 관리
- **장점**
  - zCard(ACTIVE_TOKEN_KEY)는 O(1) 연산으로 활성 토큰 개수를 즉시 조회 가능.
  - 기존 RDB에서는 COUNT(*) 쿼리 실행 필요 (O(N)) → 성능 이점
```java
//QueueRepositoryImpl
@Override
public Long getActiveTokenCount() {
    return redisTemplate.opsForZSet().zCard(ACTIVE_TOKEN_KEY);
}
```

### 4. 만료 토큰 처리(스케쥴러)
- **변경사항**: RDB -> Redis Database로 관리
- **장점**
  - SET을 rangeByScore 활용하여 빠르게 조회 후만료된 항목 제거 (O(log N))
  - RDB 부하 감소

```java
//TokenScheduler
public class TokenScheduler {

    private final QueueService queueService;
  
    //5초 마다 갱신
    @Scheduled(fixedDelay = 5000)
    public void updateActiveToken(){
      queueService.updateActiveToken(); 
    }
  
    @Scheduled(fixedRate = 30000)  // 30초마다 실행
    public void removeExpiredTokensScheduler() {
      queueService.removeExpiredTokens();   
    }
}

//QueueService
public void updateActiveToken(){
    long activeCount = queueRepository.getActiveTokenCount();
    if(activeCount < MAX_ACTIVE_TOKENS){
        long neededTokens = MAX_ACTIVE_TOKENS - activeCount;
        List<String> waitingTokens = queueRepository.retrieveAndRemoveWaitingToken(neededTokens);
        waitingTokens.forEach(queueRepository::saveActiveToken);
    }
}

public void removeExpiredTokens() {
    queueRepository.removeExpiredTokens();
}

//QueueRepositoryImpl
@Override
public void saveActiveToken(String token) {
    long expireAt = System.currentTimeMillis() + TOKEN_TTL.toMillis(); // 현재 시간 + TTL(10분)
    redisTemplate.opsForZSet().add(ACTIVE_TOKEN_KEY, token, expireAt);
}

@Override
public List<String> retrieveAndRemoveWaitingToken(long count) {

    Set<String> tokens = redisTemplate.opsForZSet().range(WAITING_TOKEN_KEY, 0, count - 1);
    if(tokens != null && !tokens.isEmpty()) {
        redisTemplate.opsForZSet().remove(WAITING_TOKEN_KEY, tokens.toArray());
        return tokens.stream().toList();
    }
    return List.of();
}

@Override
public void removeExpiredTokens() {
    long now = System.currentTimeMillis();
  
    Set<String> expiredTokens = redisTemplate.opsForZSet().rangeByScore(ACTIVE_TOKEN_KEY, 0, now);
  
    if (expiredTokens != null && !expiredTokens.isEmpty()) {
        redisTemplate.opsForZSet().remove(ACTIVE_TOKEN_KEY, expiredTokens.toArray());
    }
}
```

### 5. 순번 계산: RDB -> Redis Sorted Set (zCard)
- **변경사항**: 
- **장점**
  - zCard(WAITING_TOKEN_KEY)는 O(1) 연산으로 활성 토큰 개수를 즉시 조회 가능.
  - 기존 RDB에서는 COUNT(*) 쿼리 실행 필요 (O(N)) → 성능 이점
```java
//QueueRepositoryImpl
@Override
public Long getWaitingTokenCount() {
    return redisTemplate.opsForZSet().zCard(WAITING_TOKEN_KEY);
}
```

---