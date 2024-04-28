# SQL 2

### 1. DDL (Data Definition Language)

- 데이터의 기본 구조 및 형식 변경 (주로 필드와 연관)

**→ DDL은 테이블의 필드를 변경, DML은 레코드 수정 느낌**

- CREATE, DROP, ALTER 등

- 예시

  ```sqlite
  -- 테이블 생성(CREATE TABLE)
  -- '필드명-데이터타입-제약조건-추가옵션 키워드' 순으로 작성
      CREATE TABLE examples (
          ExamID INTEGER PRIMARY KEY AUTOINCREMENT, --ATUONINCREMENT : 자동 증가
          LastName VARCHAR(50) NOT NULL,
          FirstName VARCHAR(50) NOT NULL
      );
  
  -- 테이블 구조에 관한 정보 확인(Sqlite3)
  -- cid --> 컬럼 아이디
  PRAGMA table_info('examples');
  
  
  -- 테이블 구조 변경(ALTER TABLE)
      -- 필드 추가
      -- sqlite는 ADD COLUMN에 여러개 컬럼 넣기 안됨
      ALTER TABLE
          examples
      ADD COLUMN
          Country VARCHAR(50) NOT NULL DEFAULT 1;
          
      ALTER TABLE
          examples
      ADD COLUMN
          Age INTEGER NOT NULL DEFAULT 1;
          
      ALTER TABLE
          examples
      ADD COLUMN
          Address VARCHAR(50) NOT NULL DEFAULT 1;
  
      -- 필드명 변경
      ALTER TABLE
          examples
      RENAME COLUMN
          Address TO PostCode;
      
      -- 필드 삭제 (SQLite 버전에 따라 DROP COLUMN 직접 사용 불가능 함) -> 그냥 버전 업 시켜서 필드 삭제만 가능한 버전을 사용하기
  		-- 그게 아니면 삭제할 필드를 제외한 새로운 복제 테이블 생성 후 기존 테이블을 지우는 방식으로 사용해야함
      -- SQLite에서 CREATE TABLE ... AS SELECT ... 문을 사용하면, 새로 생성된 테이블은 원본 테이블의 데이터는 가지고 오지만, 원본 테이블의 여러 속성과 제약 조건(예: PRIMARY KEY, UNIQUE, NOT NULL 등)은 상속받지 않음.
      -- SQLite에서는 제약 조건이나 인덱스를 포함하여 구조를 그대로 복사하는 내장 기능이 없어서 테이블에 이러한 제약조건을 적용하려면, 처음에 테이블을 생성할 때 명시적으로 이러한 제약조건을 지정해주어야 함 
      -- 결론은 CREATE TABLE... AS SELECT ...로 복제하면 안되고 CREATE TABLE로 필드명, 속성,제약사항 동일하게 하나씩 다 만들어서 데이터만 복제시켜야됨 -> 굉장히 비효율적
          -- examples의 Country 필드 삭제
              ALTER TABLE
                  examples
              DROP COLUMN
                  Country;
  
  		-- 테이블 삭제
  			DROP TABLE examples;
  ```

  

※ 주의 ※ 

> SQLite은 테이블이 이미 만들어진 후에는 제약조건을 수정할 수 없다. 만약 데이터를 보존하면서 테이블에 제약조건만 바꾸고 싶은 경우에는 새 테이블을 만들어서 데이터를 복사한 후 이전 테이블을 삭제하는 방법을 사용해야 함
>
> 1. 기존 테이블 스키마에서 제약조건을 포함한 새로운 스키마를 이용해 새 테이블을 만든다. 
>
>  \- 기존 테이블[example] 스키마
>
> ```sql
> CREATE TABLE example(
>     id varchar(12)
>     name varchar(12),
>     age varchar(2)
> );
> ```
>
>  \- 새 테이블[new_example] 스키마
>
> ```sql
> CREATE TABLE new_example(
>     id varchar(12)
>     name varchar(12),
>     age varchar(2),
>     PRIMARY KEY(id)
> );
> ```
>
> 2. 기존 테이블에서 새 테이블로 모든 데이터를 복사한다.
>
> ```sql
> INSERT INTO new_example SELECT * FROM example;
> ```
>
> 3. 기존 테이블을 삭제한다.
>
> ```sql
> DROP TABLE example;
> ```
>
> 4. 새 테이블의 이름을 기존 테이블의 이름으로 바꾼다.
>
> ```sqlite
> ALTER TABLE new_example RENAME TO example;
> ```



### 2. 타입 선호도(참고)

