# React Context API

### 1. 개요

- 데이터를 전역적으로 공유할 수 있는 방법을 제공하기 위함. 이를 통해 중첩된 컴포넌트 사이에서 props를 일일이 전달하지 않고도 데이터를 공유할 수 있음.

### 2. 사용법

```react
//CounterProvider.jsx

import { createContext, useState } from 'react';

const CounterContext = createContext(inital);

function CounterProvider({ children }) {
  const counterState = useState(1);
  return (
    <CounterContext.Provider value={counterState}>
      {children}
    </CounterContext.Provider>
  );
}

export default CounterProvider;

```

```react
// App.js
import React from 'react';
import { CounterProvider } from './path/to/CounterProvider';
import ChildComponent from './path/to/ChildComponent';

function App() {
  return (
    <CounterProvider>
      <ChildComponent />
      {/* 여기에 다른 컴포넌트들을 추가할 수 있습니다 */}
    </CounterProvider>
  );
}

export default App;
```



1. `const CounterContext = createContext();`: 전역적으로 사용가능한 박스를 하나 생성
2. `return <CounterContext.Provider value={counterState}>` : CounterProvider 컴포넌트가 CounterContext.Provider라는 구문을 통해 `value`가 담긴 박스(CounterContext)를 하위의 { childern }에게 제공.
3. 정리하면, `CounterContext ` 자체는 전역적으로 사용 가능하지만, 그것이 제공하는 실제 값은 `CounterProvider`에 의해 제공되는 범위 내에서만 접근할 수 있음. **Provider로 감싸져 있지 않은 컴포넌트에서 context에 접근하면 기본 값(inital)만 사용됨**
4. 다른 하위 컴포넌트에서 context를 사용하려면 `useContext(CounterContext)`를 사용해서 직접 박스에 접근하면 됨

### 3. {children} 매개변수에 대한 이해

- JSX에서 `<CounterProvider>` 태그 사이에 위치한 모든 것들이 `children` prop으로 전달됨. 
- `function CounterProvider({ children }) {}` 
  -  여기서 { children }의 중괄호는 JavaScript의 비구조화 할당(destructuring assignment) 문법임. 
  - `{children}`의 비구조화 할당은, `props` 객체에서 `children` 속성을 직접 추출하여 사용하겠다는 의미. 이를 사용하면, `props.children`이라고 쓰는 대신에 `children`이라고만 써서 해당 속성을 사용할 수 있습니다.

- `<CounterContext.Provider value={counterState}>
        {children}
      </CounterContext.Provider>`
  - 여기서 { children }의 중괄호는 prop받은 children 변수를 html 문법에서 변수로서 사용하겠다는 의미임.



### 4. Context + LocalStorage

-  애플리케이션의 전역 상태 관리와 데이터의 영구 저장을 결합하기 위해서는 `Context`와 `LocalStorage`를 같이 사용해야 됨

  1. **상태 관리 및 전달**: `Context`는 React 컴포넌트 트리 전체에 걸쳐 데이터를 전달하고 공유하는 데 매우 유용합니다. 이를 통해 props 드릴링(prop drilling) 문제를 해결하고, 상태 관리를 효율적으로 할 수 있습니다.

  2. **영구 데이터 저장**: `localStorage`는 브라우저 세션 간에 데이터를 유지하는 데 사용됩니다. 사용자가 애플리케이션을 닫았다가 다시 열 때 이전 상태를 복원하는 데 유용합니다.

  3. **상태 동기화와 영구 저장의 결합**: `Context`를 통해 상태가 관리되고 `localStorage`를 통해 이 상태가 영구적으로 저장됩니다. 예를 들어, 사용자가 로그인 상태를 변경하면 이 변경 사항이 `Context`를 통해 애플리케이션 전체에 전파되고, 동시에 `localStorage`에도 저장되어 브라우저를 재시작하거나 새로고침했을 때 상태가 유지됩니다.

  `localStorage`만 사용하는 경우, 상태의 변경을 실시간으로 감지하는 것은 자동으로 이루어지지 않습니다. `localStorage`는 단순히 키-값 기반의 저장소로, 데이터가 변경될 때 이를 자동으로 감지하거나 리스너를 제공하지 않습니다. 따라서 상태가 변경될 때마다 명시적으로 `localStorage`를 업데이트하는 코드를 작성해야 합니다.

  결론적으로, `Context`와 `localStorage`를 함께 사용하면, React 컴포넌트 간의 효율적인 상태 공유와 더불어 사용자 세션 간에 이 상태를 영구적으로 저장할 수 있는 이점을 얻을 수 있습니다. 이는 특히 사용자 인증 상태, 테마 설정, 사용자 선호도 등을 관리할 때 매우 유용합니다.



