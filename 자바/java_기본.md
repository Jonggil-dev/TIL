# Java Intro

### 1. 자바 가상 머신(JVM, Java virtual machine)

- 자바 바이트 코드를 실행할 수 있는 주체
- 자바 바이트 코드(자바 언어)는 플랫폼에 독립적이며 모든 JVM은 자바 가상 머신 규격에 정의된 대로 자바 바이트코드를 실행
- **자바(Java) 프로그래밍 언어에서 소스 코드를 컴파일하면 `.class` 파일이 생성. 이 `.class` 파일에는 Java Virtual Machine(JVM)이 이해할 수 있는 바이트코드(bytecode)가 들어 있음. 이 과정을 '컴파일'이라고 하며, 이때 소스 코드를 해석하고 실행 가능한 바이트코드로 변환해주는 프로그램을 '컴파일러(compiler)'라고 함.**

> ※ 참고
>
> ★ **JDK는 JRE, JVM을 포함하는 개념으로 JDK를 다운로드 받으면 JVM, JRE가 포함되어 있음**
>
> - JRE(Java Runtime Enviroment)
>   - 자바 실행환경 : JVM + 자바를 실행시키기 위해 필요한 것들
> - JDK(Java Development Kit)
>   - 자바 개발 도구 : JRE + 필요한 도구(문서,컴파일러 등) 

### 2. 변수와 자료형

- 변수

```java
//변수 선언 : 자료형 + 변수이름
int age;

//변수 할당 : 변수이름 = 값
age = 100;

// 변수 초기화 : 선언과 동시에 할당을 하면 초기화라고 부른다.
int money = 1000;
```

- 데이터 타입

  - 기본 자료형(Primitive Type)과 참조 자료형(Reference Type, 기본 자료형 8가지 외 모든 것)
  - 기본 자료형 : 미리 정해진 크기의 Memory Size 표현, 변수 자체에 값 저장
  - string(문자열)은 참조 자료형임

  | 타입   | 세부타입 | 데이터형 | 크기(byte) | 초기값 |
  | ------ | -------- | -------- | ---------- | ------ |
  | 논리형 |          | boolean  |            | false  |
  | 문자형 |          | char     | 2          | \u0000 |
  | 숫자형 | 정수형   | byte     | 1          |        |
  | 숫자형 | 정수형   | short    | 2          |        |
  | 숫자형 | 정수형   | int      | 4          | 0      |
  | 숫자형 | 정수형   | long     | 8          |        |
  | 숫자형 | 실수형   | float    | 4          | 0.0    |
  | 숫자형 | 실수형   | double   | 8          |        |

- 형 변환

  - 자동(묵시적,암묵적) 형변환이 가능한 방향

    - byte → short,char →int → long → float → double

  - 묵시적(암묵적) : Implicit Casting

    - 범위가 넓은 데이터 형에 좁은 데이터 형을 대입하는 것

  - 명시적 : Explicit Casting

    - 범위가 좁은 데이터 형에 넓은 데이터 형을 대입하는 것

    - 형 변환 연산자 사용 : (타입)값;

      ```java
      //ex)
      
      int i = 100;
      byte b = (byte)i;
      ```

- 제어문(조건문)

  - if문

    ```java
    if(조건식){
      실행할 문장;
    }else if(조건식){
      실행할 문장;
    }
    else{
      실행할 문장;
    }
    ```

  - switch문

    - 인자로 선택변수를 받아 변수의 값에 따라서 실행문이 결정

    ```java
    switch(수식){
    	case 값1:
    	  실행문;
    	  break;
    	case 값2:
    	  실행문;
    	  break;
    	default:
    	  실행문 C;
    }
    ```

    

- 반복문

  - for문

  ```java
  for(초기화식;조건식;증감식){
    실행문;
  }
  ```

  - while문

  ```java
  while(조건식){
    실행문;
  }
  ```

  - do while문
    - 블록 내용을 먼저 수행 후 조건식 판단 (최소 한 번은 수행)

  ```java
  do{
   실행문;
  }while(조건식);
  ```

  - break
    - 자바의 break는 파이썬과 다르게 반복문에 이름(라벨)을 붙여 break에 걸리는 순간 명시해놓은 for 문을 빠져 나올 수 있음
