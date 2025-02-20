# 카프카 설정 및 실습 보고서

<br/>

## 카프카 설정

### application.yml
```yaml
spring:
  kafka:
    # Kafka 브로커 서버 주소 설정
    bootstrap-servers: localhost:9092

    listener:
      # Kafka 메시지 수신 시 수동으로 ACK(확인 응답) 모드 사용
      # Consumer가 직접 처리 완료 후 commit 해야 함
      ack-mode: MANUAL

    consumer:
      # Kafka Consumer 그룹 ID 설정 (같은 그룹에 속한 Consumer들이 메시지를 공유)
      group-id: concert
      # Kafka 메시지 소비 시 처음부터 읽도록 설정
      auto-offset-reset: earliest
      # Consumer의 offset 자동 커밋 비활성화 (수동 커밋 방식 사용)
      enable-auto-commit: false
      # Kafka 메시지의 Key(키) Deserializer 설정 (String으로 변환)
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      # Kafka 메시지의 Value(값) Deserializer 설정 (String으로 변환)
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      properties:
        # JSON 메시지 역직렬화 시 허용할 패키지 설정 (*: 모든 패키지 허용)
        spring.json.trusted.packages: "*"

    producer:
      # Kafka 메시지의 Key(키) Serializer 설정 (String으로 변환)
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      # Kafka 메시지의 Value(값) Serializer 설정 (String으로 변환)
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
```

<br/>

### docker-compose.yml
```yaml
services:
  zookeeper:
    # Zookeeper 컨테이너 이미지
    image: confluentinc/cp-zookeeper:latest
    # 도커 컨테이너 네임
    container_name: zookeeper
    # 컨테이너가 중지되지 않는 한 계속 실행
    restart: unless-stopped
    # Mac Apple silicon 환경에서 실행될 수 있도록 플랫폼 지정
    platform: linux/amd64
    
    # 환경변수 설정
    environment:
      # 클라이언트가 Zookeeper에 연결하는 포트
      ZOOKEEPER_CLIENT_PORT: 2181
      # Zookeeper 세션 타임(기본 2000ms = 2초)
      ZOOKEEPER_TICK_TIME: 2000

  broker:
    # Kafka 브로커 컨테이너 이미지
    image: confluentinc/cp-kafka:latest
    # 컨테이너의 이름을 `broker`로 설정
    container_name: broker
    # 컨테이너가 중지되지 않는 한 계속 실행
    restart: unless-stopped
    # 호스트의 9092 포트를 컨테이너의 9092 포트에 매핑
    ports:
      - "9092:9092"
    # Zookeeper 서비스가 실행된 후에 broker(Kafka)가 실행되도록 설정
    # Zookeeper 없이 Kafka가 실행될 수 없기 때문
    depends_on:
      - zookeeper
    # 환경변수 설정
    environment:
      # Kafka 브로커 ID (클러스터 내에서 고유한 ID, 단일 브로커라 1)
      KAFKA_BROKER_ID: 1
      # Kafka가 Zookeeper(zookeeper:2181)에 연결되도록 설정
      KAFKA_ZOOKEEPER_CONNECT: 'zookeeper:2181'
      # Kafka 리스너 설정 (보안 프로토콜 매핑)
      # PLAINTEXT:PLAINTEXT -> 외부 클라이언트가 PLAINTEXT(암호화X)로 연결
      # PLAINTEXT_INTERNAL:PLAINTEXT -> 내부 컨테이너 간 통신
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_INTERNAL:PLAINTEXT
      # Kafka 가 클라이언트들에게 자신을 알릴 때 사용할 주소
      # localhost:9092 → 로컬에서 Kafka에 접근할 때 사용
      # broker:29092 → Docker 내부에서 Kafka 컨테이너 간 통신할 때 사용
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092,PLAINTEXT_INTERNAL://broker:29092
      # Kafka가 내부적으로 사용하는 __consumer_offsets 토픽의 복제 개수를 1로 설정.
      # 단일 브로커 환경에서는 1이어야 함.
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      # Kafka 트랜잭션 로그의 최소 ISR(In-Sync Replicas)과 복제 개수를 설정.
      # 단일 브로커 환경에서는 1로 설정해야 정상 작동.
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1

  create-topics:
    # Kafka 브로커 컨테이너 이미지
    image: confluentinc/cp-kafka:latest
    # 컨테이너의 호스트 이름을 create-topics로 설정
    hostname: create-topics
    # 컨테이너의 이름을 create-topics로 지정
    container_name: create-topics
    # Kafka 브로커(broker)가 실행된 후 실행되도록 설정
    # Kafka가 실행되지 않으면 토픽을 생성할 수 없기 때문에 필요함
    depends_on:
      - broker
    # cub kafka-ready -b broker:29092 1 120 -> Kafka 브로커가 정상적으로 실행될 때까지 최대 120초 대기, 1개의 브로커가 정상적으로 준비되었는지 확인 후 진행
    # kafka-topics --create --if-not-exists --bootstrap-server broker:29092 --partitions 2 --replication-factor 1 --topic concert-payment
    # Kafka 에서 concert-payment 토픽을 자동 생성
    # 토픽이 이미 존재하면 생성하지 않음 (--if-not-exists)
    # 파티션 수: 2
    # 복제 개수: 1 (단일 브로커이므로 1)
    command: "
      bash -c 'cub kafka-ready -b broker:29092 1 120 && \
      kafka-topics --create --if-not-exists --bootstrap-server broker:29092 --partitions 2 --replication-factor 1 --topic concert-payment'"
```

