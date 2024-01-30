# React Hook, Custom Hook

### 1. Hook 정의

- 함수형 컴포넌트에서 React의 상태와 생명주기 기능을 "훅(hook)"하는, 즉 연결하는 함수들.

### [중요] Hook 사용 규칙

- **함수 컴포넌트 또는 커스텀 훅 내부에서만 사용 가능함**
- **React 훅은 컴포넌트의 최상단에서만 호출되어야 함**. 훅이 조건부 블록이나 반복문, 중첩된 함수 내부에서 호출되면 안 됨.

<hr/>

### 2. [중요] 커스텀 훅 (Custom Hook)

- **정의**
  - 쉽게 생각하면, 반복되는 로직을 하나로 묶어 재사용하기 위한 함수를 그냥 커스텀 훅이라고 생각하기
  - **커스텀 훅을 사용해서 만든 기능은 하나의 컴포넌트안에서도 독립된 상태를 가짐 (로직은 공유하지만 상태를 공유하지는 않음)**


- "**react 런타임 환경"이 커스텀 훅 함수와 일반 함수를 구분짓는 기준**

  - **함수 안에서 "다른 hook을 호출하는가" (다른 hook을 호출해야 커스텀 훅이 됨)**
  - **React는 정의된 함수가 어떻게 사용되는지를 기반으로 그것이 커스텀 훅인지를 판단**
    - **정의된 함수가 다른 함수 컴포넌트나 커스텀 훅 내에서 호출되면, React는 이를 커스텀 훅으로 간주 함. 반면, 정의된 함수가 이벤트 핸들러, 콜백 함수, 다른 일반 함수 내에서 호출되면, 이는 일반 함수로 간주됨.**
    - 정의된 함수를 여러 컴포넌트에서 사용하는 경우, 각 컴포넌트 내에서 독립적으로 인식되게 됨 (각 컴포넌트별 호출된 방식에 따라 다름)  
      => 호출된 함수가 일반 함수로 인식되면, 정의된 함수 내에서 작성된 Hook 코드가 있을 경우 invalid hook call 에러를 발생시킴 (Hook의 경우 일반 함수 내에서는 사용되지 못하기 때문에)

- **(예시) 아래 `usersApiCall` 는 어떻게 사용되는지에 따라 커스텀 훅으로 인식되거나 일반 함수로 인식 됨.**

  ```jsx
  //usersApiCall.jsx
  
  import axios from "axios";
  import usersUrl from "@/api/url/usersUrl";
  import { useNavigate } from "react-router-dom";
  
  const usersApiCall = () => {
    const navigate = useNavigate();
    const signup = async (profileImage, email, password, nickname, accessToken) => {
      const body = { profileImage, email, password, nickname };
      try {
        const response = await axios.post(usersUrl.signUp(), body);
        await login(email, password, accessToken);
      } catch (error) {
        alert(error.response.data.errorMessage);
      }
  
        ... 생략
  
    };
  ```

  

  **(1) `usersApiCall`이 커스텀 훅으로 인식되는 경우**

  아래 Lobby.jsx의 코드를 보면 `usersApiCall()`의 호출이 Lobby 컴포넌트 내의 최상단에서 이루어지므로 react가 `usersApiCall`을 커스텀 훅으로 인식하게 됨 
  => `userApiCall` 내부의 `const navigate = useNavigate();` 구문에 에러가 발생하지 않고 정상적으로 사용이 가능함

  ```jsx
  //Lobby.jsx
  
  import usersApiCall from "@/api/axios/usersApiCall";
  import LobbyBtn from "@/components/lobby/LobbyBtn";
  import { useAccessTokenState } from "@/context/AccessTokenContext";
  
  const Lobby = () => {
    const accessToken = useAccessTokenState();
    const test = usersApiCall();
  
    const logout = (event) => {
      event.preventDefault();
      test.logout(accessToken);
    };
  
    return (
      <>
        <LobbyBtn text="로그아웃" onClick={logout} />
      </>
    );
  };
  
  export default Lobby;
  
  ```

  

  **(2) `usersApiCall`이 일반 함수로 인식되는 경우**
  아래 Lobby.jsx의 코드를 보면 `usersApiCall()`의 호출이 Lobby 컴포넌트 내의 logout(이벤트 핸들러 또는 일반 함수) 함수 내에서 이루어짐.
  => react가 `usersApiCall`을 일반 함수로 인식하게 됨 
  => **`userApiCall` 내부의 `const navigate = useNavigate();` 구문에서 invalid hook call 에러가 발생**

  ```jsx
  //Lobby.jsx
  
  import usersApiCall from "@/api/axios/usersApiCall";
  import LobbyBtn from "@/components/lobby/LobbyBtn";
  import { useAccessTokenState } from "@/context/AccessTokenContext";
  
  const Lobby = () => {
    const accessToken = useAccessTokenState();
  
    const logout = (event) => {
      event.preventDefault();
      usersApiCall().logout(accessToken);
    };
  
    return (
      <>
        <LobbyBtn text="로그아웃" onClick={logout} />
      </>
    );
  };
  
  export default Lobby;
  
  ```

- 참고

1. **커스텀 훅의 이름 규칙과 React의 인식**: 함수의 이름이 `use`로 시작하는 것은 React가 해당 함수를 커스텀 훅으로 "인식"하는 데 직접적인 영향을 주지 않습니다. React 런타임 자체는 함수 이름으로 커스텀 훅을 구별하지 않습니다. 대신, `use`로 시작하는 이름은 개발자와 린팅 도구에게 해당 함수가 훅의 규칙을 따라야 함을 알리는 명명 규칙으로 사용됩니다. 즉, `use` 접두사는 개발자에게 이 함수가 훅으로 사용되어야 함을 암시하는 역할을 하지만, React가 이를 커스텀 훅으로 인식하는 데 직접적인 기준은 아닙니다.
2. - 만약 `usersApiCall`이 다른 함수 컴포넌트나 커스텀 훅 내에서 호출된다면, React는 이를 커스텀 훅으로 간주할 수 있습니다. 특히, 이 함수 내부에서 React의 훅(예: `useNavigate`)을 사용하고 있으며, 함수의 이름이 `use`로 시작하는 경우, 이러한 조건들은 `usersApiCall`을 커스텀 훅으로 간주하는 데 더욱 강력한 근거가 됩니다.
   - 반대로, `usersApiCall`이 이벤트 핸들러, 콜백 함수, 다른 일반 함수 내에서 호출된다면, 이는 일반 함수로 간주됩니다. 이 경우 `usersApiCall` 내부에서 React 훅을 사용하는 것은 허용되지 않으며, 이를 시도할 경우 React는 런타임 에러를 발생시킬 것입니다.

<hr/>

### 3. React Hook 종류

 (1) useMemo

```jsx
const value = useMemo(
  () => ({
    refreshToken,
  }),
  [accessToken]
);
```

- **메모이제이션 대상**: 콜백 함수의 반환값 (`{ refreshToken }`)
- **메모이제이션 재계산 조건**: 의존성 배열 (`[accessToken]`)
