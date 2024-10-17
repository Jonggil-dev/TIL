# Spring 예외 처리(Exception) 내부 동작 과정 요약

### 스프링 부트 예외 처리 과정 흐름도

```
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
컨트롤러로 예외 전파
     ↓
┌───────────────────────────────┐
│ [예외 처리 분기]              │
│                               │
│ 컨트롤러에 @ExceptionHandler? │
└──────────────┬────────────────┘
             예             아니오
             ↓               ↓
예외 처리 후 응답 반환    DispatcherServlet으로 예외 전파
                             ↓
              DispatcherServlet에서 HandlerExceptionResolver 호출
                             ↓
            ┌───────────────────────────────┐
            │ 전역 예외 처리기(@ControllerAdvice) 존재? │
            └──────────────┬────────────────┘
                         예             아니오
                         ↓               ↓
              전역 예외 처리 후 응답 반환     WAS로 예외 전파
                                             ↓
                               ┌───────────────────────────────┐
                               │ WAS에서 /error 경로로 리다이렉트 │
                               └──────────────┬────────────────┘
                                             ↓
                         [다시 요청 처리 흐름 시작]  
                               ↓
                      필터 → DispatcherServlet → 인터셉터
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

### **설명**

1. **클라이언트 요청 → WAS → 필터 체인**  
   - 클라이언트 요청이 WAS에 도달하고, 요청은 **필터 체인**을 통과합니다. 필터는 요청을 가로채어 인증, 로깅 등의 작업을 할 수 있습니다.

2. **DispatcherServlet → 인터셉터 전처리**  
   - 필터를 통과한 요청은 **DispatcherServlet**에 도달하고, **인터셉터**의 전처리(`preHandle`)가 실행됩니다.

3. **컨트롤러 호출 → 서비스 레이어에서 예외 발생**  
   - **컨트롤러 메서드**가 호출되고, 서비스 레이어에서 **예외가 발생**합니다. 예외가 발생하면 상위로 전파됩니다.

4. **컨트롤러로 예외 전파 → 예외 처리 분기**  
   - 예외가 **컨트롤러**로 전파됩니다.
   - 컨트롤러 내부에 **`@ExceptionHandler`**가 있으면 예외를 처리하고 응답을 반환합니다.
   - **`@ExceptionHandler`가 없으면**, 예외는 **DispatcherServlet**으로 전파됩니다.

5. **DispatcherServlet에서 `HandlerExceptionResolver` 호출**  
   - DispatcherServlet은 예외를 처리할 **`HandlerExceptionResolver`**를 호출하여, 예외 처리기를 탐색합니다.

6. **전역 예외 처리 분기 (`@ControllerAdvice`)**  
   - **[케이스 2-1]** 전역 예외 처리기(`@ControllerAdvice`)가 있으면 이를 통해 예외를 처리합니다.
   - **[케이스 2-2]** 전역 예외 처리기가 없으면 예외는 **WAS**로 전파됩니다.

7. **WAS에서 `/error` 경로로 리다이렉트**  
   - WAS는 예외가 처리되지 않았을 때 **`/error` 경로로 리다이렉트**하여 에러 처리를 시도합니다.

8. **리다이렉트 후 다시 요청 흐름 시작**  
   - **필터 → DispatcherServlet → 인터셉터**를 다시 거쳐 요청이 처리됩니다.  
   - `/error` 엔드포인트는 **BasicErrorController**로 매핑되어 호출됩니다.

9. **BasicErrorController에서 기본 에러 응답 생성**  
   - **BasicErrorController**는 기본 에러 응답(JSON, HTML 등)을 생성합니다.

10. **HTTP 응답 반환**  
   - 에러 응답이 최종적으로 **클라이언트에 반환**됩니다.
