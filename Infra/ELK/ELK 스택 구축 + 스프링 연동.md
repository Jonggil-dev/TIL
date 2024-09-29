# ELK 스택 구축 + 스프링 연동

### 0. 참고

- [elastic stack 구축하여 로그 수집, 관리 하기 (+ spring boot)](https://dgjinsu.tistory.com/34)

---

### 1. 사용 방법

#### **(1) Spring Boot Application에서 로그를 수집**

-  **`LoggingFilter로 request, response 로그 수집하기` TIL 참고**
-  logback 기능을 활용하여 console에 찍히는 로그들을 파일로 백업
-  Spring Boot는 `src/main/resources` 폴더에 위치는 해당 경로에 있는 `logback.xml` 파일을 자동으로 로드함
  -  대신 파일명이 `logback.xml` 이나 `logback-spring.xml`이어야 함
  -  다른 파일명으로 사용할 거면 경로를 명시적으로 설정 해주어야 함
  



#### **(2) ELK 설정 및 실행 (Docker)**

- **참고 사이트의 깃허브 ELK Stack 레포지토리 클론해서 사용하기**

- **FileBeat에 대한 설정만 추가해서 그대로 사용**

  - **docker-compose.yml에 filebeat 추가**

    ```yml
    services:  
      filebeat:
        build:
          context: filebeat/
          args:
            ELK_VERSION: ${ELASTIC_VERSION}
        volumes:
       	  #Spring 로그가 있는 홈 디렉토리와 볼륨 마운팅
          - Spring 로그가 있는 홈 디렉토리 경로 :/var/logs/spring_logs:ro
          
          #filebeat.yml 설정을 입히기 위한 볼륨 마운팅
          - ./filebeat/filebeat.yml:/usr/share/filebeat/filebeat.yml
        networks:
          - elk
        depends_on:
          - logstash
    ```
  
    
  
  - **filebeat 디렉토리 만들기**
  
    - **filebeat.yml 작성**
  
      - 수집할 로그 파일이 있는 경로 작성 방법(yml의 path 부분)
        1. 디렉토리 구분 
           - A : 로그 파일을 남겨둘 홈 디렉토리 
           - B : Spring 컨테이너에서 생성되는 log 파일의 디렉토리 
           - C : filebeat 컨테이너에서 로그 파일에 접근하기 위한 디렉토리
        2. 볼륨 마운팅을 아래와 같이 진행
           - A : B -> A : C
           - 이렇게 해야 B에서 생성된 로그가 A에 저장되고 A에 저장된 로그를 C 에서 읽을 수 있음
  
      ```yml
      filebeat.inputs:
        - type: log
          enabled: true
         
          # 수집할 로그 파일이 있는 경로 (위의 A에 해당하는 디렉토리)
          paths:
            - /var/logs/spring_logs/*.log
            
      output.logstash:
        hosts: ["logstash:5044"]
      
      ```
  
    
  
    - **Dockerfile 작성**
  
      ```dockerfile
      ARG ELK_VERSION
       
      FROM docker.elastic.co/beats/filebeat:${ELK_VERSION}
       
      COPY filebeat.yml /usr/share/filebeat/filebeat.yml
      USER root
       
      RUN mkdir /var/logs
       
      RUN chown -R root /usr/share/filebeat
      ```