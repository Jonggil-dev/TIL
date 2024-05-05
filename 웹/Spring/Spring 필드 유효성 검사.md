# Spring 필드 유효성 검사

### 1. Bean Validation의 기본과 `@Valid`의 역할
- **Bean Validation**: Java의 표준 유효성 검사 방법으로, 클래스 필드에 `@NotNull`, `@NotEmpty`, `@Size` 등의 어노테이션을 사용하여 유효성 검사 규칙을 정의합니다.
- **`@Valid`**: DTO 객체가 컨트롤러 메서드로 전달될 때 이 어노테이션을 사용하여 객체의 유효성 검사를 활성화합니다. `@Valid`**가 없으면 설정된 유효성 검사 규칙이 실행되지 않습니다.**
- **DTO 말고 `@RequestParam`, `@PathVariable` 에 사용하는 `@NotNull`과 같은 단일 Bean Validation 어노테이션들은 `@Valid`나 `@Validated` 어노테이션 없이도 자동으로 유효성 검사가 적용됨**

  ```java
  @DeleteMapping("/child")
  public void deleteChild(@RequestParam @NotNull Integer childId) {
  }
  ```

  

### 2. `@Pattern` 사용과 에러 메시지 처리
- **`@Pattern`**: 정규식을 사용하여 문자열 필드가 특정 패턴을 따르는지 검사합니다. 예: `@Pattern(regexp = "^[a-zA-Z0-9]{6,12}$", message = "Username must be 6 to 12 alphanumeric characters")`는 사용자 이름이 6~12자의 영문자 또는 숫자여야 함을 검증합니다. 또한, 예외 발생 시 `message`에 작성된 부분이 예외 응답 객체에 담깁니다.



### 3. 예시

- Dto

  ```java
  public class UserDto {
  
      @NotNull(message = "Username cannot be null")
      @Pattern(regexp = "^[a-zA-Z0-9]{6,12}$", message = "Username은 must be 6 to 12 alphanumeric characters")
      private String username;
  
      @NotNull(message = "Email cannot be null")
      @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "Invalid email format")
      private String email;
  }
  
  ```

- Controller

  ```java
  @RestController
  public class UserController {
  
      @PostMapping("/users")
      public ResponseEntity<String> createUser(@Valid @RequestBody UserDto userDto) {
          // 여기서 비즈니스 로직을 수행 (예: 사용자 저장)
          return ResponseEntity.ok("User registered successfully!");
      }
  }
  
  ```

  

### 4. Bean Validation의 예외 처리와 `@ExceptionHandler`의 사용

- **예외 처리**: 유효성 검사 실패 시 `MethodArgumentNotValidException`이 발생하며, 이는 스프링의 기본 예외 처리기에 의해 처리됩니다.
- **`@ExceptionHandler`**: `@ControllerAdvice` 클래스 내에서 `MethodArgumentNotValidException`을 처리하고 사용자 정의 응답을 반환할 수 있습니다. 
- **유효성 검사 실패 시 `@Pattern`에서 정의한 `message`는 `FieldError.getDefaultMessage()`를 통해 얻을 수 있습니다.**
- 예시
  
  ```java
  @ControllerAdvice
  public class GlobalExceptionHandler {
      @ExceptionHandler(MethodArgumentNotValidException.class)
      public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
          Map<String, String> errors = new HashMap<>();
          ex.getBindingResult().getAllErrors().forEach(error -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
          });
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
      }
  }
  ```
