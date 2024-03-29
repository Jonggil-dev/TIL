# SSL, TLS 프로토콜

### 1. 개념

- 인터넷상에서 데이터를 안전하게 주고받기 위해 사용되는 프로토콜
- TLS(Transport Layer Security는 SSL(Secure Sockets Layer)의 후속 버전으로 더 강화된 보안 기능을 제공
- SSL, TLS 프로토콜을 사용하는 웹 서버는 신뢰할 수 있는 인증 기관(CA)에 의해 발급 받은 SSL/TLS 인증서를 가지고 있어야 함

### 2. 동작 방식

1. **핸드셰이크(Handshake)** : 클라이언트와 서버간 서로의 신원을 확인하고, 통신에 사용될 세션 키를 교환 하는 과정

   (1) **인증서의 제공**: 클라이언트가 서버에 연결을 시도할 때, 서버는 클라이언트에게 자신의 SSL/TLS 인증서를 제공 (인증서 안에는 서버의 공개키가 포함)

   (2) **세션키 생성 및 암호화**: 클라이언트는 임의의 세션키(대칭키)를 생성하고, 서버로부터 받은 공개키를 사용하여 이 세션키를 암호화함. 그 후, 암호화된 세션키를 서버에 전송.

   (3) **세션키의 복호화 및 사용**: 서버는 자신의 개인키를 사용하여 암호화된 세션키를 복호화합니다. 이제 클라이언트와 서버 양쪽 모두 동일한 세션키를 알고 있으며, 이 키를 사용하여 세션 동안의 모든 데이터를 암호화하고 복호화할 수 있음

2. 핸드셰이크과 과정이 끝나면 클라이언트, 서버간 세션키를 이용해 암호화/복호화 하며 데이터를 교환함
3. 한 번의 핸드셰이크를 통해 설정된 세션키는 하나의 세션이 종료될 때까지 유효
   - "하나의 세션" : 세션은 명시적으로 종료되거나, 일정 시간 동안 활동이 없어 타임아웃에 의해 종료됨. 브라우저가 동일한 서버에 여러 페이지나 리소스를 요청할 경우, 이러한 요청들은 모두 같은 세션 내에서 처리. 즉, 하나의 세션은 여러 HTTP 요청과 응답을 포함할 수 있음

### 3. SSL 인증서 발급 방법 2가지

- 도메인이 있을 때만 인증 기관에 의해 SSL 인증서 발급이 가능함. IP 주소만 있을 때는 "자체 서명된 인증서"를 생성해서 사용 해야 됨
  - 도메인 이름이 있을 때와 없을 때 SSL 인증서 발급 방법이 달라지는 이유는 인증 기관(Certificate Authority, CA)이 도메인 소유권을 검증하는 과정 때문입니다.

1. **도메인 이름이 있을 때** 
   - 인증 기관은 도메인 소유자가 인증서를 신청하는 것을 확인하기 위해 도메인 이름을 사용하여 소유권 검증을 수행합니다. 예를 들어, Let's Encrypt와 같은 CA는 DNS 레코드 확인, 이메일 확인, HTTP 파일 업로드 확인 등의 방법을 통해 도메인 소유자임을 인증합니다. 이 과정을 통과하면, 해당 도메인에 대한 신뢰할 수 있는 SSL 인증서를 발급받을 수 있습니다

2. **도메인 이름이 없고 IP 주소만 있을 때 **
   -  일반적으로 공개적인 인증 기관에서는 순수한 IP 주소에 대한 SSL 인증서를 발급하지 않습니다. 이는 인증 기관이 도메인 소유권 검증을 통해 인증서를 발급하는 구조 때문입니다. 그렇기 때문에 IP 주소만 사용하는 경우, 보통 자체 서명된(self-signed) 인증서를 생성하여 사용합니다. 자체 서명된 인증서는 신뢰할 수 있는 CA에 의해 서명되지 않았기 때문에 브라우저에서 보안 경고를 표시할 수 있습니다.