### 5. 컴포넌트 재랜더링

- **react에서는 기본적으로 상위 컴포넌트들이 재랜더링 되면 모든 하위 컴포넌트들도 재랜더링됨**
  - 상위 컴포넌트들의 **재랜더링을 최대한 방지하기 위해 `useMemo`, `useCallBack`과 같은 훅을 사용**하는 이유 중 하나임
- 그래서 context를 하위 컴포넌트들에게 제공하는 `Provider`태그를 사용할 떄는 이 부분을 고려해야됨

- 예시

  ```jsx
    //App.jsx
      <AccessTokenProvider>
        <WebSocketProvider>
          <WebMobileLayout>
                ...
          </WebMobileLayout>
        </WebSocketProvider>
  	</AccessTokenProvider>
  ```

  - 위 코드에서 `AccessTokenProvider`에 있는 상태들이 변경이 되면 `AccessTokenProvider`가 재랜더링 됨.
  - 이 때 `AccessTokenProvider`의 상태를 직접적으로 사용하는 하위 컴포넌트가 아니더라도 하위 컴포넌트는 재랜더링 됨. 
  - `WebSocketProvider`나 하위 컴포넌트에 `useEffect `함수가 있는 컴포넌트들은 재랜더링 될 때마다 해당 `useEffect`가 실행되게 됨
  - 결과적으로  `AccessTokenProvider`의 상태가 바뀌면 하위 컴포넌트에 있는 `useEffect `함수가 전부 다시 실행됨
  - 이러한 로직을 방지하기 위해 `AccessTokenProvider`에 `useMemo`,`useCallback` 훅을 사용하여 재랜더링을 최소화 시킴
  
- Q & A

  Q . 하위컴포넌트들은 부라우저에 출력이 안 된 상태일텐데 상위컴포넌트의 상태가 변경된다고 하위 컴포넌트가 재랜더링 된다는게 무슨말이지?? 출력이 안되면 랜더링 자체가 안되있는거 아닌가?

  A . React에서 "렌더링"이라는 용어는 실제 브라우저에 픽셀을 그리는 것만을 의미하지 않습니다. React의 렌더링 과정은 크게 두 단계로 나뉩니다:

  1. **가상 DOM 렌더링 (Virtual DOM Rendering)**: 컴포넌트의 상태가 변경되면, React는 해당 컴포넌트를 포함하여 그 하위 컴포넌트들의 새로운 가상 DOM 트리를 생성합니다. 이 과정은 실제 DOM에 변화를 반영하기 전에 메모리 상에서 이루어집니다.

  2. **실제 DOM 업데이트 (Real DOM Update)**: 가상 DOM에서 이루어진 변경 사항들이 실제 DOM으로 반영되는 단계입니다. 이 단계에서 브라우저에 실제로 픽셀이 그려집니다.

  하위 컴포넌트가 브라우저에 실제로 출력되지 않았더라도, 상위 컴포넌트의 상태 변화가 하위 컴포넌트의 가상 DOM 생성을 유발할 수 있습니다. 이는 하위 컴포넌트가 조건부 렌더링(예: `if` 문이나 `&&` 연산자 사용)을 통해 실제 DOM에 반영되지 않았을 때에도 마찬가지입니다.

  예를 들어, 상위 컴포넌트의 상태가 변경되면 React는 그 컴포넌트뿐만 아니라 그 하위 컴포넌트들에 대해서도 새로운 가상 DOM을 생성합니다. 이 과정에서 하위 컴포넌트들은 새로 "렌더링" 되지만, 이들의 최종 출력 여부는 조건부 렌더링 로직에 의해 결정됩니다.

  따라서, 실제 브라우저에 그려지지 않는 컴포넌트라도 React는 그 상태를 추적하고 관리하며, 필요에 따라 가상 DOM 렌더링 과정을 거치게 됩니다.