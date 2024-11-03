# Spring 예외 처리(Exception) 내부 동작 과정 요약



### Spring 예외 처리 과정 흐름도 (최종 수정)

```tex
클라이언트 요청
     ↓
WAS (Web Application Server)
     ↓
필터 (Filter) 체인
     ↓
DispatcherServlet
     ↓
인터셉터 (Interceptor) 전처리
     ↓
컨트롤러 메서드 호출
     ↓
서비스 레이어에서 예외 발생
     ↓
Controller로 예외 전파
     ↓
DispatcherServlet으로 예외 전파
     ↓
DispatcherServlet에서 HandlerExceptionResolver 호출
     ↓
┌─────────────────────────────────────────────────────────────────────────────────────────────┐
│ [HandlerExceptionResolver 순차 처리]              										   │
│                                                											   │
│ 1. ExceptionHandlerExceptionResolver          											   │
│    - 예외가 발생한 컨트롤러의 @ExceptionHandler 탐색 											 │
│    - 예외가 발생한 컨트롤러에 @ExceptionHandler가 없으면 @ControllerAdvice 탐색                  │
└──────────────┬───────────────────────────────────────────────────────────────────────────────┘
             예               아니오
             ↓                 ↓
예외 처리 후 응답 반환     다음 Resolver로 전파
                             ↓
                     ┌───────────────────────────────────────────────┐
                     │ 2. ResponseStatusExceptionResolver	           │
                     │    - @ResponseStatus 또는                      │
                     │      ResponseStatusException 확인              │
                     └──────────────┬────────────────────────────────┘
                                   예                               아니오
                                   ↓                                  ↓
        `response.sendError(상태 코드)` 호출                    DefaultHandlerExceptionResolver 호출
      	 /error로 리다이렉트 및               		 					 ↓
         `BasicErrorController`에서 처리         			┌───────────────────────────────────────┐
                                                            │ 3. DefaultHandlerExceptionResolver    │
                                                            │    - Spring의 기본 예외 처리          │
                                                            │      (예: TypeMismatchException)      │
                                                            └──────────────┬────────────────────────┘
                                                                         예                    아니오
                                                                         ↓                      ↓
                `response.sendError(상태 코드)` 호출    		     		↓				WAS로 예외 전파
                /error로 리다이렉트 및						←--------------				   			↓
               `BasicErrorController`에서 처리                                                        
                                                                       ┌───────────────────────────────────┐
                                                                       │ WAS에서 /error 경로로 리다이렉트 │
                                                                       └──────────────┬────────────────────┘
                                                                                      ↓
                                                                       [다시 요청 처리 흐름 시작]  
                                                                                      ↓
                                                                           필터 → DispatcherServlet  
                                                                                      ↓
                                                                           /error 엔드포인트 매핑
                                                                                      ↓
                                                                           BasicErrorController 호출
                                                                                      ↓
                                                                      기본 에러 응답 생성 (JSON, HTML 등)
                                                                                      ↓
                                                                                   HTTP 응답 반환

```

---

### 각 단계의 설명

1. **클라이언트 요청 → WAS → 필터  -> DispatcherServlet → 인터셉터 → 컨트롤러 메서드 호출→  Service 로직 수행**
   - 클라이언트 요청이 컨트롤러에 매핑되고 Service 로직이 수행
3. **Service 계층 예외 발생 → 컨트롤러 → DispatcherServlet으로 예외 전파**  
   - 서비스 계층에서 예외가 발생하면 `DispatcherServlet`으로 예외 전파
4. **DispatcherServlet에서 HandlerExceptionResolver 순차 탐색 및 예외 처리**
   - `DispatcherServlet`은 `HandlerExceptionResolver`들을 호출하여 아래의 순서로 예외를 처리할 수 있는지 탐색
     1. **ExceptionHandlerExceptionResolver**
        - **해당 컨트롤러에 정의된 `@ExceptionHandler`를 우선적으로 탐색**하여 예외를 처리
        - **컨트롤러 내에 예외 처리기가 없으면, 전역 예외 처리기인 `@ControllerAdvice`에 정의된 `@ExceptionHandler`를 탐색**
     2. **ResponseStatusExceptionResolver**
        - `ExceptionHandlerExceptionResolver`에서도 예외 처리가 이루어지지 않은 경우, **`@ResponseStatus`가 붙은 예외나 `ResponseStatusException`을 감지하여 아래와 같이 처리 함**
          - 예외가 감지되면, **`response.sendError(상태 코드)` 메서드를 호출**하여 응답에 지정된 HTTP 상태 코드를 설정
          - `sendError()`가 호출되면 서블릿 컨테이너의 오류 처리 로직인 `/error`로 리다이렉트 및 `BasicErrorController`에 매핑되어 처리
          - **해당 과정의 `/error` 리다이렉트는`DispatcherServlet`에서 수행 (WAS까지 안감)**
     3. **DefaultHandlerExceptionResolver**
        - `ResponseStatusExceptionResolver`에서도 예외 처리가 되지 않으면, `DefaultHandlerExceptionResolver`가 Spring의 표준 예외를 처리
          - `HttpRequestMethodNotSupportedException` → 405 Method Not Allowed
          - `TypeMismatchException` → 400 Bad Request
          - 등등
        - `ExceptionHandlerExceptionResolver`와 마찬가지로, 이 과정에서도  `sendError()` 호출 및 서블릿 컨테이너의 오류 처리 로직인 `/error`로 리다이렉트 및 `BasicErrorController`에 매핑되어 처리
        - **해당 과정의 `/error` 리다이렉트도`DispatcherServlet`에서 수행 (WAS까지 안감)**
5. **WAS로 예외 전파 및 `/error` 경로 리다이렉트**  
   - `HandlerExceptionResolver`의 모든 예외 처리기를 통해서도 처리되지 않은 예외는 WAS로 전파되며, WAS는 `/error` 경로로 요청을 리다이렉트
6. **BasicErrorController에서 최종 에러 응답 생성 및 반환**  
   - `/error` 경로는 `BasicErrorController`에 매핑되어, 기본 에러 응답을 생성하고 최종적으로 클라이언트에게 반환
