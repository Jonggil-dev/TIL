# Vue Basic Syntax - 01

### 1. Template Syntax

- DOM을 기본 구성 요소 인스턴스의 데이터에 선언적(Vue Instance와 DOM을 연결)으로 바인딩할 수 있는 HTML 기반 템플릿(확장된 문법 제공) 구분을 사용 
  → 쉽게, Vue.js라는 프레임워크를 사용하여 웹 페이지의 요소들과 데이터를 연결하는 방법을 설명하고 있으며, 이를 위해 확장된 HTML 문법을 사용하는 템플릿 시스템이 있다"는 의미

- Text Interpolation

  ```vue
  <p>Message : {{ msg }} </p>
  ```
  
  - 데이터 바인딩의 가장 기본적인 형태
  - 이중 중괄호 구문 (콧수염 구문)을 사용
  - 콧수염 구문은 해당 구성 요소 인스턴스의 msg 속성 값으로 대체
  - msg 속성이 변경될 때마다 업데이트 됨

- Raw HTML

  ```vue
  <div v-html="rawHtml"></div>
  const rawHtml = ref('<span style="color:red">This should be red.</span>')
  ```

  - 콧수염 구문은 데이터를 일반 텍스트로 해석하기 때문에 실제 HTML을 출력하려면 v-html을 사용해야 함

- Attribute Bindings

  ```vue
  <div v-bind: id="dynamicID"></div>
  const dynamicID = ref('my-id')
  ```

  - 콧수염 구문은 HTML 속성 내에서 사용할 수 없기 때문에 v-bind를 사용
  - HTML의 id 속성 값을 vue의 dynamicId 속성과 동기화 되도록 함
  - 바인딩 값이 null이나 undefind인 경우 렌더링 요소에서 제거됨

- JavaScript Expressions

  ```vue
  {{ number + 1 }}
  {{ ok ? 'YES' : NO }}
  {{ message.split('').reverse().join('') }}
  <div : id=""`list-${id}`"></div>
  ```

  - Vue는 모든 데이터 바인딩 내에서 JavaScript 표현식의 모든 기능을 지원
  - Vue 템플릿에서 JavaScript 표현식을 사용할 수 있는 위치
    - 콧수염 구문 내부
    - 모든 directive의 속성 값(v-로 시작하는 특수 속성)

- Expressions 주의사항

  - 각 바인디에는 하나의 단일 표현식만 포함될 수 있음
    - 표현식은 값으로 평가할 수 있는 코드 조각**(return 뒤에 사용할 수 있는 코드여야 함)**
  - 작동하지 않는 경우

  ```vue
  <!-- 표현식이 아닌 선언식 -->
  {{ const number = 1 }}
  
  <!-- 흐름제어도 작동하지 않음. 삼항 표현식을 사용 -->
  {{ if (ok) { return message } }}
  ```



### 2. Directive

- 'v-' 접두사가 있는 특수 속성

- 특징

  - Directive의 속성 값은 단일 JavaScript 표현식이어야 함 (v-for, v-on 제외)
  - 표현식 값이 변경될 때 DOM에 반응적으로 업데이트를 적용
  - 예시
    - v-if 는 seen 표현식 값의 T/F를 기반으로 <p> 요소를 제거/삽입

  ```vue
  <p v-if="seen">Hi There</p>
  ```
  
