# 서비스 장애 대응을 위한 부하 테스트

## 부하 테스트 목표

- 서비스 운영 중 예상치 못한 트래픽 증가 상황에 대비하여, **K6, Grafana, Prometheus를 활용한 부하 테스트 및 성능 모니터링**을 수행합니다.
- 이를 통해 서비스가 감당할 수 있는 최대 부하 수준을 측정하고, 대량 트래픽 발생 시 성능 저하 없이 원활하게 대응할 수 있도록 시스템을 최적화하여 안정적인 서비스 운영을 보장하는 것이 목표입니다.

<br/>

## 테스트 대상 API
> 부하테스트를 위해서 트래픽이 많이 발생할 것으로 예상되는 API를 조사하고, 해당 API를 대상으로 선정한다.

- `POST`-`/api/queues/token` : 대기열 토큰 생성
   - 해당 API는 콘서트 예약 가능 시간이 오픈될 때 가장 많은 요청이 집중될 것으로 예상되며, 단시간 내 폭발적인 트래픽 증가가 발생하는 대표적인 API
   - 대량의 동시 요청이 발생할 가능성이 높고, 각 요청이 서버에서 적절히 처리되지 않을 경우 대기열 시스템 장애 및 성능 저하로 이어질 수 있음
   - 트래픽이 급격하게 몰리는 상황에서 API의 처리 속도가 유지되며, 부하를 견디는지 검증

