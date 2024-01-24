# Router

### 1. Router

- 네트워크에서 경로를 선택하는 프로세스

- 웹 애플리케이션에서 다른 페이지 간의 전환과 경로를 관리하는 기술

  > GPT : 라우팅(routing)은 사용자가 웹 애플리케이션 내에서 다양한 페이지를 방문할 때 해당 URL에 따라 적절한 뷰(view)나 컴포넌트(component)를 화면에 보여주는 프로세스를 말합니다. 이 기능을 통해 싱글 페이지 애플리케이션(SPA)에서 사용자가 다른 주소로 이동할 때마다 서버에 새로운 페이지를 요청하는 대신, 클라이언트 사이드에서 미리 정의된 경로에 따라 동적으로 컨텐츠를 렌더링할 수 있습니다.

### 2. Vue Router

- project 생성 시 Add vue Router ~~ 부분  yes로 하기

- RouterLink
  - 페이지를 다시 로드 하지 않고 URL을 변경하고 URL 생성 및 관련 로직을 처리
  - HTML의 a 태그를 렌더링

- RouterView
  - URL에 해당하는 컴포넌트를 표시
  - 어디에나 배치하여 레이아웃에 맞출 수 있음

```vue
<template>
  <header>
    <img alt="Vue logo" class="logo" src="@/assets/logo.svg" width="125" height="125" />

    <div class="wrapper">
      <HelloWorld msg="You did it!" />

      <nav>
        <RouterLink to="/">Home</RouterLink>
        <RouterLink to="/about">About</RouterLink>
      </nav>
    </div>
  </header>

  <RouterView />
</template>
```

- views
  - RouterView 위치에 렌더링 할 컴포넌트를 배치
  - 기존 components 폴더와 기능적으로 다른 것은 없으며 단순 분류의 의미로 구성됨
  - **일반 컴포넌트와 구분하기 위해 컴포넌트 이름을 View로 끝나도록 작성하는 것을 권장**

### 3. Basic Routing

- 라우팅 기본

  - index.js에 라우터 관련 설정 작성(주소,이름,컴포넌트)
  - RouterLink의 'to' 속성으로 index.js에서 정의한 주소 속성 값(path)을 사용

- Named Routes

  - name 속성 값에 경로에 대한 이름을 지정
  - **to 속성에 v-bind 설정하기 + 속성값은 객체로**

  ```
  <nav>
    <RouterLink :to="{ name: 'home' }">Home</RouterLink>
    <RouterLink :to="{ name : 'about' }">About</RouterLink>
  </nav>
  ```

### 4. Dynamic Route Matching with Params

- django의 variable routing과 유사함
- django에서 `<int:pk>` → **vue에서는 `:id`**
- **RouterLink 태그에서 params 속성사용**
  - prams 속성 값은 객체로 입력
  - 속성 값 객체의 키명과 routing url 변수명과 동일하게 작성해야 됨

```vue
#index.js
  {
      path: '/user/:id',
      name: UserView,
      component: UserView
   }
   
#App.vue
<script setup>
  import { ref } form 'vue'
  const userId = ref(1)
</script>

<template>
    <nav>
      <RouterLink :to="{ name : 'user', params: {id: userId } }"></RouterLink>
    </nav>
</template>
```

- template에서 라우트의 매개변수는 컴포넌트 에서 $route.params로 참조 가능 (script에서 사용 권장)

  - **script에서 라우트의 매개변수를 받기 위해서는 useRoute import해서 사용**

    ```vue
    <script setup>
    import { ref } from 'vue'
    import { useRoute } from 'vue-router'
    
    const route = useRoute()
    const userId = ref(route.params.id)
    ```

### 5. Programmatic Navigation

- router의 인스턴스 메서드를 사용해 RouterLink로 a 태그를 만드는 것처럼 프로그래밍으로 네비게이션 관련 작업을 수행할 수 있음

  - 다른 위치로 이동하기 : **router.push()**
    - router.push의 속성으로는 **name,params,path,qeury**가 있음
       - name
         - new VueRouter에서 정의한 routes 설정에서 name 속성을 통해 라우트에 이름을 지정. 
         - 이름을 사용하여 라우트를 참조할 때 params를 사용하여 동적 세그먼트를 전달할 수 있습니다.
       - params
         - URL에 포함될 동적 세그먼트 값입니다. 이 값은 경로에 정의된 동적 세그먼트 `:param` 형식에 매핑되어 URL의 일부가 됨.
         - 이름이 지정된 라우트를 사용할 때 `params`를 이용하여 특정 세그먼트에 값을 사용 가능
       - path
         - 라우트의 URL 경로입니다. `/user/123`과 같이 직접적인 URL을 지정할 때 사용됩니다.
         - `path`를 사용할 때는 동적 세그먼트를 직접 URL 경로에 포함시켜야 하며, `params`는 사용되지 않습니다.
       - query
         - URL의 쿼리 파라미터를 나타냅니다. 이는 `?key=value` 형태의 쿼리 스트링으로 URL에 포함됩니다.
         - `query`는 라우트의 정의와 상관없이 언제든지 추가될 수 있으며, `path` 또는 `name`과 함께 사용될 수 있습니다.
  - 현재 위치 바꾸기 : router.replace()
  
  - user view에서 홈으로 가는 버튼 만들기

