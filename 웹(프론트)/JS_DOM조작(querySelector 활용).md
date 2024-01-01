# Introduction of JavaScript

### 1. JavaScript 개요

- ECMAScript
  - Ecma International(정보와 통신 시스템을 위한 국제적 표준화 기구)이 정의하고 있는 표준화된 스크립트 프로그래밍 언어 명세
  - 스크립트 언어가 준수해야 하는 규칙, 세부사항 등을 제공
- ECMAScript와 JavaScript
  - JavaScript는 ECMAScript 표준을 구현한 구체적인 프로그래밍 언어
  - ECMAScript의 명세를 기반으로 하여 웹 브라우저나 Node,.js와 같은 환경에서 실행됨
  - ECMAScript는 JavaScript의 표준이며, JavaScript는 ECMAScript 표준을 따르는 구체적인 프로그래밍 언어
  - ECMAScript는 언어의 핵심을 정의하고, JavaScript는 ECMAScript 표준을 따라 구현된 언어로 사용됨
- JavaScript의 현재
  - 현재는 Chrome,Firefox,Safari 등 다양한 웹 브라우저가 출시되어 있으며, 웹 브라우저 시장이 다양화 되어있음
  - 기존에 JavaScript는 브라우저에서만 웹 페이지의 동적인 기능을 구현하는 데에만 사용되었음
    - 예를들어, 사용자의 입력에 따라 웹 페이지의 내용이 동적으로 변경되거나, 애니메이션 효과가 적용되는 등의 기능
  - 이후 브라우저에서 벗어나 Node.js와 같은 서버 사이드 분야 뿐만 아니라, 다양한 프레임워크와 라이브러리들이 개발되면서, 웹 개발 분야에서는 필수적인 언어로 자리 잡게 됨


### 2. JavaScript and DOM

- 웹 브라우저에서의 JavaScript

  - 웹 페이지의 동적인 기능을 구현
  - HTML script 태그, js 확장자 파일, 브라우저 Console 3가지 방법(환경)으로 실행이 가능함

- **DOM(The Document Object Model)**

  - 웹 페이지(Document)를 구조화된 객체로 제공하여 프로그래밍 언어가 페이지 구조에 접근할 수 있는 방법을 제공

  - 문서 구조, 스타일, 내용 등을 변경할 수 있도록 함

  - **DOM 핵심은 문서의 요소들을 객체로 제공하여 다른 프로그래밍 언어에서 접근하고 조작할 수 있는 방법을 제공하는 API**

    > 예를 들어, JavaScript로 h1 태그를 빨간색으로 변경하는 동작을 수행하기 위해서는사용하려면 h1태그가 선택이 되야됨. db에서 db를 수정할 때 인스턴스를 선택(조회)후 수정하는 것처럼, JavaScript는 그냥 하나의 웹 페이지의 요소들을 선택(조회) 할 수 있어야됨. 이 때 이러한 행위를 가능하도록 웹 브라우저에서 DOM(웹 페이지를 구조화한 객체(Model))을 제공함