[//]: # (2. `GET`-`/api/queues/status` : 대기열 토큰 상태 조회)

[//]: # (   - 해당 API는 사용자가 대기열에 진입한 후 지속적으로 상태를 확인하기 위해 주기적으로 호출되며, 지속적인 트래픽 부하가 예상됨)

[//]: # (   - 토큰이 유효한 동안 일정 간격으로 지속적인 요청이 발생하며, 사용자 수가 증가할수록 부하가 점진적으로 증가할 가능성이 큼)

[//]: # (   - 특히, 새로운 사용자가 대기열에 추가되면서 요청 수가 더욱 늘어나기 때문에 트래픽이 기하급수적으로 증가할 수 있음)

[//]: # (   - 서버가 이러한 지속적인 요청을 효과적으로 처리하고 응답할 수 있는지, 캐싱 및 부하 분산 전략이 제대로 적용되는지 검증 필요)

[//]: # (3. `GET`-`/api/concerts/{concertId}/schedules` : 예약 가능 일정 조회)

[//]: # (    - 콘서트 예약을 원하는 사용자는 먼저 해당 콘서트의 예약 가능 일정을 조회해야 하며, 이 과정에서 짧은 시간 동안 다수의 사용자가 동시에 요청을 보낼 가능성이 높음)

[//]: # (    - 예약 가능 일정을 조회할 때, 단순한 조회가 아니라 데이터베이스에서 조건 범위 내의 일정 데이터를 가져와야 하기 때문에, DB 부하가 증가할 수 있음)

[//]: # (    - 사용자가 많아질수록 동시 조회 요청이 많아지고, DB 응답 시간이 지연될 경우 서버 자원이 묶이면서 전체 시스템 성능이 저하될 가능성 있음)

[//]: # (    - DB 인덱싱 및 캐싱 전략을 적절히 적용하지 않으면, 응답 지연으로 인해 API 성능이 크게 저하될 위험이 있음)

[//]: # (4. `GET`-`/api/concerts/{concertId}/schedules/{concertScheduleId}/seats` : 예약가능 좌석 조회)

[//]: # (    - 좌석 조회 API는 예약 과정에서 필수적으로 호출되는 API로, 실시간 데이터 조회가 필수적임)

[//]: # (    - 예약이 진행됨에 따라 좌석 상태&#40;예약 가능/불가능&#41;가 실시간으로 변동되며, 다수의 사용자가 동시에 좌석 정보를 조회하거나 특정 좌석을 선택할 수 있기 때문에 요청 충돌&#40;Concurrency Issue&#41;이 발생할 가능성이 높음)

[//]: # (    - 트래픽이 많아질 경우, 최신 좌석 정보를 빠르게 조회해야 하며, 실시간 응답 성능이 저하될 경우 사용자 경험에 악영향을 줄 수 있음)

[//]: # (    - 좌석 정보가 동적으로 변하는 만큼 데이터 정합성을 유지하는 것이 중요하며, 많은 트래픽 요청 상황에서 올바른 Redis 캐싱이나 DB 락이 적용되었는지 함께 검증)

<br/>

## 테스트 환경

### 하드웨어 및 시스템 환경

- 운영체제: mac
- 프로세서: Apple Silicon M4 Pro
- 메모리(RAM): 24GB
- Docker: Docker Desktop for Mac
    - Docker Engine: 27.3.1

### 테스트 및 모니터링 툴
- Prometheus: 3.2.0 
- Grafana: 11.5.2
- K6: 0.57.0


<br/>

## 테스트 시나리오 및 결과 분석

### 서론
- 테스트 대상의 API의 요청 트래픽 별 예상 TPS를 설정하고, 가용 가능 서버 대수를 설정하고, 컨테이너 자원 분배를 다르게 여러번 설정하여, 적절한 하드웨어의 스펙을 도출

- `POST`-`/api/queues/token` - 토큰 발급
  - 콘서트 예약 시간 오픈 시간을 기점으로 최대 **3만명의 유저에게 토큰 발급 요청**을 받아내야함
    - 트래픽 최고점: `30000TPS`
    - 최대 가용 서버: `50대`
    - 1대당 `600TPS`를 견뎌낼 수 있는 서버 하드웨어 도출
    - 총 10초간 테스트 진행([k6-scripts](../k6/scripts/api-queues-token.js) 참고)
      - 0~1초: `600TPS`
      - 1~3초: `500TPS`
      - 3~5초: `400TPS`
      - 5~7초: `300TPS`
      - 7~10초: `200TPS`
    - 서비스 적절 요청 응답속도 `200ms` 이내 측정

### 1번 실험 - CPU 1 / RAM 2G
```text
         /\      Grafana   /‾‾/  
    /\  /  \     |\  __   /  /   
   /  \/    \    | |/ /  /   ‾‾\ 
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/ 

     execution: local
        script: api-queues-token.js
        output: -

     scenarios: (100.00%) 1 scenario, 600 max VUs, 40s max duration (incl. graceful stop):
              * default: Up to 600 looping VUs for 10s over 5 stages (gracefulRampDown: 30s, gracefulStop: 30s)


     ✓ status is 201
     ✗ response time < 200ms
      ↳  44% — ✓ 2194 / ✗ 2781

     checks.........................: 72.05% 7169 out of 9950
     data_received..................: 891 kB 85 kB/s
     data_sent......................: 801 kB 77 kB/s
     http_req_blocked...............: avg=25.58µs  min=0s      med=3µs      max=1.81ms  p(90)=118µs    p(95)=190µs   
     http_req_connecting............: avg=17.89µs  min=0s      med=0s       max=1.22ms  p(90)=100µs    p(95)=147µs   
   ✗ http_req_duration..............: avg=302.39ms min=1.4ms   med=215.32ms max=2s      p(90)=707.09ms p(95)=893.3ms 
       { expected_response:true }...: avg=302.39ms min=1.4ms   med=215.32ms max=2s      p(90)=707.09ms p(95)=893.3ms 
   ✓ http_req_failed................: 0.00%  0 out of 4975
     http_req_receiving.............: avg=358.09µs min=6µs     med=31µs     max=94.21ms p(90)=341µs    p(95)=493.59µs
     http_req_sending...............: avg=13.81µs  min=2µs     med=9µs      max=417µs   p(90)=25µs     p(95)=36µs    
     http_req_tls_handshaking.......: avg=0s       min=0s      med=0s       max=0s      p(90)=0s       p(95)=0s      
     http_req_waiting...............: avg=302.02ms min=1.31ms  med=214.59ms max=2s      p(90)=706.96ms p(95)=893.2ms 
     http_reqs......................: 4975   475.99534/s
     iteration_duration.............: avg=802.68ms min=501.6ms med=715.5ms  max=2.5s    p(90)=1.2s     p(95)=1.39s   
     iterations.....................: 4975   475.99534/s
     vus............................: 212    min=212          max=588
     vus_max........................: 600    min=600          max=600


running (10.5s), 000/600 VUs, 4975 complete and 0 interrupted iterations
default ✓ [======================================] 000/600 VUs  10s
ERRO[0010] thresholds on metrics 'http_req_duration' have been crossed 
```

### 2번 실험 - CPU 1 / RAM 4G
```text
         /\      Grafana   /‾‾/  
    /\  /  \     |\  __   /  /   
   /  \/    \    | |/ /  /   ‾‾\ 
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/ 

     execution: local
        script: api-queues-token.js
        output: -

     scenarios: (100.00%) 1 scenario, 600 max VUs, 40s max duration (incl. graceful stop):
              * default: Up to 600 looping VUs for 10s over 5 stages (gracefulRampDown: 30s, gracefulStop: 30s)


     ✓ status is 201
     ✗ response time < 200ms
      ↳  45% — ✓ 2176 / ✗ 2630

     checks.........................: 72.63% 6982 out of 9612
     data_received..................: 860 kB 82 kB/s
     data_sent......................: 774 kB 74 kB/s
     http_req_blocked...............: avg=26.56µs  min=1µs      med=3µs      max=612µs   p(90)=128.5µs p(95)=199µs   
     http_req_connecting............: avg=18.76µs  min=0s       med=0s       max=528µs   p(90)=106µs   p(95)=153µs   
   ✗ http_req_duration..............: avg=331.81ms min=1.45ms   med=261.26ms max=2.39s   p(90)=791.2ms p(95)=1s      
       { expected_response:true }...: avg=331.81ms min=1.45ms   med=261.26ms max=2.39s   p(90)=791.2ms p(95)=1s      
   ✓ http_req_failed................: 0.00%  0 out of 4806
     http_req_receiving.............: avg=367.31µs min=6µs      med=40µs     max=83.12ms p(90)=365µs   p(95)=512.74µs
     http_req_sending...............: avg=12.72µs  min=2µs      med=9µs      max=369µs   p(90)=23µs    p(95)=34µs    
     http_req_tls_handshaking.......: avg=0s       min=0s       med=0s       max=0s      p(90)=0s      p(95)=0s      
     http_req_waiting...............: avg=331.43ms min=1.3ms    med=259.24ms max=2.39s   p(90)=790.9ms p(95)=1s      
     http_reqs......................: 4806   457.566993/s
     iteration_duration.............: avg=832.06ms min=501.63ms med=761.7ms  max=2.89s   p(90)=1.29s   p(95)=1.5s    
     iterations.....................: 4806   457.566993/s
     vus............................: 212    min=212          max=590
     vus_max........................: 600    min=600          max=600


running (10.5s), 000/600 VUs, 4806 complete and 0 interrupted iterations
default ✓ [======================================] 000/600 VUs  10s
ERRO[0011] thresholds on metrics 'http_req_duration' have been crossed 
```

### 3번 실험 - CPU 2 / RAM 2G
```text
         /\      Grafana   /‾‾/  
    /\  /  \     |\  __   /  /   
   /  \/    \    | |/ /  /   ‾‾\ 
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/ 

     execution: local
        script: api-queues-token.js
        output: -

     scenarios: (100.00%) 1 scenario, 600 max VUs, 40s max duration (incl. graceful stop):
              * default: Up to 600 looping VUs for 10s over 5 stages (gracefulRampDown: 30s, gracefulStop: 30s)


     ✓ status is 201
     ✓ response time < 200ms

     checks.........................: 100.00% 15528 out of 15528
     data_received..................: 1.4 MB  134 kB/s
     data_sent......................: 1.3 MB  120 kB/s
     http_req_blocked...............: avg=13.55µs  min=0s    med=3µs      max=726µs    p(90)=10µs     p(95)=113µs   
     http_req_connecting............: avg=8.07µs   min=0s    med=0s       max=305µs    p(90)=0s       p(95)=90µs    
   ✓ http_req_duration..............: avg=1.82ms   min=911µs med=1.44ms   max=15.87ms  p(90)=2.96ms   p(95)=3.82ms  
       { expected_response:true }...: avg=1.82ms   min=911µs med=1.44ms   max=15.87ms  p(90)=2.96ms   p(95)=3.82ms  
   ✓ http_req_failed................: 0.00%   0 out of 7764
     http_req_receiving.............: avg=53.58µs  min=6µs   med=24µs     max=1.32ms   p(90)=128µs    p(95)=190.84µs
     http_req_sending...............: avg=11.53µs  min=2µs   med=8µs      max=1.01ms   p(90)=20µs     p(95)=27µs    
     http_req_tls_handshaking.......: avg=0s       min=0s    med=0s       max=0s       p(90)=0s       p(95)=0s      
     http_req_waiting...............: avg=1.76ms   min=857µs med=1.38ms   max=15.85ms  p(90)=2.87ms   p(95)=3.75ms  
     http_reqs......................: 7764    748.028693/s
     iteration_duration.............: avg=502.12ms min=501ms med=501.68ms max=516.02ms p(90)=503.45ms p(95)=504.31ms
     iterations.....................: 7764    748.028693/s
     vus............................: 205     min=205            max=588
     vus_max........................: 600     min=600            max=600


running (10.4s), 000/600 VUs, 7764 complete and 0 interrupted iterations
default ✓ [======================================] 000/600 VUs  10s
```


### 최종 분석

| 실험 | CPU | RAM | 요청 수 (완료) | 평균 응답 시간 | 90% 응답 시간 | 95% 응답 시간 | 초당 요청 수 | 응답 200ms 미만 (%) | 실패율 |
|------|-----|-----|--------------|--------------|--------------|--------------|--------------|--------------|--------|
| 1번  | 1   | 2G  | 4975         | 302.39ms     | 707.09ms     | 893.3ms      | 475.99/s     | 44%          | 0.00%  |
| 2번  | 1   | 4G  | 4806         | 331.81ms     | 791.2ms      | 1s           | 457.57/s     | 45%          | 0.00%  |
| 3번  | 2   | 2G  | 7764         | 1.82ms       | 2.96ms       | 3.82ms       | 748.02/s     | 100%         | 0.00%  |

- 가장 낮은 성능에서도 **기준 응답속도를 미달하는 응답이 약 55%에 육박**했지만, **요청에 대한 실패는 없었다**.
- 해당 API 실험에서는 **RAM 보다 CPU를 늘리는 것**이 **더 큰 성능 향상폭**을 보인다.
- `RAM`을 늘리더라도 `CPU`처리에 병목현상이 발생하여, 실제 결과 차이를 보이지 않음(1번 vs 2번 실험)
- 실험 API의 경우, Macbook Pro M4칩인 경우, `CPU 2 / RAM 2G` `가용 서버 50대`로 최대 `30000TPS`의 트래픽을 안정적으로 처리할 수 있다. (200ms 미만)


<br/>

## 결론 및 정리

> 부하 테스트는 다양한 관점에서 임계 영역을 분석하고, 개발자가 설정해야 할 성능 지표를 결정하는 과정이다.<br/>
> 예를 들어, 고정된 하드웨어 환경에서는 실제 운영 서버와 유사한 테스트 환경을 구축하여 최대 처리 가능한 트래픽을 측정할 수 있다.<br/>
> 반면, AWS와 같은 클라우드 환경에서는 주어진 제한된 리소스 내에서 최적의 하드웨어 구성과 가용 서버 대수(오토 스케일링 정책)를 산출하는 것이 중요하다.<br/>
> 이를 통해, 효율적인 서버 운영 전략을 수립하고, 트래픽 증가에 따른 성능 저하를 사전에 예방할 수 있다.

<br/>

## (가상) 장애 및 병목현상 발생 시 해결 방안

### 현재, 토큰 저장소가 Redis -> DB인 경우
- 이미 해당 프로젝트에서는 빠른 조회를 위하여 Redis에 토큰을 저장하지만 만약, 토큰 저장소가 DB라고 가정한다면 토큰 발급 및 조회에 대한 응답속도가 현저히 느려질 것이다.
- 이에 대한 병목현상 해결방안으로는 현재 적용된 것과 같이 캐싱서버를 적용할 경우 Scale-Up 또는 Scale-Out 없이 해결할 수 있을 것이다.

### Slow Query 색출 및 DB 최적화
- Slow Query를 모니터링을 통해 찾고, 해당 쿼리에 적절하게 인덱싱과 조건문이 적절하게 작성되었는지 확인하여
- 만약, 최적화가 가능하다면 최적화를 진행하는 것 또한 병목현상을 효과적으로 해결할 수 있다.