# ELK 스택 구성과 데이터 흐름 (Filebeat 포함)

1. **Filebeat**: 

   - **경량 로그 수집기로,** 스프링 애플리케이션의 로그를 수집해 Logstash나 Elasticsearch로 전송

   - Logstash가 무겁고 설정이 복잡하다는 단점을 보완하기 위해 등장
   - **Spring 애플리케이션 내에 탑재되어 사용되는게 아니라, 로그 파일을 대상으로 동작하는 거임** 
     -> 즉, Spring에서 logback 라이브러리를 통해 log파일을 생성하게 되면, Filebeat는 해당 파일을 읽어서 Logstash나 Elasticsearch로 전송함

2. **Logstash**: 

   - **로그 데이터를 전처리하고, 필터링, 변환 작업을 수행해 Elasticsearch로 전송**
   - Logstash만으로도 로그 수집과 전처리가 가능하지만, 리소스 부담이 커질 수 있어 Filebeat를 사용해 로그를 수집하고, Logstash에서 전처리를 수행함

3. **Elasticsearch**

   - **수집된 로그 데이터를 저장하고, 검색 및 분석할 수 있도록 관리**

4. **Kibana**

   - **Elasticsearch에 저장된 데이터를 시각화하여 분석 대시보드 제공**

   

### 데이터 흐름:
- **Spring 애플리케이션에서 logback 라이브러리를 통해 log 파일 생성** 

  → **Filebeat 에서 로그 파일을 읽어 Logstash나 Elasticsearch로 전송**
  → **Logstash (선택)**
  → **Elasticsearch**
  → **Kibana**
