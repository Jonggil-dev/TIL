# Controlling event

### 1. 개요

- 웹에서의 이벤트
  - 버튼을 클릭했을 때 팝업 창이 출력되는 것
  - 마우스 커서의 위치에 따라 드로그 앤 드롭하는 것
  - 사용자의 키보드 입력 값에 따라 새로운 요소를 생성하는 것

### 2. event

- 무언가  일어났다는 신호,사건
- 모든 DOM 요소는 이러한 event를 만들어냄

### 3. event object

- DOM에서 이벤트가 발생했을 때 생성되는 객체
- DOM 요소는 evnet를 받고 받은 event를 '처리(event handler)'할 수 있음

### 4. event handler (.addEventListener)

- 이벤트가 발생했을 때 실행되는 함수

- 사용자의 행동에 어떻게 반응할지를 JavaScript 코드로 표현한 것

- **요소**.addEventListener(**type**, **handler**)

  - 대표적인 이벤트 핸들러 중 하나
  - **매개변수인 콜백함수(handler)는 this 동작 때문에 화살표 함수로 선언하지 말기**
  - 특정 이벤트를  DOM 요소가 수신할 때마다 콜백 함수를 호출
  
  ```javascript
  EventTarget.addEventListener(type,handler)
  // EventTarget : DOM 요소(대상)
  // type : 수신할 이벤트(특정 Event)
  // handler : 콜백 함수(할 일)
  
  "대상에 특정 Event가 발생하면 할 일을 등록한다"
  ```

![1](https://github.com/JeongJonggil/TIL/assets/139416006/32c05281-1d3f-4d20-ae61-8a49ab49a7bb)
![2](https://github.com/JeongJonggil/TIL/assets/139416006/5e2b8be5-a7cb-4e1c-b1de-5e03349a0c79)
![3](https://github.com/JeongJonggil/TIL/assets/139416006/7cc1ad09-429e-4347-a283-b41454a661bc)


### 5. 버블링 ( <-> 캡쳐링과 반대, 캡처링 : 위에서부터 내려오는 것)

- 한 요소에 이벤트가 발생하면, 이 요소에 할당된 핸들러가 동작하고, 이어서 부모 요소의 핸들러가 동작하는 현상
- 가장 최상단의 조상 요소(document)를 만날 때까지 이 과정이 반복되면서 요소 각각에 할당된 핸들러가 동작
- 이벤트가 제일 깊은 곳에 있는 요소에서 시작해 부모 요소를 거슬러 올라가며 발생하는 것이 마치 물속 거품과 닮았기 때문
- **event.stopPropagation() 메서드를 통해 버블링 방지 가능** 
- 이벤트가 정확히 어디서 발생했는지 접근할 수 있는 방법
  - event.target 속성
    - 이벤트가 발생한 가장 안쪽의 요소(target)를 참조하는 속성
    - **실제 이벤트가 시작된 target 요소**
    - 버블링이 진행 되어도 변하지 않음
  - event.currentTarget 속성
    - '현재' 처리 중인 이벤트 핸들러가 연결된 요소
    - 항상 이벤트 핸들러가 연결된 요소만을 참조하는 속성
    - 'this'와 같음  
      
  
  ![4](https://github.com/JeongJonggil/TIL/assets/139416006/b1e3e9bd-4b2f-40ba-838f-18b622c5bd82)

### 6.이벤트 기본 동작 취소(preventDefault)

- .preventDefault() : 해당 이벤트에 대한 기본 동작을 실행하지 않도록 지정



### 7. 참고(addEventListener에서의 화살표 함수 주의사항)

- 화살표 함수는 자신만의 this를 가지지 않기 때문에 자신을 포함하고 있는 함수의 this를 상속받음

