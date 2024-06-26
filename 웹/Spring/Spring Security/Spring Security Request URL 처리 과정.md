### Spring Security Request URL 처리 과정

- Spring Security를 사용할 때, **들어오는 요청에 대해 가장 먼저 판단하는 것은 해당 요청이 인증을 필요로 하는지, 그리고 명시적으로 인증 없이 접근이 허용되는 요청인지 여부**입니다. 
- 이 과정은 Spring Security의 필터 체인을 통해 이루어지며, 여기서는 다음과 같은 단계로 요청을 처리합니다.
  1. **요청 경로 분석:** 요청이 들어오면, **Spring Security는 먼저 요청된 URL(경로)를 분석하여 이 경로가 보안 설정에서 어떻게 정의되어 있는지 확인합니다**. 예를 들어, 특정 경로들은 인증 없이 접근이 가능하도록 설정될 수 있으며(`permitAll()`), 다른 경로들은 인증된 사용자만 접근할 수 있도록 설정될 수 있습니다(`authenticated()`).

  2. **인증 여부 확인:** 요청된 경로가 인증을 필요로 하는 경우, Spring Security는 요청과 함께 제공된 인증 정보(예: JWT 토큰)를 검증합니다. 인증 정보가 없거나 유효하지 않으면, 요청은 거부됩니다.

  3. **권한 확인:** 요청이 인증 과정을 통과했다면, 다음 단계로 사용자가 해당 요청을 수행할 권한이 있는지 확인합니다. 이는 사용자의 역할이나 권한에 따라 다를 수 있으며, 특정 경로나 작업에 대한 접근을 제한할 수 있습니다.

  4. **요청 처리:** 모든 검사를 통과한 요청은 애플리케이션으로 전달되어 처리됩니다. 반면, 인증되지 않았거나 권한이 부족한 요청은 적절한 HTTP 상태 코드(예: 401 Unauthorized, 403 Forbidden)와 함께 거부됩니다.

  5. **Controller 매핑**: 인증/권한이 허가된 요청의 경우 해당하는 Url과 매핑된 Controller로 요청이 전달됩니다. 만약 매핑되는 Controller 가 없는 경우 404 NotFound Error와 함께 요청이 거부됩니다.

