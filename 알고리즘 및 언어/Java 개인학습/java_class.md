# 객체지향 프로그래밍

### 1. 개요

- 객체지향 프로그래밍(OOP, Object Oriented Programming)

  - 객체 : 사물과 같이 유형적인 것과 개념이나 논리와 같은 무형적인 것들

  - 지향 : 작정하거나 지정한 방향으로 나아감

  - 객체 모델링 : 현실세계의 객체를 SW 객체로 설계하는 것

- 클래스(Class)
  - 객체를 만드는 설계도(Blueprint)
  - **상태와 행동을 묶어서 만든 사용자정의 자료형**

- 인스턴스(Instance)
  - 클래스를 통해 생성된 객체

### 2. 특징

- A PIE
  - Abstraction(추상화)
  - Polymorphism(다형성)
  - Inheritance(상속)
  - Encapsulation(캡슐화)
- 장점
  - 모듈화된 프로그래밍
  - 재사용성이 높다

###  3. 코드

```java
public class Person{
	String name;
	int age;
	String hobby;
	
	public void info() {
		System.out.println("나의 이름은" + name +"입니다")
		System.out.println("나이는"+age", 취미는"+ hobby +"입니다");

	}
}
```

### 4. 클래스 구성

- 속성(Attribute) - 필드
- 동작(Behavior) - 메소드
- 생성자(Constructor) - 특수한 함수
- 중첩 클래스(클래스 내부의 클래스)

### 5. 클래스 선언

- [접근제한자(public/default)] [활용제한자(final/abstract)] class 클래스명 { 

  ​	속성 정의 (필드)

  ​	기능 정의 (메소드)

  ​	생성자

  }

```java
public class Person {
	String name;
	int age;
	
	publick void eat(){
	}
	
	public Person(){
	}
}
```

### 6. 변수

- 클래스 변수
  - 클래스 영역 선언 (static 키워드)
  - 생성시기: 클래스가 메모리에 올라 갔을 때
  - 모든 인스턴스가 공유함
- 인스턴스 변수
  - 클래스 영역 선언
  - 생성시기 : 인스턴스가 생성되었을 때 (new)
  - 인스턴스 별로 생성됨
- 지역 변수
  - 클래스 영역 이외 (메서드,생성자 등)
  - 생성시기 : 선언되었을 떄

### 7. 메소드

- 리턴 타입은 메소드를 선언할 때 지정, 없다면 void (return 문 생략 가능)

- 리턴 타입을 작성했다면 반드시 해당 타입의 값을 리턴

- 리턴 타입은 하나만 적용 가능

- 메소드 오버로딩

  - 이름이 같고 매개변수가 다른 메서드를 여러 개 정의하는 것
  - 중복 코드에 대한 효율적 관리 가능
  - 파라미터의 개수 또는 순서, 타입이 달라야 할 것(파라미터 이름만 다른 것은 X)
  - 리턴 타입이 다른 것은 의미 X

  ```java
  ex)
  println(): void;
  println(boolean x): void;
  println(char x): void;
  println(char[] x): void;
  println(double x): void;
  println(float x): void;
  prinln(int x);
  ```


### 8.생성자

- new 키워드와 함께 호출하여 객체 생성
- **클래스명과 동일** `[접근제한자] [활용제한자] 클래스명 (매개변수){ }
- **기본생성자는 습관처럼 추가하자.**
- 결과형 리턴값을 갖지 않음 (void 작성 x)

- 객체가 생성될 때 반드시 하나의 생성자 호출
- 멤버필드의 초기화에 주로 사용

- 하나의 클래스 내부에 생성자가 하나도 없으면 자동적으로 default 생성자가 있는 것으로 인지
  - default 생성자 : 매개 변수도 없고 내용도 없는 생성자
- 매개변수의 개수가 다르거나, 자료형이 다른 여러 개의 생성자가 있을 수 있음(생성자 오버로딩)
- 생성자의 첫번째 라인으로 this() 생성자를 사용하여 또 다른 생성자를 하나 호출 가능

```java
//예시
public class Dog{
    public Dog() {
        System.out.println("기본생성자")
    }
}
```

- 파라미터가 있는 생성자
  - 생성자의 목적이 필드 초기화
  - 생성자 호출 시 값을 넘겨주어야 함

```java
//예시
public class Dog{
    String name;
    int age;
    
    //기본생성자는 습관처럼 추가하자.
    public Dog() {
        System.out.println("기본생성자")
    }
    
    public Dog(String name, int a){
        this.name =name;
        age = a
    }
}
```

- this

  - 참조 변수로써 객체 자신을 가리킴
  - this를 이용하여 자신의 멤버 접근 가능
  - 지역변수(매개변수)와 필드의 이름이 동일한 경우 필드임을 식별할 수 있게 함
  - this([인자값]) : 생성자 호출 할 때 사용함
    - 생성자 내에서만 호출이 가능함
    - 생성자 내에서 첫번째 구문에 위치해야 함

  ```java
  class Dog {
  	String name;
  	int age;
  	Dog() {
  		this("쫑");
  	}
  	Dog( String name ) {
  	}
  }
  
  // 여기서 
  //Dog1 = new Dog();
  //Dog.name을 호출하면 "쫑" 이 출력됨
  //인스턴스 생성시 매개변수가 없으니 기본 생성자 실행 →  this("쫑") 실행 → 생성자 내에서 첫번째 구문에 위치함으로
  //생성자 중에서 `생성자(String 매개변수)` 타입의 생성자 호출 됨 ( Dog( String name) 실행 → Dog("쫑"))
  ```