![1](https://github.com/JeongJonggil/TIL/assets/139416006/7c016132-d611-40da-a6af-7b134ff4839c)
![2](https://github.com/JeongJonggil/TIL/assets/139416006/20435093-2866-4110-a689-060c88cc61c5)
![3](https://github.com/JeongJonggil/TIL/assets/139416006/27dfda8f-8200-4b77-9835-734d38503f92)


- 'document'객체

  - 웹 페이지 객체
  - DOM Tree의 진입점
  - 페이지를 구성하는 모든 객체 요소를 포함

![4](https://github.com/JeongJonggil/TIL/assets/139416006/e2e7e2f0-e4d4-4151-8ea7-207efe4ed09c)

### 3. DOM 조작 시 기억해야 할 것

- 웹페이지를 동적으로 만들기 == 웹페이지를 조작하기

- 조작순서

  (1) 조작 하고자 하는 요소를 선택 (또는 탐색)

  (2) 선택된 요소의 콘텐츠 또는 속성을 조작

### 4. DOM 선택

- **document.querySelector(selector), 요소 한 개 선택**
  - 제공한 선택자와 일치하는 element 한 개 선택
  - 제공한 CSS selector를 만족하는 첫 번째 element 객체를 반환 (없다면 null 반환)
- **document.querySelectorAll(selector), 요소 여러 개 선택**
  - 제공한 선택자와 일치하는 여러 element를 선택
  - 제공한 CSS selector를 만족하는 NodeList를 반환
- **참고 : 복잡한 곳에 위치한 태그의 클래스를 선택하고자 할 때**는, 웹브라우저 → 개발자도구 → 해당 요소 클릭 → Elements → 해당 태그 우클릭 → Copy → Copy Selector 이후 VS code에 붙여넣기하면 해당 태그의 선택자가 출력됨 → 문자열로 만들어서('' 붙여서) 사용하기

![5](https://github.com/JeongJonggil/TIL/assets/139416006/1ee217aa-d524-4378-9b8b-542d9c8a61e2)


### 5. DOM 조작

- Class 속성 조작(classList 메서드)
  - 요소의 클래스 목록을 DOMTokenList(유사 배열) 형태로 반환
 
![6](https://github.com/JeongJonggil/TIL/assets/139416006/871a84f5-5450-4797-a3a7-5b901a9fcb33)
![7](https://github.com/JeongJonggil/TIL/assets/139416006/70510ad4-0e67-44b9-930f-64c8729304d3)


- 일반 속성 조작(일반 속성 : a태그의 href 같은 것들 처럼 태그에 있는 속성 값들 )

![8](https://github.com/JeongJonggil/TIL/assets/139416006/2a4a999a-4df5-40de-8540-4b71fe94a26d)
![9](https://github.com/JeongJonggil/TIL/assets/139416006/00b22815-733d-4ecf-99ee-82d5d1af220e)

- HTML 콘텐츠 조작 (textContent)
  - 요소의 텍스트 콘텐츠를 표현 (태그 사이의 content 부분)

![10](https://github.com/JeongJonggil/TIL/assets/139416006/019896e7-ffd8-4b6c-abfb-e2b5aaab0a48)

- DOM 요소 조작

  - 아래 이미지는 : 요소 생성 -> 부모 요소 선택 -> 요소 추가 -> 요소 삭제 하는 순서임
 
![11](https://github.com/JeongJonggil/TIL/assets/139416006/75df5702-00d8-4cfd-b04a-78513bb7f847)
![12](https://github.com/JeongJonggil/TIL/assets/139416006/ca7c2e11-47e4-4fd1-bd1b-146d95968d8d)


- Style 조작(style 메서드)

  - 해당 요소의 모든 style 속성 목록을 포함하는 속성
  - 위의 Class는 Class를 조작하는 거고, Style 조작은 In-line 스타일을 조작한다고 생각하면 됨
  - 보통은 CSS파일의 Class에 속성을 설정해서 Class로 사용하는게 맞는데, 한 번씩 우선순위에 따라 in-line속성 을 사용해야 할 때만 사용하기

![13](https://github.com/JeongJonggil/TIL/assets/139416006/09b7dda7-8961-468d-bd8e-9c0ba9399e40)


### 6. 참고(Node,NodeList)

![14](https://github.com/JeongJonggil/TIL/assets/139416006/8b3e4c68-4c3c-4dec-840f-57a293e55237)
![15](https://github.com/JeongJonggil/TIL/assets/139416006/8a175aae-85a0-4af0-8228-315863052f45)
![16](https://github.com/JeongJonggil/TIL/assets/139416006/39460023-a15f-416f-b6ec-dfb84a77885e)
![17](https://github.com/JeongJonggil/TIL/assets/139416006/e9fcf289-003f-4483-82e4-a807b2efd866)
![18](https://github.com/JeongJonggil/TIL/assets/139416006/d48e19d4-4e42-4375-b6ef-3ce62f8918ac)