<br/>

  
## 통합테스트 연결 확인 및 결과
```text
// Kafka Producer가 생성됨. "Idempotent producer"는 중복 메시지 전송을 방지하는 기능이 활성화된 Producer임.
2025-02-20T04:11:56.134Z  INFO 16586 --- [hhplus] [    Test worker] o.a.k.clients.producer.KafkaProducer     : [Producer clientId=hhplus-producer-1] Instantiated an idempotent producer.

// Kafka의 버전 및 실행 정보 로깅.
2025-02-20T04:11:56.143Z  INFO 16586 --- [hhplus] [    Test worker] o.a.kafka.common.utils.AppInfoParser     : Kafka version: 3.8.1
2025-02-20T04:11:56.143Z  INFO 16586 --- [hhplus] [    Test worker] o.a.kafka.common.utils.AppInfoParser     : Kafka commitId: 70d6ff42debf7e17
2025-02-20T04:11:56.143Z  INFO 16586 --- [hhplus] [    Test worker] o.a.kafka.common.utils.AppInfoParser     : Kafka startTimeMs: 1740024716143

// Kafka ProducerInterceptor에서 메시지 본문(body)을 로깅.
2025-02-20T04:11:56.147Z  INFO 16586 --- [hhplus] [    Test worker] k.h.b.s.i.k.KafkaProducerInterceptor     : message body: {"id":1,"reservationId":1,"userId":1,"amount":100,"paymentAt":"2025-02-20 04:11:56.098","uuid":"f6f48420-ed31-45ac-b28c-17b35f8561a9"}

// Kafka ProducerInterceptor에서 메시지의 헤더(header) 정보를 로깅.
2025-02-20T04:11:56.147Z  INFO 16586 --- [hhplus] [    Test worker] k.h.b.s.i.k.KafkaProducerInterceptor     : message header: RecordHeaders(headers = [RecordHeader(key = payment-type, value = [67, 79, 77, 80, 76, 69, 84, 69, 68])], isReadOnly = false)

// Kafka 클러스터 ID 정보 로깅.
2025-02-20T04:11:56.149Z  INFO 16586 --- [hhplus] [plus-producer-1] org.apache.kafka.clients.Metadata        : [Producer clientId=hhplus-producer-1] Cluster ID: PtC79vxrSBuHMh3Gl2mmNw

// Kafka 트랜잭션 매니저가 Producer ID와 Epoch를 설정함.
2025-02-20T04:11:56.258Z  INFO 16586 --- [hhplus] [plus-producer-1] o.a.k.c.p.internals.TransactionManager   : [Producer clientId=hhplus-producer-1] ProducerId set to 0 with epoch 0

// Spring Event Listener에서 Kafka로 메시지를 전송함. (토픽: `concert-payment`, Key: `1`, Payload: 결제 정보)
2025-02-20T04:11:56.264Z  INFO 16586 --- [hhplus] [    Test worker] k.h.b.s.i.s.PaymentSpringEventListener   : Send event to Kafka: topic=concert-payment, key=1, payload={"id":1,"reservationId":1,"userId":1,"amount":100,"paymentAt":"2025-02-20 04:11:56.098","uuid":"f6f48420-ed31-45ac-b28c-17b35f8561a9"}

// ProducerInterceptor에서 메시지가 `concert-payment` 토픽의 `1번` 파티션에 저장됨을 로깅.
2025-02-20T04:11:56.283Z  INFO 16586 --- [hhplus] [plus-producer-1] k.h.b.s.i.k.KafkaProducerInterceptor     : topic: concert-payment
2025-02-20T04:11:56.283Z  INFO 16586 --- [hhplus] [plus-producer-1] k.h.b.s.i.k.KafkaProducerInterceptor     : partition: 1

// Kafka Producer가 성공적으로 메시지를 전송했음을 확인.
2025-02-20T04:11:56.294Z  INFO 16586 --- [hhplus] [plus-producer-1] k.h.b.s.i.kafka.KafkaProducerListener    : kafka producer on success topic: concert-payment, key: 1, payload: PaymentEventCommand(id=1, reservationId=1, userId=1, amount=100, paymentAt=2025-02-20T04:11:56.098, uuid=f6f48420-ed31-45ac-b28c-17b35f8561a9)

// Kafka Consumer가 `concert` 그룹으로 메시지를 소비하기 위해 브로커를 발견함.
2025-02-20T04:11:56.473Z  INFO 16586 --- [hhplus] [ntainer#0-0-C-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=consumer-concert-1, groupId=concert] Discovered group coordinator localhost:9092 (id: 2147483646 rack: null)

// Kafka Consumer가 `concert` 그룹에 재참여(re-join) 시작.
2025-02-20T04:11:56.474Z  INFO 16586 --- [hhplus] [ntainer#0-0-C-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=consumer-concert-1, groupId=concert] (Re-)joining group

// Kafka Consumer가 `concert` 그룹에 다시 참여해야 하는 이유: 
// Consumer가 특정 Member ID(`consumer-concert-1-5dd46fc9-eccc-46a2-9773-237090450de9`)를 가진 상태로 재가입해야 함.
2025-02-20T04:11:56.494Z  INFO 16586 --- [hhplus] [ntainer#0-0-C-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=consumer-concert-1, groupId=concert] Request joining group due to: need to re-join with the given member-id: consumer-concert-1-5dd46fc9-eccc-46a2-9773-237090450de9

// Kafka Consumer가 `concert` 그룹에 재참여(re-join) 시작.
2025-02-20T04:11:56.494Z  INFO 16586 --- [hhplus] [ntainer#0-0-C-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=consumer-concert-1, groupId=concert] (Re-)joining group

// Consumer가 Kafka Consumer Group에 정상적으로 연결됨.
2025-02-20T04:11:59.515Z  INFO 16586 --- [hhplus] [ntainer#0-0-C-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=consumer-concert-1, groupId=concert] Successfully joined group with generation Generation{generationId=1, memberId='consumer-concert-1-5dd46fc9-eccc-46a2-9773-237090450de9', protocol='range'}

// Kafka Consumer가 `concert` 그룹에서 파티션 할당 완료 (generation 1에서).
2025-02-20T04:11:59.522Z  INFO 16586 --- [hhplus] [ntainer#0-0-C-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=consumer-concert-1, groupId=concert] Finished assignment for group at generation 1: {consumer-concert-1-5dd46fc9-eccc-46a2-9773-237090450de9=Assignment(partitions=[concert-payment-0, concert-payment-1])}

// Kafka Consumer가 그룹 동기화 완료.
2025-02-20T04:11:59.540Z  INFO 16586 --- [hhplus] [ntainer#0-0-C-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=consumer-concert-1, groupId=concert] Successfully synced group in generation Generation{generationId=1, memberId='consumer-concert-1-5dd46fc9-eccc-46a2-9773-237090450de9', protocol='range'}

// Kafka Consumer가 새로운 파티션 할당 내용을 할당자(assignor)에 알림.
2025-02-20T04:11:59.541Z  INFO 16586 --- [hhplus] [ntainer#0-0-C-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=consumer-concert-1, groupId=concert] Notifying assignor about the new Assignment(partitions=[concert-payment-0, concert-payment-1])

// Kafka Consumer가 새로운 파티션을 할당받음.
2025-02-20T04:11:59.543Z  INFO 16586 --- [hhplus] [ntainer#0-0-C-1] k.c.c.i.ConsumerRebalanceListenerInvoker : [Consumer clientId=consumer-concert-1, groupId=concert] Adding newly assigned partitions: concert-payment-0, concert-payment-1

// Kafka Consumer가 `concert-payment-0`에 대한 커밋된 오프셋을 찾을 수 없음 (즉, 처음부터 읽어야 함).
2025-02-20T04:11:59.553Z  INFO 16586 --- [hhplus] [ntainer#0-0-C-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=consumer-concert-1, groupId=concert] Found no committed offset for partition concert-payment-0

// Kafka Consumer가 `concert-payment-1`에 대한 커밋된 오프셋을 찾을 수 없음 (즉, 처음부터 읽어야 함).
2025-02-20T04:11:59.554Z  INFO 16586 --- [hhplus] [ntainer#0-0-C-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=consumer-concert-1, groupId=concert] Found no committed offset for partition concert-payment-1

// Kafka Consumer가 `concert-payment-0`의 오프셋을 0으로 리셋 (처음부터 메시지를 소비하도록 설정).
2025-02-20T04:11:59.562Z  INFO 16586 --- [hhplus] [ntainer#0-0-C-1] o.a.k.c.c.internals.SubscriptionState    : [Consumer clientId=consumer-concert-1, groupId=concert] Resetting offset for partition concert-payment-0 to position FetchPosition{offset=0, offsetEpoch=Optional.empty, currentLeader=LeaderAndEpoch{leader=Optional[localhost:9092 (id: 1 rack: null)], epoch=0}}.

// Kafka Consumer가 `concert-payment-1`의 오프셋을 0으로 리셋 (처음부터 메시지를 소비하도록 설정).
2025-02-20T04:11:59.562Z  INFO 16586 --- [hhplus] [ntainer#0-0-C-1] o.a.k.c.c.internals.SubscriptionState    : [Consumer clientId=consumer-concert-1, groupId=concert] Resetting offset for partition concert-payment-1 to position FetchPosition{offset=0, offsetEpoch=Optional.empty, currentLeader=LeaderAndEpoch{leader=Optional[localhost:9092 (id: 1 rack: null)], epoch=0}}.

// Kafka Consumer가 concert-payment 토픽의 파티션을 정상적으로 할당받음.
2025-02-20T04:11:59.563Z  INFO 16586 --- [hhplus] [ntainer#0-0-C-1] o.s.k.l.KafkaMessageListenerContainer    : concert: partitions assigned: [concert-payment-0, concert-payment-1]

// Kafka Consumer가 메시지를 수신하고, 메시지의 헤더와 내용을 로깅.
2025-02-20T04:11:59.589Z  INFO 16586 --- [hhplus] [ntainer#0-0-C-1] k.h.b.s.i.kafka.PaymentMessageConsumer   : Received headers: {payment-type=[B@5e1c50b2, kafka_offset=0, kafka_consumer=org.springframework.kafka.core.DefaultKafkaConsumerFactory$ExtendedKafkaConsumer@56eb71cc, kafka_timestampType=CREATE_TIME, kafka_receivedPartitionId=1, kafka_receivedMessageKey=1, kafka_receivedTopic=concert-payment, kafka_receivedTimestamp=1740024716258, kafka_acknowledgment=Acknowledgment for concert-payment-1@0, kafka_groupId=concert}, payload: {"id":1,"reservationId":1,"userId":1,"amount":100,"paymentAt":"2025-02-20 04:11:56.098","uuid":"f6f48420-ed31-45ac-b28c-17b35f8561a9"}

// Kafka Consumer가 결제 완료 메시지를 정상적으로 처리함.
2025-02-20T04:11:59.594Z  INFO 16586 --- [hhplus] [ntainer#0-0-C-1] k.h.b.s.i.kafka.PaymentMessageConsumer   : ✅ 결제 완료 메시지 처리: PaymentMessagePayload(id=1, reservationId=1, userId=1, amount=100, paymentAt=2025-02-20T04:11:56.098)

// Kafka Consumer가 기존에 할당받았던 파티션을 반납.
2025-02-20T04:11:59.651Z  INFO 16586 --- [hhplus] [ntainer#0-0-C-1] k.c.c.i.ConsumerRebalanceListenerInvoker : [Consumer clientId=consumer-concert-1, groupId=concert] Revoke previously assigned partitions concert-payment-0, concert-payment-1

// Kafka Consumer가 `concert` 그룹에서 구독 해제.
2025-02-20T04:11:59.651Z  INFO 16586 --- [hhplus] [ntainer#0-0-C-1] o.s.k.l.KafkaMessageListenerContainer    : concert: partitions revoked: [concert-payment-0, concert-payment-1]

// Kafka Consumer가 그룹을 떠난다고 Kafka Coordinator에 알림.
2025-02-20T04:11:59.651Z  INFO 16586 --- [hhplus] [ntainer#0-0-C-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=consumer-concert-1, groupId=concert] Member consumer-concert-1-5dd46fc9-eccc-46a2-9773-237090450de9 sending LeaveGroup request to coordinator localhost:9092 (id: 2147483646 rack: null) due to the consumer unsubscribed from all topics

// Kafka Consumer가 그룹을 떠나면서 Generation ID와 Member ID를 초기화함.
2025-02-20T04:11:59.652Z  INFO 16586 --- [hhplus] [ntainer#0-0-C-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=consumer-concert-1, groupId=concert] Resetting generation and member id due to: consumer pro-actively leaving the group

// Kafka Consumer가 그룹에 다시 가입 요청을 보냄.
2025-02-20T04:11:59.652Z  INFO 16586 --- [hhplus] [ntainer#0-0-C-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=consumer-concert-1, groupId=concert] Request joining group due to: consumer pro-actively leaving the group

// Kafka Consumer가 모든 구독된 토픽 및 할당된 파티션을 해제.
2025-02-20T04:11:59.652Z  INFO 16586 --- [hhplus] [ntainer#0-0-C-1] o.a.k.c.c.internals.LegacyKafkaConsumer  : [Consumer clientId=consumer-concert-1, groupId=concert] Unsubscribed all topics or patterns and assigned partitions

// Kafka Consumer가 그룹을 떠난 후 다시 가입을 요청하면서 Generation ID와 Member ID를 재설정.
2025-02-20T04:11:59.653Z  INFO 16586 --- [hhplus] [ntainer#0-0-C-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=consumer-concert-1, groupId=concert] Resetting generation and member id due to: consumer pro-actively leaving the group

// Kafka Consumer가 그룹에 다시 가입 요청.
2025-02-20T04:11:59.653Z  INFO 16586 --- [hhplus] [ntainer#0-0-C-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=consumer-concert-1, groupId=concert] Request joining group due to: consumer pro-actively leaving the group

// Kafka Consumer의 Metrics 모니터링이 종료됨.
2025-02-20T04:12:00.097Z  INFO 16586 --- [hhplus] [ntainer#0-0-C-1] o.apache.kafka.common.metrics.Metrics    : Metrics scheduler closed
```

