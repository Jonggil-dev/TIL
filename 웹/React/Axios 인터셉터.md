# Axios 인터셉터

### 1. 개요

- **Axios를 사용한 HTTP 요청, 응답을 가로채서 중간에 원하는 로직을 수행할 수 있도록 해주는 기능**
- 즉, 요청이 서버로 전송되기 전이나 서버로부터 응답을 받은 직후에 추가적인 코드를 실행할 수 있게 해주는 기능
- 전역적인 로그 기록, 인증 토큰 추가, 에러 처리 등 공통된 작업에 주로 사용

### 2. 사용법

- 인터셉터를 한 번 설정하면, 해당 인스턴스를 사용하는 모든 Axios 요청에 대해 설정한 인터셉터 로직이 자동으로 실행됨.

1. **인터셉터 설정하기**: 먼저, Axios 인스턴스를 생성하고 이 인스턴스에 인터셉터를 추가

```javascript
import axios from 'axios';

// Axios 인스턴스 생성
const api = axios.create({
  baseURL: 'https://api.example.com',
});

// 요청 인터셉터 추가
api.interceptors.request.use(config => {
  // 요청 헤더에 인증 토큰 추가
  const token = localStorage.getItem('authToken');
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`;
  }
  return config;
}, error => {
  // 요청 에러 처리
  return Promise.reject(error);
});

// 응답 인터셉터 추가
api.interceptors.response.use(response => {
  // 응답 데이터 처리
  return response;
}, error => {
  // 응답 에러 처리
  return Promise.reject(error);
});
```

2. **Axios 요청 보내기**

```javascript
// GET 요청
api.get('/users')
  .then(response => {
    console.log(response.data);
  })
  .catch(error => {
    console.error(error);
  });

// POST 요청
api.post('/posts', { title: 'New Post', content: 'Here is the content.' })
  .then(response => {
    console.log(response.data);
  })
  .catch(error => {
    console.error(error);
  });
```

이렇게 하면, `/users` 또는 `/posts` 엔드포인트로의 요청을 보낼 때, 자동으로 요청 인터셉터가 요청 전에 실행되어 헤더에 인증 토큰을 추가하고, 응답 인터셉터가 서버로부터 응답을 받은 후에 실행. 인터셉터는 Axios 요청 메서드(`get`, `post`, `put`, `delete` 등)를 호출할 때마다 자동으로 적용됨.



### 3. header에 jwt토큰 담기 + 토큰 만료시 새로운 토큰 요청하기 코드

```jsx
import axios from "axios";
import { baseUrl } from "@/api/url/baseUrl";
import { useNavigate } from "react-router-dom";
import { useAccessTokenState } from "@/context/AccessTokenContext";

const useAxiosConfig = () => {
  const accessToken = useAccessTokenState();
  const navigate = useNavigate();

  const privateAxios = axios.create({
    baseURL: baseUrl,
  });

  privateAxios.interceptors.request.use(
    (config) => {
      if (accessToken.accessToken) {
        config.headers.Authorization = `Bearer ${accessToken.accessToken}`;
      }
      return config;
    },
    (error) => {
      return Promise.reject(error);
    }
  );

  privateAxios.interceptors.response.use(
    (response) => {
      return response;
    },
    async (error) => {
      const statusCode = error.response?.status;
      if (statusCode === 401) {
        try {
          const response = await axios.post(baseUrl + "/api/auth/reissue", {
            grantType: "Bearer",
            accessToken: accessToken.accessToken,
            refreshToken: accessToken.refreshToken,
          });
          accessToken.setAccessToken(response.data.accessToken);
          accessToken.setRefreshToken(response.data.refreshToken);

          const originalRequest = error.config;
          originalRequest.headers.Authorization = `Bearer ${accessToken.accessToken}`;

          // 복제된 요청을 다시 보내고 응답을 반환
          return axios(originalRequest);
        } catch (refreshError) {
          navigate("/");
          return Promise.reject(refreshError);
        }
      }
      return Promise.reject(error);
    }
  );
  return { privateAxios };
};

export default useAxiosConfig;

```