```vue
<template>
  <div>
    <h1>UserView</h1>
    <h2>{{ $route.params.id }} 유저의 페이지 입니다.</h2>
    <button @click="goHome">홈으로!</button>
  </div>
</template>

<script setup>
// 프로그래밍 방식 네비게이션
  import { useRoute,useRouter } from 'vue-router';

  const router = useRouter()

  const goHome = function(){
      router.push({ name : 'home' }) 
    // url에 인자 전달하려면 params속성 사용 가능  
    // router.push({ name : 'home', params: {username:'alice'}})
    // replace
    // router.replace({ name : 'home'})
}
</script>
```

### 6. Navigation Guard

- Vue router를 통해 특정 URL에 접근할 때 다른 URL로 redirect를 하거나 취소하여 네이게이션을 보호
- ex) 인증 정보가 없으면 특정 페이지에 접근하지 못하게 함
- 종류
  - Globally (전역 가드)
    - 애프릴케이션 전역에서 동작
    - index.js에서 정의
  - Per-route (라우터 가드)
    - 특정 route에서만 동작
    - index.js의 각 routes에 정의
  - In-component (컴포넌트 가드)
    - 특정 컴포넌트 내에서만 동작
    - 컴포넌트 Script에 정의

- **Globally Guard (index.js 에 코드 작성)**

  - **router.beforeEach(callbackFunc)**

    - **다른 URL로 이동하기 직전에 실행되는 함수** (Global Before Guards)

    - index.js routes 밖에서 사용

      ```vue
      router.beforeEach((to,from) => {
        return false
      })
      
      1. to : 이동 할 URL 정보가 담긴 Route 객체
      2. from : 현재 URL 정보가 담긴 Route 객체
          ※ 부가설명 : 
           to와 from 매개변수는 Vue Router의 네비게이션 가드에서 자동으로 제공. 개발자가 이 매개변수들을 직접 지정할 필요는 없음. Vue Router 	내에서 라우트 간의 전환(네비게이션)이 발생할 때, 네비게이션 가드가 호출되면 Vue Router는 자동으로 현재 라우트(from)와 이동하려는 라우	   트(to)에 관한 정보를 해당 가드 함수에 제공.
      4. 선택적 반환 값 
       (1) false : 현재 네비게이션을 취소
       (2) Route Location : 다른 위치로 redirect
       (3) return이 없다면 'to' URL Route 객체로 이동
      
      #예시
      router.beforeEach((to,from)=>{
        const isAuthenticated =false
      
        if (!isAuthenticated && to.name !== 'login'){
          console.log('로그인 하십시오')
          return { name : 'home'}
        }
      })
      ```

  - **router.beforeEnter(callbackFunc)**

    - **route에 진입했을 때만 실행되는 함수**
    - 매개변수, 쿼리 값이 변경될 때는 실행되지 않고 **다른 경로에서 탐색할 때만 실행됨**
      - ex) user/1 → user/2 이렇게 변경될 때는 실행 안됨
    - **routes 객체에서 정의함**
    - 인자들은 beforeEach와 동일

- **In-component Guard (component.vue 파일에 작성하는 코드)**

  - **onBeforeRouteLeave**

    - 현재 라우트에서 다른 라우트로 이동하기 전에 실행
    - 사용자가 현재 페이지를 떠나는 동작에 대한 로직을 처리

    ```vue
    #~~component.vue
    
    <script setup>
    import {onBeforeRouteLeave} from 'vue-router'
    
    onBeforeRouteLeave((to,from)=>{
      const answer = window.confirm('정말 떠나실 건가요?')
    	if(answer === false){
        return false
      }
    })
    
    </script>
    ```

  - **onBeforeRouteUpdate**

    - 이미 렌더링된 컴포넌트가 같은 라우트 내에서 업데이트 되기 전에 실행
    - 동일한 라우트에서 업데이트 시 추가적인 로직을 처리

    ```vue
    <script setup>
    import {onBeforeRouteUpdate} from 'vue-router'
    
    onBeforeRouteLeave((to,from)=>{
      userId.value = to.params.id
      }
    })
    </script>
    ```

### 7. 참고 (Lazy Loading Routes)

- Lazy Loading Routes
  - 서버 첫 빌드시 Route에 등록된 컴포넌트들은 한 번에 전부 로드가 됨
  - 하지만, 첫 빌드 시 해당 컴포넌트를 로드 하지 않고, 해당 경로를 처음으로 방문할 때만 컴포넌트를 로드 하는 것을 Lazy Loading Routes라고 함 
  - route작성시 component를 콜백함수 형태로 작성하면 됨 (Vue에서 about 페이지 route 참고)
