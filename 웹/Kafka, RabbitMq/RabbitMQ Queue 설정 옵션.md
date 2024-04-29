# RabbitMQ Queue 설정 옵션

- ### 참고 사이트  : [바로 가기](https://jonnung.dev/rabbitmq/2019/02/06/about-amqp-implementtation-of-rabbitmq/) 

---



### 1. Virtual Host (vhost)

- RabbitMQ 서버 내에서 독립적인 이름 공간을 제공
- 각 Virtual Host는 자체적인 권한, 큐, 교환기 및 바인딩을 가짐
- publisher 입장에서는 RabbitMQ 를 연결할 때 AMQP URL에 Virtual Host 이름을 포함시켜 지정함
- (ex) 여러 개발 팀이 같은 RabbitMQ 인스턴스를 사용하고 각 팀의 환경 설정을 서로 격리하고 싶을 때 Virtual Host를 다르게 설정

### 2. Type
- **Default for virtual host**: Virtual Host에 설정된 기본 큐 유형을 사용. 이는 주로 Classic 큐가 기본값
- **Classic**: 전통적인 RabbitMQ 큐 유형. Classic 큐는 높은 처리량과 낮은 지연 시간을 제공하지만, 클러스터 간의 메시지 복제는 지원하지 않음
- **Quorum**: 높은 가용성을 필요로 하는 분산 환경에 적합한 큐 유형. Quorum 큐는 Raft 합의 알고리즘을 사용하여 노드 간에 메시지 상태를 복제하며, 데이터 무결성과 일관성을 보장. 이 유형은 네트워크 분할이나 서버 장애가 발생해도 메시지 손실을 최소화할 수 있음
- **Stream**: 스트리밍 데이터를 처리하기 위해 설계된 큐 유형으로, 매우 높은 처리량을 제공합니다. Stream 큐는 데이터를 'segments'로 저장하여 디스크 기반의 내구성을 제공하며, 일반적인 메시징과는 다른 성능 특성을 가집니다. 이 유형은 대규모 데이터 스트림이나 시간 순서에 따른 메시지 처리가 중요한 애플리케이션에 적합

### 3. Durability (내구성)
- RabbitMQ 서버의 재시작에도 큐가 지속되어야 하는지 여부를 결정. durable 큐는 서버 재시작 후에도 존재하지만, transient 큐는 재시작 시 삭제
- durable 큐는 RabbitMQ 서버가 재시작된 후에도 큐 자체는 유지되지만, 큐 안에 있는 메시지들도 유지되게 하려면 메시지를 보낼 때 `persistent` 옵션을 `true`로 설정 해야 됨. 이렇게 설정된 메시지는 디스크에 저장되어, 서버가 재시작되더라도 유실되지 않음

### 4. Arguments
- 이 옵션은 큐의 동작을 더 세밀하게 제어하는데 사용. 예를 들어, 메시지 TTL(Time-To-Live), 큐의 최대 길이, 우선 순위 큐 설정 등을 포함
- **예시**
  - **x-message-ttl**: 큐에 있는 메시지가 삭제되기 전까지 유지되는 시간(밀리초)을 지정합니다.
  - **x-max-length**: 큐에 저장할 수 있는 메시지의 최대 수를 지정합니다.
  - **x-queue-mode**: 큐의 모드를 설정하여, 메모리에 메시지를 저장할 것인지 디스크에 저장할 것인지를 결정합니다 ('lazy' 모드에서는 디스크에 메시지가 저장됩니다)