![1](https://github.com/JeongJonggil/TIL/assets/139416006/a8c0dfce-7d18-4ab8-8716-22e38174e543)

- Directive - Arguments

  - 일부  directive는 뒤에 콜론(:)으로 표시되는 인자를 사용할 수 있음
  - 아래 예시의 href는 HTML a 요소의 href 속성 값을 myUrl 값에 바인딩 하도록 하는 v-bind의 인자

  ```vue
  <a v-bind:href="myUrl">Link</a>
  <button v-on:click="doSomething">Button</button>
  ```

- Directive - Modifiers

  - .(dot)로 표시되는 특수 접미사로, directive가 특별한 방식으로 바인딩되어야 함을 나타냄
  - 예를 들어 .prevent는 발생한 이벤트에서 event.preventDefault()를 호출하도록 v-on에 지시하는 modifier

  ```vue
  <form @submit.prevent="onSubmit">...</form>
  ```

### 3. Dynamically data binding

- v-bind

  - 하나 이상의 속성 또는 컴포넌트 데이터를 표현식에 동적으로 바인딩

![2](https://github.com/JeongJonggil/TIL/assets/139416006/032d5db4-1d24-4a5c-b495-4429c92185c8)
![3](https://github.com/JeongJonggil/TIL/assets/139416006/671c6f24-9f1f-4007-ac02-e7fa7eaf7611)
![4](https://github.com/JeongJonggil/TIL/assets/139416006/ece3dca9-d44e-4cfd-a9f2-7b53f93607d2)

### 4. Class and Style Bindings

- 클래스와 스타일은 모두 속성이므로 v-bind를 사용하여 다른 속성과 마찬가지로 동적으로 문자열 값을 할당할 수 있음
- 그러나 단순히 문자열 연결을 사용하여 이러한 값을 생성하는 것은 번거롭고 오류가 발생하기가 쉬움
- Vue는 클래스 및 스타일과 함께 v-bind를 사용할 때 객체 또는 배열을 활용한 개선 사항을 제공

![5](https://github.com/JeongJonggil/TIL/assets/139416006/1556e277-f394-4354-b6fe-44c603a76463)
![6](https://github.com/JeongJonggil/TIL/assets/139416006/9fc4f551-f976-4a2d-8b2d-16bdae006448)
![7](https://github.com/JeongJonggil/TIL/assets/139416006/1255addf-db44-40b4-8e4c-3e6ede10b5fc)
![8](https://github.com/JeongJonggil/TIL/assets/139416006/5f40b431-e744-4e8e-83e5-df49809886d4)
![9](https://github.com/JeongJonggil/TIL/assets/139416006/7b8931af-8dd6-4206-9e05-749cc3c7c1ab)
![10](https://github.com/JeongJonggil/TIL/assets/139416006/e2afda80-ca3c-47f2-8462-ec53e9377791)
![11](https://github.com/JeongJonggil/TIL/assets/139416006/699c3611-66ee-41f9-ae3d-abdd6edfa4ae)
![12](https://github.com/JeongJonggil/TIL/assets/139416006/30837df3-a10a-4a8f-839e-42761460fe77)
![13](https://github.com/JeongJonggil/TIL/assets/139416006/6d9cd1f8-3722-4835-ad34-27a2218ed071)
![14](https://github.com/JeongJonggil/TIL/assets/139416006/ff768011-11d9-434e-a639-ea2d48288652)

### 5. Event Handling

- v-on
  - DOM 요소에 이벤트 리스너를 연결 및 수신
![15](https://github.com/JeongJonggil/TIL/assets/139416006/7b8287eb-d924-4d02-bc78-e2005744c638)
![16](https://github.com/JeongJonggil/TIL/assets/139416006/8430d766-5e7b-493a-b752-82d653952491)
![17](https://github.com/JeongJonggil/TIL/assets/139416006/869723e3-9f92-446e-85e8-ef556f092a79)
![18](https://github.com/JeongJonggil/TIL/assets/139416006/d4bf5cfd-fc2d-4c00-ac39-570d5cdd1ac1)
![19](https://github.com/JeongJonggil/TIL/assets/139416006/a8090857-e62e-43b9-b0b6-7f197c25ba0c)
![20](https://github.com/JeongJonggil/TIL/assets/139416006/8444d34f-19a8-4b29-a5e8-7baaee3ff4c4)
![21](https://github.com/JeongJonggil/TIL/assets/139416006/08f43326-4250-4725-8162-13a7f11f1b76)
![22](https://github.com/JeongJonggil/TIL/assets/139416006/fed3e5fe-3e72-4dd0-a91c-7a2349da14d9)

### 6. Form Input Bindings

- form을 처리할 때 사용자가 input에 입력하는 값을 실시간으로 JavaScript 상태에 동기화해야 하는 경우 (양방향 바인딩)
- 양방향 바인딩 방법
  - v-bind와 v-on을 함께 사용
  - v-model 사용

![23](https://github.com/JeongJonggil/TIL/assets/139416006/d7cae3b1-f778-4949-b793-029cf66a5e5b)
![24](https://github.com/JeongJonggil/TIL/assets/139416006/f18487cd-1486-4407-9467-d1b48b4b9d61)

- **v-model**
  - form input 요소 또는 컴포넌트에서 양방향 바인딩을 만듦
![25](https://github.com/JeongJonggil/TIL/assets/139416006/63f1a341-4933-461e-9a49-4887a7e1185c)
![26](https://github.com/JeongJonggil/TIL/assets/139416006/38faec45-02a9-4ef6-a699-0e81e35ced82)

