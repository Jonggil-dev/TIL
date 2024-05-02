# Spring Enum Class

### 출처

- Enum 사용법 : https://limkydev.tistory.com/66
- Enum 탄생 과정 : [Inpa Dev](https://inpa.tistory.com/entry/JAVA-%E2%98%95-%EC%97%B4%EA%B1%B0%ED%98%95Enum-%ED%83%80%EC%9E%85-%EB%AC%B8%EB%B2%95-%ED%99%9C%EC%9A%A9-%EC%A0%95%EB%A6%AC)

---

### 1. Enum Class

- **클래스처럼 보이게 하는 상수**

- 서로 관련있는 상수들끼리 모아 상수들을 대표할 수 있는 이름으로 타입을 정의하는 것

- Enum 클래스 형을 기반으로 한 클래스형 선언 

### 2. 특징

- 열거형으로 선언된 순서에 따라 0 부터 인덱스 값을 가진다. 순차적으로 증가된다.
- enum 열거형으로 지정된 상수들은 모두 대문자로 선언 (관습임, 대문자로 안해도 에러는 안남)
- 마지막에 열거형 변수들을 선언한 후 세미콜론(;)은 찍지 않는다.
  (상수와 연관된 문자를 연결시킬 경우 세미콜론(;) 찍는다. 아래 사용예시 부분이 해당)

### 3. 사용 예시

```java
@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {

    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "Invalid parameter included"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not exists"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
```

- 위 코드에서 `INVALID_PARAMETER`, `RESOURCE_NOT_FOUND`, `INTERNAL_SERVER_ERROR`는 상수 객체로 결국 `CommonErrorCode`의 인스턴스임
- 그래서  `INVALID_PARAMETER`는 `CommonErrorCode` 클래스의 인스턴스 이므로 , `private final HttpStatus httpStatusk, private final String message` 두 가지 필드를 가진 객체임
- 스프링 내부적으로 `INVALID_PARAMETER`를 생성할 때 `HttpStatus.BAD_REQUEST`, `"Invalid parameter included"`를 생성자 매개변수로 받아서 `INVALID_PARAMETER`의 `httpStatus`, `message`필드에 초기화 됨
- 사용은`CommonErrorCode.INVALID_PARAMETER.getHttpStatus()`와 같은 방법으로 상수 객체에 초기화된 필드 호출 가능