# SQL 쿼리 작성 및 실행 순서

출처 : https://velog.io/@onicle/SQL%EC%9E%91%EC%84%B1%EC%88%9C%EC%84%9C-vs-%EC%8B%A4%ED%96%89%EC%88%9C%EC%84%9C

### 1. SQL 쿼리 작성 순서

- `SELECT > FROM > JOIN ON > WHERE > GROUP BY > HAVING > ORDER BY > LIMIT`

### 2. SQL 쿼리 실행 순서

- `FROM > JOIN ON > WHERE > GROUP BY > HAVING > SELECT > DISTINCT > ORDER BY > OVER > LIMIT`

- 실행 순서는 크게 3개로 분할해서 생각해볼 수 있다.

  STEP 1. 테이블 **인식** : FROM
  STEP 2. 테이블 **구조** : JOIN ON -> WHERE -> GROUP BY -> HAVING (⭐중요)
  STEP 3. 테이블 **조회** : SELECT -> DISTINCT -> ORDER BY -> LIMIT



### 3. SELECT alias 적용 범위

- **GROUP BY, HAVING, ORDER BY에서 SELECT의 alias를 사용할 수 있다.**
  - GROUP BY가 SELECT 보다 먼저 실행 되지만, SELECT의 alias를 사용할 수 있다.
    (DBMS가 알아서 해줌. mysql 기준)

- **WHERE절에서는 SELECT의 alias를 사용할 수 없다. 실제 컬럼명만 사용 가능하다.**
  - 간접적인 방법은 있다. (서브쿼리 등)