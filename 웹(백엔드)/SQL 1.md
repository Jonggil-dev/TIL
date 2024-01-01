# SQL 1

### 1. 데이터베이스

- 체계적인 데이터 모음
- 역할 : 데이터를 저장(구조적 저장)하고 조작(CRUD)
- 데이터베이스의 등장 배경 : 데이터를 잘 저장하고 조작하기 위해 등장


### 2. 관계형 데이터 베이스

- 데이터 간에 관계(여러 테이블 간의 논리적 연결)가 있는 데이터 항목들의 모음
- 서로 관련된 데이터 포인터를 저장하고 이에 대한 액세스를 제공
- 이 관계로 인해 두 테이블을 사용하여 데이터를 다양한 형식으로 조회할 수 있음
- **외래 키를 사용하여 각 행에서 서로 다른 테이블 간의 관계를 만들 수 있음. 기본 키 또는 외래 키를 통해 결합(join)될 수 있는 여러 테이블에 걸쳐 구조화 됨**


### 3. 관계형 데이터 베이스 관련 키워드

- Table(a.k.a Relation)
  - 행과 열을 사용해서 데이터를 기록하는 곳

- Field(a.k.a Column,Attribute)
  - 각 필드에는 고유한 데이터 형식(타입)이 지정됨
- Record(a.k.a Row, Tuple)
  - 각 레코드에는 구체적인 데이터 값이 저장됨
- Database(a.k.a Schema)
  - 테이블의 집합
- Primary Key(기본 키)
  - 각 레코드의 고유한 값
  - 관계형 데이터베이스에서 레코드의 식별자로 사용
- **Foreign Key(외래 키)**
  - **테이블의 필드 중 다른 테이블의 레코드를 식별할 수 있는 키**
  - 일반적으로 다른 테이블의 기본 키를 참조
  - **각 레코드에서 서로 다른 테이블 간의 관계를 만드는 데 사용**



### 4. DBMS(Database Management System)

- 데이터베이스를 관리하는 소프트웨어 프로그램
- 데이터 저장 및 관리를 용이하게 하는 시스템
- 데이터베이스와 사용자 간의 인터페이스 역할
- 사용자가 데이터 구성,업데이트,모니터링,백업,복구 등을 할 수 있도록 도움



### 5. SQL(Structure Query Language)

- 데이터베이스에 정보를 저장하고 처리하기 위한 프로그래밍 언어

- SQL 키워드는 대소문자를 구분하지 않음
  - 하지만 대문자로 작성하는 것을 권장(명시적 구분)

- 각 SQL Statements의 끝에는 세미콜론(;)이 필요
  - 세미콜론은 각 SQL Statements을 구분하는 방법(명령어의 마침표)

- 각 DBMS마다 독자적인 기능에 따라 SQL 표준을 벗어나는 문법이 존재하니 주의할 것

### 6. SQL Statements

- SQL을 구성하는 가장 기본적인 코드 블록

- 예시

  ```sql
  SELECT column_name FROM table_name;
  ```

  - 위 예시 코드는 SELECT Statement라 부름
  - 이 Statement는 SELECT, FROM 2개의 keyword로 구성 됨

- 수행 목적에 따른 SQL Statements 4가지 유형

|                 유형                  |                  역할                  | SQL 키워드                     |
| :-----------------------------------: | :------------------------------------: | ------------------------------ |
|  DDL<br />(Data Definition Language)  |    데이터의 기본 구조 및 형식 변경     | CREATE<br />DROP<br />ALTER    |
|    DQL<br />(Data Query Language)     |              데이터 검색               | SELECT                         |
| DML<br />(Data Manipulation Language) |  데이터 조작<br />(추가, 수정, 삭제)   | INSERT<br />UPDATE<br />DELETE |
|   DCL<br />(Data Control Language)    | 데이터 및 작업에 대한 사용자 권한 제어 | COMMIT                         |



### 7. Query

- **데이터베이스로부터 정보를 요청** 하는것
- 일반적으로 SQL로 작성하는 코드를 쿼리문(SQL문)이라 함



### 8. SELECT statement

- 테이블에서 데이터를 조회 및 반환

- ```sql
  SELECT
  	select_list
  FROM
  	table_name;
  ```

  - SELECT 키워드 이후 데이터를 선택하려는 필드를 하나 이상 지정
  - FROM 키워드 이후 데이터를 선택하려는 테이블의 이름을 지정

