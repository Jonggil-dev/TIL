# Spring Security 현재 인증된 객체 가져오는 법

### 1. `@AuthenticationPrincipal`

- Spring Security에서 현재 인증된 사용자의 세부 정보를 컨트롤러 메소드의 파라미터로 직접 주입받기 위해 사용되는 어노테이션 
- 이 어노테이션을 사용하면, `SecurityContextHolder`를 통해 명시적으로 인증 정보를 조회할 필요 없이, 인증된 사용자의 정보를 쉽게 가져올 수 있음

### 2. 사용 방법

컨트롤러의 메소드에 `@AuthenticationPrincipal` 어노테이션을 사용하여 현재 인증된 사용자의 정보를 파라미터로 받을 수 있습니다. 예를 들어, Spring Security가 인증 과정에서 `UserDetails` 구현체를 사용하여 사용자 정보를 관리하는 경우, 아래와 같이 컨트롤러에서 이 정보를 바로 사용할 수 있습니다:

```java
@GetMapping("/user/info")
public String userInfo(@AuthenticationPrincipal UserDetails currentUser) {
    String username = currentUser.getUsername();
    // 사용자의 정보를 이용한 로직 처리
    return "user/info";
}
```

### 3. 주의사항

- `@AuthenticationPrincipal`은 주로 인증된 사용자의 정보를 표현하는 `UserDetails` 객체 또는 그것을 확장한 사용자 정의 객체에 접근할 때 사용됩니다.
- Spring Security 5에서는 `@AuthenticationPrincipal`이 있는 메소드 파라미터에 직접 커스텀 사용자 객체를 주입받기 위해 `UserDetails`를 상속받은 객체 대신, `@AuthenticationPrincipal`에 `expression` 속성을 사용하여 특정 필드에 접근하는 것도 가능합니다.

### 4. 커스텀 사용자 정보의 주입

사용자 정보를 담는 커스텀 객체를 사용하는 경우, `@AuthenticationPrincipal`을 이용하여 해당 객체의 인스턴스를 직접 받을 수 있습니다. 예를 들어, `CustomUserDetails` 객체에서 사용자 ID를 가져오고 싶다면 다음과 같이 할 수 있습니다:

```java
public String someMethod(@AuthenticationPrincipal(expression = "id") Long userId) {
    // 사용자 ID를 사용한 로직 처리
}
```
