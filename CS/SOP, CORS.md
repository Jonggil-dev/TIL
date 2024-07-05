# SOP, CORS

### 1. SOP (Same-origin policy, 동일 출처 정책)

- 웹 보안의 기본 원칙으로, 웹 페이지가 다른 출처의 리소스에 대한 액세스를 제한합니다. 
- 여기서 '출처'는 프로토콜, 도메인, 포트의 조합을 의미
- **SOP는 브라우저의 script와 <-> 서버 통신 간에만 적용되는 정책임**
  - **브라우저 내 실행**: SOP는 웹 브라우저 내에서 실행되는 JavaScript와 같은 클라이언트 측 스크립트에 적용됩니다. 이는 웹 페이지가 다른 출처(다른 도메인, 프로토콜 또는 포트)의 리소스에 접근하는 것을 제한합니다.
  - **서버 간 통신**: 서버 간 통신에서는 SOP가 적용되지 않습니다. 서버 간에는 웹 브라우저와 같은 제한 요소가 없기 때문에, 다른 출처의 서버로 자유롭게 요청을 보내고 응답을 받을 수 있습니다.

### 2. CORS (Cross-Origin Resource Sharing, 교차 출처 리소스 공유)

- 웹 애플리케이션에서 발생하는 출처 간 요청 (cross-origin requests)을 안전하게 처리할 수 있게 하는 메커니즘
- **웹 페이지의 스크립트(ex. axios)가 다른 출처의 리소스에 접근하는 경우에만 Cross-Origin 요청임.** 
- **브라우저 주소창을 통해 URL을 직접 요청하는 행위는 스크립트가 요청하는게 아님. 사용자가 직접 해당 리소스나 페이지로 이동하는 것이기 때문에 크로스-오리진 요청으로 간주되지 않으며, SOP의 제한을 받지 않음**

### 3. (예시) 동일출처 요청 vs Cross-Origin 요청

#### 예시: 웹 페이지와 AJAX 요청

웹 페이지 `https://example.com/page.html`이 있다고 가정해봅시다. 이 페이지에는 JavaScript 코드가 포함되어 있으며, 이 코드는 서버로부터 추가 데이터를 가져오기 위해 AJAX (Asynchronous JavaScript and XML) 요청을 수행합니다.

#### 1. 동일 출처 요청

- **URL**: `https://example.com/data/api`
- **상황**: `page.html`은 JavaScript를 사용하여 `https://example.com`에 있는 `data/api` 엔드포인트로 AJAX 요청을 보냅니다.
- **SOP 적용 여부**: 이 경우 요청은 동일 출처입니다. 프로토콜(`https`), 도메인(`example.com`), 포트(명시되지 않았으나 기본적으로 443)가 모두 동일합니다. 따라서 SOP에 의한 제한이 없으며, 요청은 성공적으로 수행됩니다.

#### 2. 크로스-오리진 요청

- **URL**: `https://another-site.com/data/api`
- **상황**: 동일한 `page.html`에서 `https://another-site.com`에 있는 `data/api` 엔드포인트로 AJAX 요청을 보냅니다.
- **SOP 적용 여부**: 여기서는 요청이 크로스-오리진입니다. `example.com` 페이지의 JavaScript가 `another-site.com`의 리소스에 접근하려고 하기 때문입니다. SOP에 따라, 기본적으로 이러한 요청은 허용되지 않습니다. 요청이 성공하려면 대상 서버인 `another-site.com`이 CORS 헤더를 통해 이러한 요청을 명시적으로 허용해야 합니다.

#### 3. 독립적인 요청 

- 주소창에 직접 `https://another-site.com/data/api`를 입력하여 요청하는 경우, 이는 크로스-오리진 요청이 아님
- **직접 URL 접속**: `https://example.com/page.html`에서 다른 탭이나 창에서 `https://another-site.com/data/api`로 이동하는 것은 각각 독립적인 요청으로 간주됩니다. 이때 각 요청은 자체적인 출처를 가지며, 이전 페이지와의 관계는 없습니다.