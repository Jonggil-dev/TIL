# Drive 사이드 프로젝트 에러 정리

### 1. Spring과 DB 연결시 Schema 이름 설정 문제

- Postgres는 구조가 데이터베이스명 -> schema 명 -> table 이런식으로 되어 있음

- Postgres는 데이터베이스를 생성할 때 기본적으로 public이라는 schema가 생성되고, **Spring 연결시에도 schema에 관한 정보를 명시하지 않으면 public schema에 대해 쿼리를 사용**

- 하지만, 본 프로젝트에서는 sidepjt라는 schema를 만들고 연결하는 과정에서 schema를 지정해주지 않아 에러가 발생

  - 아래와 같이 url의 뒤쪽에 shcemaName을 추가하면서 해결 
  **(jpa hibernate, schema.sql 파일을 이용한 ddl 방식 모두 아래 설정 하나면 적용 됨)**
  
    ```yml
    spring:
    	datasource:
    		url: jdbc:postgresql://localhost:5432/DataBaseName?currentSchema = schemaName
  ```