![1](https://github.com/JeongJonggil/TIL/assets/139416006/0f5ab2d9-8751-4efd-ad71-eb3bbb026c7d)
![2](https://github.com/JeongJonggil/TIL/assets/139416006/6b3a157d-7aaa-43cb-a11a-57494a66053e)


### 3. DML(Data Manipulation Language)

- 데이터 조작(레코드 추가,수정,삭제)

  **→ DDL은 테이블의 구조를 변경, DML은 레코드 수정 느낌**

- INSERT, UPDATE, DELETE 등

- **실무에서는 데이터를 잘 지우지 않기 때문에 DB에서의 문제는 데이터를 수정하는 과정에서 많이 발생함**

- 예시

  ```sqlite
  -- INSERT INTO (레코드 입력) 
      -- TABLE 생성
      CREATE TABLE articles (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      title VARCHAR(100) NOT NULL,
      content VARCHAR(200) NOT NULL,
      createdAt DATE NOT NULL
      );
  
      --TABLE 구조 확인
      PRAGMA table_info('articles');
  
      -- DATA 입력
      INSERT INTO
          articles(title,content, createdAT)
      VALUES
          ('title1','content1','2023-10-10'),
          ('title2','content2','2023-10-11'),
          ('title3','content3','2023-10-12');
  
      -- DATA 입력
      INSERT INTO
          articles(title,content, createdAT)
      VALUES
          ('title4','content4',DATE());
  
  
  -- UPDATE ... SET (레코드 수정)
      --4번 id를 기준으로 title, content수정 하고 id 기준 내림차순 조회
      UPDATE
          articles
      SET
          title = 'update TITLE',
          content = 'update content'
      WHERE
          id = 4;
  
      SELECT
          *
      FROM
          articles
      ORDER BY
          id DESC;
  
  -- DELETE FROM .. [WHERE condition](레코드 삭제)
      DELETE FROM albums
      WHERE AlbumID =5;
  
      -- articles에서 created가 가장 오래된 2개 레코드 지우기
      DELETE FROM articles
      WHERE createdAT IN (SELECT createdAT FROM articles ORDER BY createdAT
      LIMIT 2);
  ```

### 

### 4. 관계

- 관계 : 여러 테이블 간의 (논리적) 연결
  - 관계의 필요성 : 중복되는 검색어를 조회하는 경우나, 중복되는 특정 데이터가 수정 될 때 곤란해짐
  - 테이블을 나누어 외래키를 연결하여 관리하고자 함이 관계의 필요성임
- **JOIN이 필요한 순간**
  - 테이블을 분리하면 데이터 관리는 용이해질 수 있으나 출력시에는 문제가 있음
  - 테이블 한 개 만을 출력할 수 밖에 없어 다른 테이블과 결합하여 출력하는 것이 필요해짐

### 5. JOIN

- 둘 이상의 테이블에서 데이터를 검색하는 방법

**(1) INNER JOIN**

![1](https://github.com/JeongJonggil/TIL/assets/139416006/03fb31e5-60ad-4bcf-a422-ff85ee2f016a)


```sqlite
#INNER JOIN syntax
SELECT
	select_list
FROM
	table_a
INNER JOIN table_b
	ON table_b.fk = table_a.pk;
	
# 1. FROM절 이후 메인 테이블 지정(table_a)
# 2. INNER JOIN 절 이후 메인 테이블과 조인할 테이블을 지정(table_b)
# 3. ON 키워드 이후 조인 조건을 작성
# 4. 조인 조건은 table_a와 table_b간의 레코드를 일치시키는 규칙을 지정
```

- 메인 테이블(FROM table)에 조인할 테이블(INNER JOIN table)의 데이터가 붙는 방식으로 테이블이 형성됨

- 예시코드

```sqlite
-- users, articles 2가지 table 생성
    CREATE TABLE users (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name VARCHAR(50) NOT NULL
    );

    CREATE TABLE articles(
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        title VARCHAR(10) NOT NULL,
        content VARCHAR(50) NOT NULL,
        userID INTEGER NOT NULL,
        FOREIGN KEY(userID) REFERENCES users
    );

    INSERT INTO users(name)
    VALUES
    ('하석주'),
    ('송윤미'),
    ('유하선');

    INSERT INTO articles(title,content,userID)
    VALUES
    ('게시글1','내용1',1),
    ('게시글2','내용2',2),
    ('게시글3','내용3',1),
    ('게시글4','내용4',4),
    ('게시글5','내용5',1);

-- INNER JOIN syntax
    -- userID가 1인 게시글의 제목과 작성자 가져오기
    SELECT articles.title,users.name 
    FROM articles
    INNER JOIN users
        ON articles.userID = users.id
    where
        userID = 1;



```

**(2) LEFT JOIN**

![2](https://github.com/JeongJonggil/TIL/assets/139416006/37b776e9-fc77-4870-8fbc-11addbbf68d7)


- 왼쪽은 테이블의 모든 레코드를 표기
- 오른쪽 테이블과 매칭되는 레코드가 없으면 NULL을 표시

```sqlite
#LEFT JOIN syntax
SELECT
	select_list
FROM
	table_a
LEFT JOIN table_b
	ON table_b.fk = table_a.pk;
	
# 1. FROM절 이후 왼쪽 테이블 지정(table_a)
# 2. LEFT JOIN 절 이후 오른쪽 테이블을 지정(table_b)
# 3. ON 키워드 이후 조인 조건을 작성
# 4. 왼쪽 테이블의 각 레코드를 오른쪽 테이블의 모든 레코드와 일치시킴
```

- 예시코드

```sqlite
-- LEFT JOIN syntax
    --게시글을 작성한 이력이 없는 회원 정보 조회
    SELECT *
    FROM users
    LEFT JOIN articles
        ON articles.userID = users.id
    where
        articles.userID IS NULL;
```

