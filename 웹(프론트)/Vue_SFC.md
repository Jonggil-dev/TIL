# Single-File Components

### 1. Component

- 재사용 가능한 코드 블록
- UI 를 독립적이고 재사용 가능한 일부분으로 분할하고 각 부분을 개별적으로 다룰 수 있음

### 2. SFC(Single-File Components)

- 컴포넌트의 템플릿, 로직 및 스타일을 하나의 파일로 묶어낸 특수한 파일 형식 (*.vue 파일)
- **하나의 파일로 컴포넌트 하나를 구성하는 것**
- 하나의 파일(Vue SFC)에는 HTML, CSS 및 자바스크립트 3개를 하나로 합친 것

```vue
#SFC파일 예시
<template>
	<div class="gretting">{{ msg }}</div>
</template>

<script setup>
import { ref } from 'vue'
  
const msg = ref('Hello World!')
</script>

<style scoped>
  .greeting{
    color:red
  }
</style>
```

- SFC 문법 개요

  - 각 *.vue파일은 세 가지 유형의 최상위 언어 블록 <template>, <script>,<style>으로 구성됨

  - 언어 블록의 작성 순서는 상관 없으나 일반적으로 template -> script -> style 순서로 작성

  - 각 *. vue 파일은 최상위 <template>, <script setup> 블록을 하나만 포함할 수 있음(일반 <script> 제외)

  - <script setup> : 컴포넌트의 setup()함수로 사용되며 컴포넌트의 각 인스턴스에 대해 실행 (return도 안써도 됨)

  - <style scoped> → scoped가 지정되면 CSS는 현재 컴포넌트에만 적용

### 3. SFC build tool(vite)

- 프론트 엔드 개발 도구
- 빠른 개발환경을 위한 빌드 도구와 개발 서버를 제공 (https://vitejs.dev/)

### 4. Node Package Manager(NPM)

- Node.js의 기본 패키지 관리자

- **Node : Chrome의 V8 JavaScript 엔진을 기반으로 하는 Server-Side 실행 환경**

- Node.js의 영향 
  - 기존에 브라우저 안에서만 동작할 수 있었던 JavaScript를 브라우저가 아닌 서버 측에서도 실행할 수 있게 함 → **프론트엔드와 백엔드에서 동일한 언어로 개발할 수 있게 됨**
  - NPM을 활용해 수많은 오픈 소스 패키지와 라이브러리를 제공하여 개발자들이 손쉽게 코드를 공유하고 재사용할 수 있게 함

### 5. Vite 프로젝트 구조

- **node_modules**

  - **django에서 venv 같은 느낌**
  - Node.js 프로젝트에서 사용되는 외부 패키지들이 저장되는 디렉토리
  - 프로젝트의 의존성 모듈을 저장하고 관리하는 공간
  - 프로젝트가 실행될 때 필요한 라이브러리와 패키지들을 포함
  - .gitignore에 작성됨

- **package-lock.json, package.json**

  - **django에서 requirements.txt 같은 느낌**
  - 패키지들의 실제 설치 버전, 의존성 관계, 하위 패키지 등을 포함하여 패키지 설치에 필요한 모든 정보를 포함 
  - npm install 명령을 통해 패키지를 설치할 때, 명시된 버전과 의존성을 기반으로 설치

  - package.json은 lock.json을 보조하는 파일 정도로 기억

- **public 디렉토리**
  - 주로 다음 정적파일을 위치 시킴
    - 소스코드에서 참조되지 않는
    - 항상 같은 이름을 갖는
    - import 할 필요 없는
  - 항상 root 절대 경로를 사용하여 참조
    - public/icon.png는 소스 코드에서/icon.png로 참조 할 수 있음
- **index.html**
  - Vue 앱의 기본 HTML 파일
  - 앱의 진입점 (entry point)
  - Root 컴포넌트의 App.vue가 해당 페이지에 마운트(mount) 됨
  - 필요한 스타일 시트, 스크립트 등의 외부 리소스를 로들 할 수 있음(ex. bootstrap CDN)

- **src 디렉토리**
![1](https://github.com/JeongJonggil/TIL/assets/139416006/944c87b7-be44-49c2-b0b4-70a042dc54f4)
![2](https://github.com/JeongJonggil/TIL/assets/139416006/4273cb52-30d4-4c33-8e7e-5cabe199ea14)
![3](https://github.com/JeongJonggil/TIL/assets/139416006/cbbe4f52-cac2-493b-a1ef-d4dfeb20e750)
![4](https://github.com/JeongJonggil/TIL/assets/139416006/ea975e3a-c7e0-46f4-9c96-e1effa55d715)
![5](https://github.com/JeongJonggil/TIL/assets/139416006/18c30fd8-3fdc-4dbd-9230-7bce262d6796)

### 6. 모듈과 번들러

- **Module**
  - 프로그램을 구성하는 독립적인 코드 블록 (*.js 파일)
  - 개발하는 애플리케이션의 크기가 커지고 복잡해지면서 파일 하나에 모든 기능을 담기가 어려워 짐
  - 따라서 자연스럽게 파일을 여러개로 분리하여 관리를 하게 되었고, 이때 분리된 파일 각각이 모듈(module) 즉, js 파일 하나가 하나의 모듈
- **Bundler**
  - 여러 모듈과 파일을 하나(혹은 여러 개)의 번들로 묶어 최적화하여 애플리케이션에서 사용할 수 있게 만들어주는 도구
  - Bundler가 하는 작업을 Bundling이라 함

### 7. Component 활용

- 컴포넌트 파일 생성 후 등록(import)하여 사용

- import 시 경로에 /src/ 부분은 @로 사용가능

  - ```vue
    <template>
        <div>
            <h2>MyComponent</h2>
            <MyComponentItem/>
            <MyComponentItem/>
            <MyComponentItem/>
        </div>
    </template>
    
    <script setup>
    import MyComponentItem from '@/components/MyComponentItem.vue';
    </script>
    
    <style  scoped>
    </style>
    ```

### 8. Virtual Dom

- 가상의 DOM을 메모리에 저장하고 실제 DOM과 동기화하는 프로그래밍 개념
- 실제 DOM과의 변경 사항 비교를 통해 변경된 부분만 실제 DOM에 적용하는 방식
- 웹 애플리케이션의 성능을 향상시키기 위한 Vue의 내부 렌더링 기술

- Virtual DOM 주의사항
  - 실제 DOM에 직접 접근하지 말 것
    - 자바스크립트에서 사용하는 DOM 접근 관련 메서드 사용 금지
    - querySelector, createElement, addEventListner 등
    - Vue의 ref와 Lifecycle Hooks 함수를 사용해 간접적으로 접근하여 조작할 것
