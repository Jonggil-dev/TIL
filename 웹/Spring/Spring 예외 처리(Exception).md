# Spring 예외 처리(Exception) 내부 동작 과정 

### 출처 

1. 스프링 예외 발생 시 내부 동작 과정: [MangKyu's Diary:티스토리](https://mangkyu.tistory.com/204)

---

### **1. 스프링의 기본적인 예외 처리 방법**

- Spring은 만들어질 때(1.0)부터 **에러 처리를 위한 `BasicErrorController`**를 구현해두었고, 스프링 부트는 예외가 발생하면 기본적으로 **/error로 에러 요청을 다시 전달하도록 WAS 설정**을 해두었다. 그래서 별도의 설정이 없다면 예외 발생 시에 `BasicErrorController`로 에러 처리 요청이 전달된다. 참고로 이는 스프링 부트의 `WebMvcAutoConfiguration`를 통해 자동 설정이 되는 WAS의 설정이다.

  여기서 요청이 /error로 다시 전달된다는 부분에 주목해야 한다. 일반적인 요청 흐름은 다음과 같이 진행된다.

  ```java
  WAS(톰캣) -> 필터 -> 서블릿(디스패처 서블릿) -> 인터셉터 -> 컨트롤러
  ```

  그리고 컨트롤러 하위에서 예외가 발생하였을 때, 별도의 예외 처리를 하지 않으면 WAS까지 에러가 전달된다. 그러면 WAS는 애플리케이션에서 처리를 못하는 예와라 exception이 올라왔다고 판단을 하고, 대응 작업을 진행한다.

  ```java
  컨트롤러(예외발생) -> 인터셉터 -> 서블릿(디스패처 서블릿) -> 필터 -> WAS(톰캣)
  ```

  WAS는 스프링 부트가 등록한 에러 설정(/error)에 맞게 요청을 전달하는데, 이러한 흐름을 총 정리하면 다음과 같다.

  ```java
  WAS(톰캣) -> 필터 -> 서블릿(디스패처 서블릿) -> 인터셉터 -> 컨트롤러
  -> 컨트롤러(예외발생) -> 인터셉터 -> 서블릿(디스패처 서블릿) -> 필터 -> WAS(톰캣)
  -> WAS(톰캣) -> 필터 -> 서블릿(디스패처 서블릿) -> 인터셉터 -> 컨트롤러(BasicErrorController)
  ```

  기본적인 에러 처리 방식은 **결국 에러 컨트롤러를 한번 더 호출**하는 것이다. 그러므로 필터나 인터셉터가 다시 호출될 수 있는데, 이를 제어하기 위해서는 별도의 설정이 필요하다. 서블릿은 dispatcherType로 요청의 종류를 구분하는데, 일반적인 요청은 REQUEST이며 에러 처리 요청은 ERROR이다. 필터는 서블릿 기술이므로 필터 등록(`FilterRegistrationBean`) 시에 호출될 dispatcherType 타입을 설정할 수 있고, 별도의 설정이 없다면 REQUEST일 경우에만 필터가 호출된다. 하지만 인터셉터는 스프링 기술이므로 dispatcherType을 설정할 수 없어 URI 패턴으로 처리가 필요하다.

  스프링 부트에서는 WAS까지 직접 제어하게 되면서 이러한 WAS의 에러 설정까지 가능해졌다. 또한 이는 요청이 2번 생기는 것은 아니고, 1번의 요청이 2번 전달되는 것이다. 그러므로 클라이언트는 이러한 에러 처리 작업이 진행되었는지 알 수 없다.

### 2. 스프링이 제공하는 다양한 예외처리 방법

- Java에서는 예외 처리를 위해 try-catch를 사용해야 하지만 try-catch를 모든 코드에 붙이는 것은 비효율적이다. Spring은 에러 처리라는 공통 관심사(cross-cutting concerns)를 메인 로직으로부터 분리하는 다양한 예외 처리 방식을 고안하였고, 예외 처리 전략을 추상화한 `HandlerExceptionResolver`인터페이스를 만들었다. (전략 패턴이 사용된 것이다.)
  대부분의 `HandlerExceptionResolver`는 발생한 Exception을 catch하고 HTTP 상태나 응답 메세지 등을 설정한다. 그래서 WAS 입장에서는 해당 요청이 정상적인 응답인 것으로 인식되며, 위에서 설명한 복잡한 WAS의 에러 전달이 진행되지 않는다.

```java
public interface HandlerExceptionResolver {
    ModelAndView resolveException(HttpServletRequest request, 
            HttpServletResponse response, Object handler, Exception ex);
}
```

- 위의 Object 타입인 handler는 예외가 발생한 컨트롤러 객체이다. 예외가 던져지면 디스패처 서블릿까지 전달되는데, 적합한 예외 처리를 위해 `HandlerExceptionResolver` 구현체들을 빈으로 등록해서 관리한다. 그리고 적용 가능한 구현체를 찾아 예외 처리를 하는데, 우선순위대로 아래의 4가지 구현체들이 빈으로 등록되어 있다.
  - `DefaultErrorAttributes`: 에러 속성을 저장하며 직접 예외를 처리하지는 않는다.
  - `ExceptionHandlerExceptionResolver`: 에러 응답을 위한 `Controller`나 `ControllerAdvice`에 있는 `ExceptionHandler`를 처리함
  - `ResponseStatusExceptionResolver`: `Http`상태 코드를 지정하는 `@ResponseStatus`또는 `ResponseStatusException`를 처리함
  - `DefaultHandlerExceptionResolver`:  스프링 내부의 기본 예외들을 처리한다.

- `DefaultErrorAttributes`는 직접 예외를 처리하지 않고 속성만 관리하므로 성격이 다르다. 그래서 내부적으로 `DefaultErrorAttributes`를 제외하고 직접 예외를 처리하는 3가지 `ExceptionResolver`들을 `HandlerExceptionResolverComposite`로 모아서 관리한다. 즉, 컴포지트 패턴을 적용해 실제 예외 처리기들을 따로 관리하는 것이다. 

- Spring은 아래와 같은 도구들로 `ExceptionResolver`를 동작시켜 에러를 처리할 수 있는데, 각각의 방식 대해 자세히 살펴보도록 하자

  **( 각 방식 별 자세한 설명은 출처 1번 참고)**

  - `ResponseStatus`

  - `ResponseStatusException`

  - **`ExceptionHandler`**

  - **`ControllerAdvice, RestControllerAdvice`**

    

### 3. Spring의 예외 처리 흐름 

- 앞서 설명하였듯 다음과 같은 예외 처리기들은 스프링의 빈으로 등록되어 있고, 예외가 발생하면 순차적으로 다음의 Resolver들이 처리 가능한지 판별한 후에 예외가 처리된다.
  - `ExceptionHandlerExceptionResolver`: 에러 응답을 위한 `Controller`나 `ControllerAdvice`에 있는 `ExceptionHandler`를 처리함
  - `ResponseStatusExceptionResolver`: Http 상태 코드를 지정하는 `@ResponseStatus`또는 `ResponseStatusException`를 처리함
  - `DefaultHandlerExceptionResolver`:  스프링 내부의 기본 예외들을 처리한다.

1. `ExceptionHandlerExceptionResolver`가 동작함
   1. 예외가 발생한 컨트롤러 안에 적합한 `@ExceptionHandler`가 있는지 검사함
   2. 컨트롤러의 `@ExceptionHandler`에서 처리가능하다면 처리하고, 그렇지 않으면 `ControllerAdvice`로 넘어감
   3. `ControllerAdvice`안에 적합한 `@ExceptionHandler`가 있는지 검사하고 없으면 다음 처리기로 넘어감
2. `ResponseStatusExceptionResolver`가 동작함
   1. `@ResponseStatus`가 있는지 또는 `ResponseStatusException`인지 검사함
   2. 맞으면 `ServletResponse`의 sendError()로 예외를 서블릿까지 전달되고, 서블릿이 `BasicErrorController`로 요청을 전달함
3. `DefaultHandlerExceptionResolver`가 동작함
   1. Spring의 내부 예외인지 검사하여 맞으면 에러를 처리하고 아니면 넘어감
4. 적합한 `ExceptionResolver`가 없으므로 예외가 서블릿까지 전달되고, 서블릿은 `SpringBoot`가 진행한 자동 설정에 맞게 `BasicErrorController`로 요청을 다시 전달함


앞서 살펴보았듯 Spring은 `BasicErrorController`를 구현해두었다. `ExceptionHandler`나 `ControllerAdvice`처럼 직접 에러를 반환하는 경우에는 `BasicErrorController`를 거치지 않지만 `@ResponseStatus`, `ResponseStatusException`등과 같이 직접 에러 응답을 반환하지 않는 경우에는 최종적으로 `BasicErrorController`를 거쳐 에러가 처리된다. 클라이언트 입장에서는 이를 모르지만 내부에서는 2번 컨트롤러로 요청이 전달되는 과정이 진행된다.

Spring은 매우 다양한 예외 처리 방법을 제공하고 있어 어떻게 에러를 처리하는 것이 최선(Best Practice)인지 파악이 어려울 수 있다. 위의 포스팅을 통해서 이제 `ControllerAdvice`를 이용하는 것이 가장 좋은 방식임을 이해할 수 있었는데, 다음 포스팅에서는 어떻게 `ControllerAdvice`를 사용할 수 있는지 코드를 통해 살펴보도록 하자.