<br/>

- 메시지 Publish(Producer) 성공 로그
```java
    @Override
    public void onSuccess(ProducerRecord<String, String> producerRecord, RecordMetadata recordMetadata) {
        ProducerListener.super.onSuccess(producerRecord, recordMetadata);
        try {
            PaymentEventCommand command = objectMapper.readValue(producerRecord.value(), PaymentEventCommand.class);
            applicationEventPublisher.publishEvent(PaymentEvent.from(producerRecord.topic(), OutboxStatus.PROCESSED, command));

            log.info("kafka producer on success topic: {}, key: {}, payload: {}", producerRecord.topic(), producerRecord.key(), command);
        }catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
```
```text
//PaymentSpringEventListener가 Kafka의 concert-payment 토픽으로 메시지를 전송.
2025-02-20T04:11:56.264Z  INFO 16586 --- [hhplus] [    Test worker] k.h.b.s.i.s.PaymentSpringEventListener   : Send event to Kafka: topic=concert-payment, key=1, payload={"id":1,"reservationId":1,"userId":1,"amount":100,"paymentAt":"2025-02-20 04:11:56.098","uuid":"f6f48420-ed31-45ac-b28c-17b35f8561a9"}

//로깅을 위해 개발한 인터셉터에서 해당 토픽과 저장되는 파티션 정보 로깅
2025-02-20T04:11:56.283Z  INFO 16586 --- [hhplus] [plus-producer-1] k.h.b.s.i.k.KafkaProducerInterceptor     : topic: concert-payment
2025-02-20T04:11:56.283Z  INFO 16586 --- [hhplus] [plus-producer-1] k.h.b.s.i.k.KafkaProducerInterceptor     : partition: 1

//Kafka Producer가 성공적으로 Kafka 브로커에 전송 완료
2025-02-20T04:11:56.294Z  INFO 16586 --- [hhplus] [plus-producer-1] k.h.b.s.i.kafka.KafkaProducerListener    : kafka producer on success topic: concert-payment, key: 1, payload: PaymentEventCommand(id=1, reservationId=1, userId=1, amount=100, paymentAt=2025-02-20T04:11:56.098, uuid=f6f48420-ed31-45ac-b28c-17b35f8561a9)
```

