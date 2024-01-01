# Vue Basic Syntax - 02

### 1. Computed Properties

- **Computed()**

  - **반응형 데이터에 기반하여 자동으로 계산된 값 → 의존된 데이터가 변경되면 자동으로 추적**  
  - **computed 속성이 업데이트되면, 해당 속성을 사용하는 DOM 요소도 Vue에 의해 자동으로 업데이트됨**
  - **기본적으로 콜백 함수에 인자를 받지 않음** 
  
  ```javascript
  const A = computed((인자X 비워두기)=>{return 문})
  ```
  
  - 계산된 속성을 정의하는 함수
  - 미리 계산된 속성을 사용하여 템플릿에서 표현식을 단순하게 하고 불필요한 반복 연산을 줄임


- computed vs methods

  - computed 속성 대신 method로도 동일한 기능을 정의할 수 있음

  - 두 가지 접근 방식은 실제로 완전히 동일

  - **computed와 methods 차이점**

    - **computed **

      - **속성은 의존된 반응형 데이터를 기반으로 캐시(cached)된다.**
      - 의존하는 데이터가 변경된 경우에만 재평가됨
      - 즉, 의존된 반응형 데이터가 변경되지 않는 한 이미 계산된 결과에 대한 여러 참조는 다시 평가할 필요 없이 이전에 계산된 결과를 즉시 반환

      > - Cache (캐시)
      >
      >   - 데이터나 결과를 일시적으로 저장해두는 임시 저장소
      >
      >   - 이후에 같은 데이터나 결과를 다시 계산하지 않고 빠르게 접근할 수 있도록 함

    - **method**

      - **반면, method 호출은 다시 렌더링이 발생할 때마다 항상 함수를 실행**