- **SELECT statement 실행 순서**
  - 큰 틀은 테이블에서(FROM) → 조건 반영 후 → 조회하여(SELECT) → 정렬(ORDER BY) → 특정 위치 값 반환(LIMIT)  
   ![5](https://github.com/JeongJonggil/TIL/assets/139416006/87031245-8e32-43fe-8099-6e34d5ccbc4b)

- Filtering data 관련 Keywords 

  ![6](https://github.com/JeongJonggil/TIL/assets/139416006/de306fab-134d-4eaf-9a4f-05027993fedf)

  - LIKE

    - 값이 특정 패턴에 일치하는지 확인(Wildcards와 함께 사용)
    - Wildcards 
      - % : 0개 이상의 문자열과 일치하는지 확인
      - _ : 단일 문자와 일치하는지 확인 

  - LIMIT

    - 조회하는 레코드 수를 제한

      ```sql 
      SELECT
        ~
      FROM
        ~
      LIMIT 2, 5; (앞에 2개 레코드는 건너뛰고 3번째 부터 5개 선택)
      ```

  - GROUP BY

    - 레코드를 그룹화하여 요약본 생성('집계 함수'와 함께 사용)

    - 집계 함수 : 값에 대한 계산을 수행하고 단일한 값을 반환하는 함수 (SUM, AVG, MAX, MIN, COUNT)

    - where가 있을 경우 where 뒤에 와야 됨

    - **Default으로 항목의 중복을 제거하고 오름차순으로 Grouping 해줌**

      ```sql
      SELECT
      	Country, COUNT(*)
      FROM
      	customers
      GROUP BY
      	Country;
      ```

      

- 예시

```sql
-- 01. Querying data

		-- # 단일 필드 조회
			SELECT
				LastName
			FROM
				employees;

		-- # 복수 필드 조회
			SELECT
				LastName,FirstName
			FROM
				employees;

		-- # 모든 필드 조회
			SELECT
				*
			FROM
				employees;

		-- # AS : 출력 필드명 이름 변경
			SELECT
				FirstName AS '이름'
			FROM
				employees;


		-- # 특정 필드 값 연산, 필드명 변경
			SELECT
				Name, 
				Milliseconds / 60000 AS '재생 시간(분)'
			FROM
				tracks;

-- 02. Sorting data

		-- # employees에서 FirstName 필드의 모든 데이터를 오름차순 조회
		-- # Default값이 오름차순이라 ASC는 안적어도 오름차순 정렬 됨
			SELECT
				FirstName
			FROM
				employees
			ORDER BY
				FirstName ASC;

		-- # employees에서 FirstName 필드의 모든 데이터를 내림차순 조회
			SELECT
				FirstName
			FROM
				employees
			ORDER BY
				FirstName DESC;
		
		-- # customers에서 Country 필드를 기준으로 내림차순 한 다음 City필드 기준으로 오름차순 정렬
		-- # 내림차순 된 Counry 필드 각 항목 내에서 City 필드가 오름차순 됨
			SELECT
				Country, City
			FROM
				customers
			ORDER BY
				Country DESC,
				City ASC;

		-- # tracks에서 Milliseconds 필드 기준으로 내림차순 한 다음 Name, Milliseconds 필드의 모든 데이터 조회
		-- # Milliseconds 필드는 60000으로 나눠 분 단위 값으로 출력
			SELECT
				Name,Milliseconds / 60000 AS '재생 시간(분)'
			FROM
				tracks
			ORDER BY
				Milliseconds DESC;


-- NULL 정렬 예시

		-- #NULL 값이 존재할 경우 오름차순 정렬 시 결과에 NULL이 먼저 출력
			SELECT
				ReportsTO
			FROM
				employees
			ORDER BY
				ReportsTO; 


-- 03. Filtering data

		--# DISTINCT Filtering : 중복값 제거
			--# customers에서 Country 필드의 모든 데이터를 중복없이 오름차순 조회
				SELECT DISTINCT
					Country
				FROM
					customers
				ORDER BY
					Country;

		--# WHERE Filtering : 데이터 값 중 특정 조건 조회
			--# customers에서 City 필드 값이 'Prague'인 데이터의 LastName, FirstName, City 조회
				SELECT
					LastName, FirstName, City
				FROM
					customers
				WHERE
					City = 'Prague';

			--# customers에서 City 필드 값이 'Prague'가 아닌 데이터의 LastName, FirstName, City 조회
				SELECT 
					LastName, FirstName, City
				FROM
					customers
				WHERE
					City != 'Prague';
	
  
			--# customers에서 Company 필드 값이 NULL이거나 Country 필드 값이 'USA'인 데이터의 LastName,FirstName,Company,Country 조회
			--# NULL의 경우 데이터 타입을 나타내기 때문에 '=' 말고 IS를 사용해서 비교함	
				SELECT
					LastName, FirstName, Company, Country
				FROM
					customers
				WHERE
					Company IS NULL
					OR Country = 'USA';

			--# tracks에서 Bytes 필드 값이 100000 이상 500000 이하인 데이터의 Name, Bytes 조회
			--# 100000 <= Bytes <= 500000; -> 이거는 정상적으로 동작 안됨(X)
				SELECT
					Name, Bytes
				FROM
					tracks
				WHERE
					Bytes BETWEEN 100000 AND 500000;
					-- Bytes >= 100000 
					-- AND Bytes<= 500000; 이거도 됨

			--# tracks에서 Bytes 필드 값이 100000 이상 500000 이하인 데이터의 Name, Bytes를 Bytes를 기준으로 오름차순 조회
			--#	ORDER BY 뒤에 WHERE가 나오면 동작 안함
				SELECT
					Name, Bytes
				FROM
					tracks
				WHERE
					Bytes BETWEEN 100000 AND 500000 
				ORDER BY
					Bytes;
			
			--# customers에서 Country 필드 값이 'Canada'또는 'Germany'또는 'Farance'인 데이터의 LastName,FirstName,Country 조회
				SELECT
					LastName, FirstName, Country
				FROM
					customers
				WHERE
					Country IN ('Canada','Germany','France');
				-- 이거도 가능
				--	Country = 'Canada'
				--	OR Country = 'Germany'
				--	OR Country = 'France';

			--# customers에서 Country 필드 값이 'Canada'또는 'Germany'또는 'Farance'가 아닌 데이터의 LastName,FirstName,Country 조회
				SELECT
					LastName, FirstName, Country
				FROM
					customers
				WHERE
					Country NOT IN ('Canada','Germany','France');
						
			--# customers에서 LastName 필드 값이 son으로 끝나는 데이터의 LastName,FirstName 조회
				SELECT
					LastName, FirstName
				FROM
					customers
				WHERE
					LastName LIKE '%son';

			--# customers에서 FirstName 필드 값이 4자리면서 'a'로 끝나는 데이터의 LastName,FirstName 조회
				SELECT
					LastName, FirstName
				FROM
					customers
				WHERE
					FirstName LIKE '___a';
				
		--# LIMIT	Filtering
			--# tracks에서 TrackId,Name,Bytes 필드 데이터를 Bytes 기준 내림차순으로 7개만 조회
				SELECT
					TrackId, Name, Bytes
				FROM
					tracks
				ORDER BY
					Bytes DESC
				LIMIT 7;

			--# tracks에서 TrackId,Name,Bytes 필드 데이터를 Bytes 기준 내림차순으로 4번째부터 7번째 데이터만 조회
				SELECT
					TrackId, Name, Bytes
				FROM
					tracks
				ORDER BY
					Bytes DESC
				LIMIT 3,4;
				-- LIMIT 4 OFSET 3; 이렇게도 사용 가능


-- 04. Grouping data
		-- customers에서 각 country항목 별로 Groupying + 항목 별 데이터 갯수 counting 필드 추가
			SELECT
				Country, COUNT(*)
			FROM
				customers
			GROUP BY
				Country;

		-- tracks에서 Composer 필드를 그룹화하여 각 그룹에 대한 Bytes의 평균 값을 내림차순 조회
			SELECT
				Composer, AVG(Bytes)
			FROM
				tracks
			GROUP BY
				Composer
			ORDER BY
				AVG(Bytes) DESC;

		-- tracks에서 Composer 필드를 그룹화하여 각 그룹에 대한 Milliseconds의 평균 값이 10 미만인 데이터 조회
		-- 단, Milliseconds 필드는 60000으로 나눠 분 단위 값의 평균으로 계산
			SELECT
				Composer, AVG(Milliseconds/60000) AS avgOfMinute
			FROM
				tracks
			GROUP BY
				Composer
			HAVING 				-- Having : 집계 항목에대한 세부 조건을 지정, Group by에서 where 조건을 쓸때는 Having을 사용해야 됨
				avgOfMinute < 10;



```

