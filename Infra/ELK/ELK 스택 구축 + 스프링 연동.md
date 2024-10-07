# ELK 스택 구축 + 스프링 연동

### 0. 참조

- [elastic stack 구축하여 로그 수집, 관리 하기 (+ spring boot)](https://dgjinsu.tistory.com/34)
- [깃허브 docker-elk 레포](https://github.com/deviantony/docker-elk)

---



### **1. Spring Boot Application에서 로그 수집**

-  **`LoggingFilter로 request, response 로그 수집하기` TIL 참고**
-  `logback` 기능을 활용하여 로그를 파일에 기록

   -  **[주의 !] 콘솔과 파일에 출력되는 로그는 독립적으로 동작하는 거임**
      -  로그를 남기려면 `logger.info` 같은 로거 호출이 필요하지만, 이 로그가 콘솔에 출력될지, 파일에 기록될지는 Appender 설정에 따라 다름
      -  처음에 콘솔에 출력되는 로그가 파일로 백업되는 원리인 줄 알았음
-  Spring Boot는 `src/main/resources` 폴더에 위치는 해당 경로에 있는 `logback.xml` 파일을 자동으로 로드함
  -  대신 파일명이 `logback.xml` 이나 `logback-spring.xml`이어야 함
  -  다른 파일명으로 사용할 거면 경로를 명시적으로 설정 해주어야 함

  

### **2. ELK 설정 (Docker)**

- #### **깃허브 docker-elk 레포지토리 클론해서 수정 및 사용**

- #### ElasticSearch

  1. **클러스터 통신 관련 에러로 컨테이너 실행이 안됨 -> single node로 수정**

     - 추후 필요 시 node 추가하면서 클러스터 구축하기
     
     - `elasticserach.yml`에 아래 코드 추가
     
       ```yml
       # 노드 1개만 사용하겠다는 뜻 (discovery 없음)
       discovery.type: single-node
       ```
  
    2. **한국어 처리를 위한 플러그인 반영**
  
       -  `Elasticsearch Dockerfile`에 아래 코드 추가
  
         ```dockerfile
         # 한국어 처리를 위한 필수 플러그인
         RUN elasticsearch-plugin install analysis-icu
         ```
  
         
  
- #### LogStash

  1. **`pipeline/logstash.conf 수정`**

     - `index` : 쉽게 생각하면 내가 보내는 데이터를 해당 index(테이블)에 저장해라

     - `user, password`
       - elasticsearch에 설정 해둔 user, password로 작성
         - user의 경우 elasticsearch가 내장 기능으로 `elastic` 이라는 슈퍼유저를 만듬 (별도 설정 필요 없음)
         - password의 경우 elasticsearch에 설정한 비밀번호와 동일해야 됨 
     - `ssl => false` 이거 안하면 filebeat에서 logstash로 연결이 자꾸 거부당함
       - 원인은 모르겠음
     
     
     ```yml
     input {
     	beats {
     		port => 5044
     		ssl => false
     	}
     
     	tcp {
     		port => 50000
     	}
     }
     
     ## Add your filters / logstash plugins configuration here
     
     output {
     	elasticsearch {
     			hosts => "elasticsearch:9200"
     			index => "logstash-%{+yyyy.MM.dd}"
     			user => "elastic"
     			password => "${LOGSTASH_INTERNAL_PASSWORD}"
     			ecs_compatibility => disabled
     	}
     }
     ```



- #### FileBeat (기존에 있는 /extensions/filebeat 수정해서 사용)

  1. **`/{repository}/extensions/filebeat`를 `/{repository}/filebeat` 로 이동**

  2. **docker-compose.yml에 filebeat 추가 **
  
     - **/filebeat 디렉토리 안에 있는 compose.yml 파일 복붙 및 수정**
       - 디렉토리 구분 
         - A : Spring 로그 파일을 남겨둘 호스트 환경 디렉토리 
         - B : Spring 컨테이너 내부에서 생성되는 log 파일 디렉토리 
         - C : filebeat 컨테이너 내부에서 호스트 Spring 로그 파일에 접근하기 위한 디렉토리
       - 볼륨 마운팅을 아래와 같이 진행
         - A : B -> A : C
         - 이렇게 해야 B에서 생성된 로그가 A에 저장되고 A에 저장된 로그를 C 에서 읽을 수 있음
  
     ```yml
       filebeat:
         container_name: filebeat
         build:
           context: filebeat/
           args:
             ELASTIC_VERSION: ${ELASTIC_VERSION}
         user: root
         command:
           - -e
           - --strict.perms=false
         volumes:
           - /c/Users/정종길/Desktop/sidepjt/log:/var/logs/spring_logs:ro
           - ./filebeat/config/filebeat.yml:/usr/share/filebeat/filebeat.yml:ro,Z
           - type: bind
             source: /var/lib/docker/containers
             target: /var/lib/docker/containers
             read_only: true
           - type: bind
             source: /var/run/docker.sock
             target: /var/run/docker.sock
             read_only: true
         environment:
           FILEBEAT_INTERNAL_PASSWORD: ${FILEBEAT_INTERNAL_PASSWORD:-}
           BEATS_SYSTEM_PASSWORD: ${BEATS_SYSTEM_PASSWORD:-}
         networks:
           - elk
         depends_on:
           - logstash
     ```
  
  
  
  3. **`/config/filebeat.yml` 작성**
  
     - paths : 수집할 로그 파일 경로 작성 
  
     ```yml
     name: filebeat
     
     filebeat.inputs:
       - type: log
         enabled: true
         paths:
           - /var/logs/spring_logs/*.log
     
     output.logstash:
       hosts: ["logstash:5044"]
     
     
     http:
       enabled: true
       host: 0.0.0.0
     ```
  
     
  
  4. **Dockerfile 작성**
  
     -  기존 `/extensions/filebeat` 에 있는 거 사용
  
     ```dockerfile
     ARG ELASTIC_VERSION
     
     FROM docker.elastic.co/beats/filebeat:${ELASTIC_VERSION:-8.15.1}
     ```



### 3. 실행

- `docker compose build`
- `docker compose --profile setup up -d`



### 4. 주의 사항

- **setup 컨테이너는 인증서 및 비밀번호 생성이 완료되면 컨테이너가 의도적으로 종료 됨**
  **(https://www.elastic.co/kr/blog/getting-started-with-the-elastic-stack-and-docker-compose**)