![1](https://github.com/JeongJonggil/TIL/assets/139416006/dc81f111-fc63-4b7b-ac1b-681b7acd6e2a)
![2](https://github.com/JeongJonggil/TIL/assets/139416006/eaa64b77-3999-4ae4-9d3a-6721ba8fe20a)


### 2. Conditional Rendering

- **v-if**

  - 표현식 값의 T/F를 기반으로 요소를 조건부로 렌더링
  - v-if는 directive이기 때문에 단일 요소에만 연결 가능 **(태그 하나에만 적용 가능하다는 의미)**. 이 경우 template 요소에 v-if를 사용하여 하나 이상의 요소에 대해 적용 할 수 있음

  HTML <template> element
  > - 페이지가 로드 될 때 렌더링 되지 않지만 자바스크립트를 사용하여 나중에 문서에서 사용할 수 있도록 하는 HTML을 보유하기 위한 메커니즘
  > - `<template>` 태그는 렌더링 결과에는 포함되지 않으며, 오직 구조를 정의하는데 사용되는 HTML 태그

  ```vue
    #예시
    <!-- else if -->
        <div v-if = "name==='Alice'">Alice입니다</div>
        <div v-else-if="name==='bella'">Bella입니다</div>
        <div v-else-if="name==='Cathy'">Cathy입니다</div>
        <div v-else>아무도 아닙니다.</div>
    <!-- v-if on <template> -->
        <template v-if="name==='Cathy'">
          <div>Cathy입니다</div>
          <div>나이는 30살입니다</div>
        </template>
  ```
  
- **v-show**

  - **표현식 값의 T/F를 기반으로 요소의 가시성을 전환**

  - **v-show 요소는 항상 렌더링 되어 DOM에 남아있음**

  - CSS display 속성만 전환하기 때문

  - ```vue
    #예시
    const isShow = ref(false)
    <div v-show ="isShow">v-show</div>
    ```

- v-if **vs** v-show
![3](https://github.com/JeongJonggil/TIL/assets/139416006/bfe94a35-1bff-40b4-ac70-46f31903a591)


### 3. List Rendering

- **v-for**

  - **소스 데이터(Array, Object, number, string, iterable)를 기반으로 요소 또는 템플릿 블록을 여러 번 렌더링**

  - v-for는 alias in expression 형식의 특수 구문을 사용하여 반복되는 현재 요소에 대한 별칭(alias)을 제공

  - 인덱스(객체에서는 키)에 대한 별칭을 지정할 수 있음

    ```vue
    #v-for 예시
    <div v-for="item in items">
    	{{ item.text }}
    </div>
    
    #배열을 반복할 때는 현재 항목의 인덱스를, 객체를 반복할 때는 현재 속성의 키를 추가적인 인자로 받을 수 있음
    <div v-for="(item,index) in itmes"></div> #배열 순회
    <div v-for="(value,key,index) in object"></div> #객체 순회
    ```

- **v-for with key**

  - **반드시 v-for와 key를 함께 사용한다**.
  - 내부 컴포넌트의 상태를 일관되게 유지
  - 데이터의 예측 가능한 행동을 유지(Vue 내부 동작 관련)
  - key는 반드시 각 요소에 대한 고유한 값을 나타낼 수 있는 식별자여야 함 (key로 index는 사용 금지)

- **v-for with v-if**

  - 동일 요소에 v-for와 v-if를 함께 사용하지 않는다.

    - 동일한 요소에서 v-if가 v-for보다 우선순위가 더 높기 때문

    - v-if 조건은 v-for 범위의 변수에 접근할 수 없음

      ```vue
      #예시
      
      #문제점
      1.v-if="!todo.isComplete"는 v-for에 의해 생성된 todo 변수에 접근하기를 원하지만, v-if가 먼저 평가되기 때문에 todo은 정의되지 않았을 것이고, 결과적으로 에러 발생
      
      <ul>
          <li v-for="todo in todos" v-if="!todo.isComplete" :key="todo.id">
              {{todo.name}}
          </li>
      </ul>
         
      # 해결법-1
      computed를 활용해 필터링 된 목록을 반환하여 반복 하도록 설정
      
      const completeTodos = computed(() =>{
      	return todos.value.filter((todo)=> !todo.isComplete)
      })
      
      <ul>
          <li v-for="todo in completeTodos" :key="todo.id">
              {{todo.name}}
          </li>
      </ul>
      
      # 해결법-2
      v-for와 template 요소를 사용하여 v-if를 이동
      
      
      <ul>
          <template v-for="todo in todos" :key="todo.id">
              <li v-if="!todo.isComplete">
                  {{todo.name}}
              </li>
          </template>
      </ul>
      ```
      

### 4. Watchers

- watch()

  - 반응형 데이터를 감시하고, 감시하는 데이터가 변경되면 콜백 함수를 호출

  - 구조

    ```vue
    watch(variable, (newValue, oldValue)=>{})
    ```

    - variable
      - 감시하는 변수
    - newValue
      - 감시하는 변수가 변화된 값
      - 콜백 함수의 첫번째 인자
    - oldValue
      - 콜백 함수의 두번째 인자

- Computed vs Watchers

> `computed` 속성과 `watchers`는 서로 다른 경우에 사용됩니다. computed 속성은 종속된 데이터가 변경될 때마다 자동으로 다시 계산되는 반응형 속성을 만드는 데 적합하며, watch는 특정 데이터의 변경을 감시하고 그에 대응하는 로직을 실행하는 데 사용됩니다.
>
> - **Computed 사용 예시**: 사용자의 이름과 성을 합쳐서 전체 이름을 표시해야 하는 경우.
> - **Watcher 사용 예시**: 사용자의 입력 값에 따라 서버에서 데이터를 가져와야 하거나, 입력 값의 유효성을 비동기적으로 검사해야 하는 경우.
![4](https://github.com/JeongJonggil/TIL/assets/139416006/070c1753-c105-42e9-b544-7fe6fd53a7ce)


### 5. Lifecycle Hooks

- Vue 인스턴스의 생애주기 동안 특정 시점에 실행되는 함수
- 개발자가 특정 단계에서 의도하는 로직이 실행될 수 있도록 함
![5](https://github.com/JeongJonggil/TIL/assets/139416006/3bee012f-0ee0-4e0f-a5b1-0b8d10fba7d3)


### 6. Vue Style Guide
![6](https://github.com/JeongJonggil/TIL/assets/139416006/ba6f76d9-571d-424b-9cfb-d9c0f2b2cd5a)
![7](https://github.com/JeongJonggil/TIL/assets/139416006/94cc8a1b-fc50-452b-ae03-1caca545c0d5)
![8](https://github.com/JeongJonggil/TIL/assets/139416006/6336a7ca-9317-451c-8611-1fb58495f481)


### 7. 참고
![9](https://github.com/JeongJonggil/TIL/assets/139416006/2e9986ae-e170-46dd-bec1-8624f4227c3d)
![10](https://github.com/JeongJonggil/TIL/assets/139416006/1d31955f-5104-43c7-b633-0d10dea282ad)
![11](https://github.com/JeongJonggil/TIL/assets/139416006/c4b0559b-2dca-40d2-9288-8df925aa6737)
![12](https://github.com/JeongJonggil/TIL/assets/139416006/ea2660c3-97d3-4851-80cf-b6fab7e52eea)
