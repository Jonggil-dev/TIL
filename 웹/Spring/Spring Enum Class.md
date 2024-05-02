# Spring Enum Class

### 출처

- Enum 사용법 : https://limkydev.tistory.com/66
- Enum 탄생 과정 : [Inpa Dev](https://inpa.tistory.com/entry/JAVA-%E2%98%95-%EC%97%B4%EA%B1%B0%ED%98%95Enum-%ED%83%80%EC%9E%85-%EB%AC%B8%EB%B2%95-%ED%99%9C%EC%9A%A9-%EC%A0%95%EB%A6%AC)

---

### 1. Enum Class

- **클래스처럼 보이게 하는 상수**

- 서로 관련있는 상수들끼리 모아 상수들을 대표할 수 있는 이름으로 타입을 정의하는 것

- Enum 클래스 형을 기반으로 한 클래스형 선언 

### 2. 선언 방법

**1) 별도의 .java 선언**

- DevType.java

```
package EnumExample;

public enum DevType {

	MOBILE, WEB, SERVER
}
```

- Developer.java

```
package EnumExample;

public class Developer {
	
	public String name;
	public int career;
	public DevType type;
	
}
```

**2) Class 내부에서 선언**

- Developer.java

```
package EnumExample;
package EnumExample;

public class Developer {
	
	public String name;
	public int career;
	public enum DevType {

		MOBILE, WEB, SERVER
	}

}
```

**3) Class 외부에서 선언**

Develoer.java

```
package EnumExample;

public class Developer {
	
	public String name;
	public int career;
	public DevType type;

}

enum DevType {

	MOBILE, WEB, SERVER
}
```

### 3. 특징

- 열거형으로 선언된 순서에 따라 0 부터 인덱스 값을 가진다. 순차적으로 증가된다.
- enum 열거형으로 지정된 상수들은 모두 대문자로 선언
- 마지막에 열거형 변수들을 선언한 후 세미콜론(;)은 찍지 않는다.
  (상수와 연관된 문자를 연결시킬 경우 세미콜론(;) 찍는다. 맨아래 예제 나와있음.)