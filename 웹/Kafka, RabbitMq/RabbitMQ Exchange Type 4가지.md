# RabbitMQ Exchange Type 4가지

- ### 참고 사이트 : [바로가기](https://jonnung.dev/rabbitmq/2019/02/06/about-amqp-implementtation-of-rabbitmq/) 

---

### 1. 개요 

- exchange는 타입에 따라 특정한 라우팅 알고리즘을 사용하여 메시지를 적절한 큐에 전달

- RabbitMQ 시스템 동작 과정

  - ```
              Routing Key            	 Broker
    Producers      -> 		[Exchange -- Binding --> Queue] 	-> Consumers
    ```



### 2. Routing Key와 Binding Key

- **쉽게, Routing Key는 publisher가 메시지의 경로를 제시하는 키, Binding Key는 개발자가 그 경로를 받아들일 큐를 결정하는 키 **

- **Routing Key**: 메시지 publisher가 메시지를 발행할 때 지정하는 키
- **Binding Key**: 개발자가 특정 큐를 exchange에 바인딩할 때 지정하는 키



### 3. Exchange Type 

1. **Direct Exchange**

   - 메시지의 routing key가 바인딩 key와 정확하게 일치하는 경우에만 큐로 전달

   - 예를 들어, 'error' routing key를 가진 메시지는 'error' routing key로 바인딩 된 큐에만 exchange가 메시지를 보냄


2. **Fanout Exchange**

   - 바인딩된 모든 큐에 메시지를 복사하여 보냄. 이 타입의 exchange는 routing key를 무시

   - 예를 들어, 모든 워커에게 동시에 메시지를 전달해야 할 경우


3. **Topic Exchange**

   - routing key와 Binding key 사이의 패턴 매칭을 사용

     - `*` : 하나의 단어를 대체

     - `#` :  0개 이상의 단어를 대체


   - **예시**

     - 바인딩 key가 "example.*.test"인 경우 
       - Routing key가 "example.A.test"인 메시지 -> 큐로 전송
       - Routing key가 "example.A.B.test"인 메시지 -> 큐로 전송이 안됨

     - 바인딩 key가 "example.#.test"인 경우
       - Routing key가 "example.A.test"인 메시지 -> 큐로 전송
         - Routing key가 "example.A.B.test"인 메시지 -> 큐로 전송


4. **Headers Exchange**

   - routing key를 사용하지 않고, 대신 메시지 헤더의 키-값 쌍을 사용하여 메시지를 라우팅합니다. 이 타입에서는 'x-match' 헤더가 중요한 역할을 합니다. 이 헤더는 두 가지 값을 가질 수 있음

     - `all`: 바인딩의 모든 조건이 메시지의 헤더와 일치해야 합니다.

     - `any`: 바인딩의 조건 중 하나라도 메시지의 헤더와 일치하면 라우팅됩니다.


   - **예시**

     - 큐가 {'x-match': 'all', 'format': 'pdf', 'type': 'report'} 헤더로 바인딩 되어 있는 경우
       - 메시지 헤더가 최소한 {'format': 'pdf', 'type': 'report'}을 포함한다면 해당 큐로 라우팅 됨
       - 큐가 {'x-match': 'any', 'format': 'pdf'} 헤더로 바인딩 되어 있는 경우
         - 메시지 헤더가 최소한 {'format': 'pdf'} 을 포함한다면 해당 큐로 라우팅 됨 
       