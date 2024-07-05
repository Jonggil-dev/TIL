# Spring 예외 처리(Exception) 구현 방법

### 출처 

1. 스프링 예외 발생 시 내부 동작 과정: [MangKyu's Diary:티스토리](https://mangkyu.tistory.com/205)

---

### **1. ControllerAdvice와 RestControllerAdvice**

Spring은 전역적으로 예외를 처리할 수 있는 `@ControllerAdvice`와 `@RestControllerAdvice` 어노테이션을 각각 Spring3.2, Spring4.3부터 제공하고 있다. 두 개의 차이는 `@Controller`와 `@RestController`와 같은데, `@RestControllerAdvice`는 `@ControllerAdvice`와 달리 `@ResponseBody`가 붙어 있어 응답을 Json으로 내려준다는 점에서 다르다.

```java
@Target(ElementType.TYPE) 
@Retention(RetentionPolicy.RUNTIME) 
@Documented 
@ControllerAdvice 
@ResponseBody 
public @interface RestControllerAdvice {
    ... 
} 

@Target(ElementType.TYPE) 
@Retention(RetentionPolicy.RUNTIME) 
@Documented 
@Component 
public @interface ControllerAdvice {
    ... 
}
```

`ControllerAdvice`는 여러 컨트롤러에 대해 전역적으로 `ExceptionHandler`를 적용해준다. 위에서 보이듯 `ControllerAdvice` 어노테이션에는 `@Component` 어노테이션이 있어서 `ControllerAdvice`가 선언된 클래스는 스프링 빈으로 등록된다. 그러므로 우리는 다음과 같이 전역적으로 에러를 핸들링하는 클래스를 만들어 어노테이션을 붙여줌으로써 에러 처리를 위임할 수 있다.

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementFoundException.class)
    protected ResponseEntity<?> handleIllegalArgumentException(NoSuchElementFoundException e) {
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .code("Item Not Found")
                .message(e.getMessage()).build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
```

우리는 이러한 ControllerAdvice를 이용함으로써 다음과 같은 이점을 누릴 수 있다.

- 하나의 클래스로 모든 컨트롤러에 대해 전역적으로 예외 처리가 가능함
- 직접 정의한 에러 응답을 일관성있게 클라이언트에게 내려줄 수 있음
- 별도의 try-catch문이 없어 코드의 가독성이 높아짐

이러한 이유로 API에 의한 예외 처리를 할 때에는 `ControllerAdvice`를 이용하면 평가된다. 하지만 `ControllerAdvice`를 사용할 때에는 항상 다음의 내용들을 주의해야 한다. 여러 `ControllerAdvice`가 있을 때 `@Order` 어노테이션으로 순서를 지정하지 않는다면 Spring은 `ControllerAdvice`를 임의의 순서로 에러를 처리할 수 있다. 그러므로 일관된 예외 응답을 위해서는 이러한 점에 주의해야 한다.

- 한 프로젝트당 하나의 ControllerAdvice만 관리하는 것이 좋다.
- 만약 여러 ControllerAdvice가 필요하다면 basePackages나 annotations 등을 지정해야 한다.
- 직접 구현한 Exception 클래스들은 한 공간에서 관리한다.



### **2. @RestControllerAdvice를 이용한 Spring 예외 처리 방법**

#### [에러 코드 정의하기]

먼저 우리가 클라이언트에게 보내줄 에러 코드를 정의해야 한다. 기본적으로 에러 이름과 HTTP 상태 및 메세지를 가지고 있는 에러 코드 클래스를 만들어 보도록 하자. 에러 코드는 애플리케이션에서 전역적으로 사용되는 `CommonErrorCode`와 특정 도메인에 대해 구체적으로 내려가는 `UserErrorCode`로 나누고, 인터페이스를 이용해 추상화하도록 하자.

먼저 다음과 같이 `CommonErrorCode`와 `UserErrorCode`의 공통 메소드로 추상화할 인터페이스를 정의할 수 있다.

```java
public interface ErrorCode {

    String name();
    HttpStatus getHttpStatus();
    String getMessage();

}
```

그리고 발생할 수 있는 에러 코드를 다음과 같이 정의할 수 있다.

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

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    INACTIVE_USER(HttpStatus.FORBIDDEN, "User is inactive"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
```



### **[ 에러 코드 정의하기 ]**

그리고 우리가 발생한 예외를 처리해줄 예외 클래스(Exception Class)를 추가해주어야 한다. 우리는 언체크 예외(런타임 예외)를 상속받는 예외 클래스를 다음과 같이 추가해줄 수 있다.

```java
@Getter
@RequiredArgsConstructor
public class RestApiException extends RuntimeException {

    private final ErrorCode errorCode;

}
```

여기서 체크 예외가 아닌 언체크 예외를 상속받도록 한 이유가 있다. 왜냐하면 일반적인 비지니스 로직들은 따로 catch해서 처리할 것이 없므로 만약 체크 예외로 한다면 불필요하게 throws가 전파될 것이기 때문이다.

(만약 체크 예외, 언체크 예외에 대해서 잘 모르면 [여기](https://mangkyu.tistory.com/152)를 참고해주세요!)

또한 Spring은 내부적으로 발생한 예외를 확인하여 언체크 예외이거나 에러라면 자동으로 롤백시키도록 처리한다. Spring에서 체크 예외만 롤백을 안하는 이유는 체크 예외는 처리가 강제되기 때문에 개발자가 무언가를 처리할 것이라는 기대 때문이다.

(스프링의 트랜잭션 지원 관련해서는 [여기](https://mangkyu.tistory.com/170)를 참고해주세요!)

과거에는 체크 예외가 많이 사용되었지만, 최근에는 거의 모든 경우에 언체크 예외를 사용한다고 보면 된다.



### **[ 에러 응답 클래스 생성하기 ]**

우리는 클라이언트로 다음과 같은 포맷의 에러를 던져주도록 해야 한다고 하자.

```java
{
    "code": "INACTIVATE_USER",
    "message": "User is inactive"
}
```

이를 위해 다음과 같인 에러 응답 클래스를 추가해줄 수 있다.

```java
@Getter
@Builder
@RequiredArgsConstructor
public class ErrorResponse {

    private final String code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<ValidationError> errors;

    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class ValidationError {
    
        private final String field;
        private final String message;

        public static ValidationError of(final FieldError fieldError) {
            return ValidationError.builder()
                    .field(fieldError.getField())
                    .message(fieldError.getDefaultMessage())
                    .build();
        }
    }
}
```

추가적으로 위의 예외에서는 @Valid를 사용했을 때 에러가 발생한 경우 어느 필드에서 에러가 발생했는지 응답을 위한 ValidationError를 내부 정적 클래스로 추가해두었다. 또한 만약 errors가 없다면 응답으로 내려가지 않도록 @JsonInclude 어노테이션을 추가하였다.

### **[ @RestControllerAdvice 구현하기 ]**

이제 전역적으로 에러를 처리해주는 @RestControllerAdvice 클래스를 추가해주어야 한다. Spring은 스프링 예외를 미리 처리해둔 ResponseEntityExceptionHandler를 추상 클래스로 제공하고 있다. ResponseEntityExceptionHandler에는 스프링 예외에 대한 ExceptionHandler가 모두 구현되어 있으므로 ControllerAdvice 클래스가 이를 상속받게 하면 된다. 하지만 에러 메세지는 반환하지 않으므로 스프링 예외에 대한 에러 응답을 보내려면 아래 메소드를 오버라이딩 해야 한다.

```java
public abstract class ResponseEntityExceptionHandler {
    ...

    protected ResponseEntity<Object> handleExceptionInternal(
        Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request){
            
        ...
    }
}
```

이제 우리가 만든 RestApiException 예외와 @Valid에 의한 유효성 검증에 실패했을 때 발생하는 IllegalArgumentException 예외와 마지막으로 잘못된 파라미터를 넘겼을 경우 발생하는 IllegalArgumentException 에러를 처리해주도록 하자.

(아래의 코드는 예시 코드이므로, 상황에 맞게 최적화 및 커스터마이징 해주도록 하자.)

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RestApiException.class)
    public ResponseEntity<Object> handleCustomException(RestApiException e) {
        ErrorCode errorCode = e.getErrorCode();
        return handleExceptionInternal(errorCode);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("handleIllegalArgument", e);
        ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
        return handleExceptionInternal(errorCode, e.getMessage());
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        log.warn("handleIllegalArgument", e);
        ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
        return handleExceptionInternal(e, errorCode);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAllException(Exception ex) {
        log.warn("handleAllException", ex);
        ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
        return handleExceptionInternal(errorCode);
    }

    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(makeErrorResponse(errorCode));
    }

    private ErrorResponse makeErrorResponse(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .build();
    }

    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode, String message) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(makeErrorResponse(errorCode, message));
    }

    private ErrorResponse makeErrorResponse(ErrorCode errorCode, String message) {
        return ErrorResponse.builder()
                .code(errorCode.name())
                .message(message)
                .build();
    }

    private ResponseEntity<Object> handleExceptionInternal(BindException e, ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(makeErrorResponse(e, errorCode));
    }

    private ErrorResponse makeErrorResponse(BindException e, ErrorCode errorCode) {
        List<ErrorResponse.ValidationError> validationErrorList = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(ErrorResponse.ValidationError::of)
                .collect(Collectors.toList());

        return ErrorResponse.builder()
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .errors(validationErrorList)
                .build();
    }
}
```

RestApiException 예외와 IllegalArgumentException의 경우에는 이를 캐치해서 핸들링하는 @ExceptionHandler를 구현해주면 되었다. 하지만 @Valid에 의한 MethodArgumentNotValidException의 경우에는 에러 필드와 메세지를 추가해주어야 하는데, 관련 정보는 MethodArgumentNotValidException의 getBindingResult를 통해서 얻을 수 있다.

### **[ 에러 응답 확인 ]**

이제 실제로 우리가 원하는 대로 에러 응답이 내려오는지 확인할 차례이다. 이를 위해 다음과 같은 컨트롤러를 구현해보도록 하자.

```java
@RestController
@RequiredArgsConstructor
public class UserController {

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser() {
        throw new RestApiException(UserErrorCode.INACTIVE_USER);
    }
}
```

위의 설명과 조금 다르지만 실제 예시 코드는 다음의 [깃허브](https://github.com/MangKyu/InterviewSubscription/tree/master/src/main/java/com/mangkyu/employment/interview/erros)에서 확인하실 수 있습니다.