<br/>

- 메시지 Consume(Consumer) 성공 로그
```java
@Override
    @KafkaListener(topics = "concert-payment", groupId = "concert")
    public void handle(Message<String> message, Acknowledgment acknowledgment) {
        log.info("Received headers: {}, payload: {}", message.getHeaders(), message.getPayload());
        try {
            PaymentMessagePayload paymentMessagePayload = objectMapper.readValue(message.getPayload(), PaymentMessagePayload.class);

            String paymentType = new String(message.getHeaders().get("payment-type", byte[].class), StandardCharsets.UTF_8);
            lastReceivedMessage.set(paymentMessagePayload); // ✅ 마지막으로 받은 메시지 저장


            if("COMPLETED".equals(paymentType)){
                log.info("✅ 결제 완료 메시지 처리: {}", paymentMessagePayload);

                //결제 완료 시
            } else if("CANCELLED".equals(paymentType)){
                log.info("❌ 결제 취소 메시지 처리: {}", paymentMessagePayload);

                //결제 취소 시
            }

        } catch (JsonProcessingException e) {
            log.error("Error processing Kafka message", e);
        }

    }
```
```text
// Kafka Consumer가 concert-payment 토픽에서 메시지를 정상적으로 수신함.
2025-02-20T04:11:59.589Z  INFO 16586 --- [hhplus] [ntainer#0-0-C-1] k.h.b.s.i.kafka.PaymentMessageConsumer   : Received headers: {payment-type=[B@5e1c50b2, kafka_offset=0, kafka_consumer=org.springframework.kafka.core.DefaultKafkaConsumerFactory$ExtendedKafkaConsumer@56eb71cc, kafka_timestampType=CREATE_TIME, kafka_receivedPartitionId=1, kafka_receivedMessageKey=1, kafka_receivedTopic=concert-payment, kafka_receivedTimestamp=1740024716258, kafka_acknowledgment=Acknowledgment for concert-payment-1@0, kafka_groupId=concert}, payload: {"id":1,"reservationId":1,"userId":1,"amount":100,"paymentAt":"2025-02-20 04:11:56.098","uuid":"f6f48420-ed31-45ac-b28c-17b35f8561a9"}

// PaymentMessageConsumer가 메시지를 정상적으로 처리 완료.
2025-02-20T04:11:59.594Z  INFO 16586 --- [hhplus] [ntainer#0-0-C-1] k.h.b.s.i.kafka.PaymentMessageConsumer   : ✅ 결제 완료 메시지 처리: PaymentMessagePayload(id=1, reservationId=1, userId=1, amount=100, paymentAt=2025-02-20T04:11:56.098)
```

