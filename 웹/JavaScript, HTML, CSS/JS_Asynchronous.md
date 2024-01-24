# Asynchronous JavaScript

### 1. 비동기 (Synchoronous 가 아닌 것)

- Synchoronous(동기) 
  - 프로그램의 실행 흐름이 순차적으로 진행
  - 하나의 작업이 완료되 후에 다음 작업이 실행되는 방식
  - **꼭 기억해야 될 특징 : 순서가 보장이 된다**

![1](https://github.com/JeongJonggil/TIL/assets/139416006/5f5eed81-f1f4-4a0d-9cd9-99dbf31434a0)

- Asynchronous(비동기)
  - 프로그램의 실행 흐름이 순차적이지 않으며, 작업이 완료되기를 기다리지 않고 다음 작업이 실행되는 방식
  - 작업의 완려 여부를 신경 쓰지 않고 동시에 다른 작업들을 수행할 수 있음
  - **동시에 코드를 처리하는게 아님.** 순서를 바꿔서 코드를 처리 하는 거임
  
  - 특징
    - 병렬적 수행
    - 당장 처리를 완료할 수 없고 시간이 필요한 작업들은 별도로 요청을 보낸 뒤 응답이 빨리 오는 작업부터 처리
  - 예시
    - Gmail에서 메일 전송을 누르면 목록 화면으로 전환되지만 실제로 메일을 보내는 작업은 병렬적으로 별도로 처리됨
    - 브라우저는 웹 페이지를 먼저 처리되는 요소부터 그려 나가며 처리가 오래 걸리는 것들은 별도로 처리가 완료 되는대로 병렬적으로 처리

### 2. 자바스크립트와 비동기

- 자바스크립트는 Single Thread 언어
  - Thread : 작업을 처리할 때 실제로 작업을 수행하는 주체로, multi-thread라면 업무를 수행할 수 있는 주체가 여러 개라는 의미
  - 즉, 자바스크립트는 하나의 작업을 요청한 순서대로 처리할 수 밖에 없음
- **자바스크립트 Runtime**
  - 자바스크립트가 동작할 수 있는 환경(Runtime)
  - **자바스크립트 자체는 Single Thread이므로 비동기 처리를 할 수 있도록 도와주는 환경이 필요**
  - 자바스크립트에서 비동기와 관련한 작업은 "브라우저" 또는 "Node"와 같은 환경에서 처리
- 브라우저 환경에서의 자바스크립트 비동기 처리 관련 요소
  - Call Stack (자바스크립트의 Engine)
    - 요청이 들어올 때 마다 순차적으로 처리하는 Stack(LIFO)
    - 기본적인 자바스크립트의 Single Thread 작업 처리
  - Web API
    - 자바스크립트 엔진이 아닌 브라우저에서 제공하는 runtime 환경
    - 시간이 소요되는 작업을 처리 (setTimeout, DOM Event, AJAX 요청 등)
  - Task Queue
    - 비동기 처리된 Callback 함수가 대기하는 Queue(FIFO)
  - Event Loop
    - 태스크(작업)가 들어오길 기다렸다가 태스크가 들어오면 이를 처리하고, 처리할 태스크가 없는 경우엔 잠드는, 끊임없이 돌아가는 자바스크립트 내 루프
    - Call Stack과 Task Queue를 지속적으로 모니터링
    - Call Stack이 비어 있는지 확인 후 비어 있다면 Task Queue에서 대기중인 오래된 작업을 Call Stack으로 Push
- **정리**
  - 자바스크립트는 한 번에 하나의 작업을 수행하는 Single Thread 언어로 동기적 처리를 진행
  - 하지만 브라우저 환경에서는 Web API에서 처리된 작업이 지속적으로 Task Queue를 거쳐 Event Loop에 의해 Call Stack에 들어와 순차적으로 실행됨으로써 비동기 작업이 가능한 환경이 됨



### 3. 브라우저 환경에서의 자바스크립트 비동기 처리 동작 방식

- 모든 작업은 **Call Stack**(LIFO)으로 들어간 후 처리된다. (Call Stack에 들어갔다가 호출되면서(=나오면서) 코드의 동작이 처리됨)
- 오래 걸리는 작업이 Call Stack으로 들어오면 **Web API**로 보내 별도로 처리하도록 한다.
- Web API에서 처리가 끝난 작업들은 곧바로 Call Stack으로 들어가지 못하고 **Task Queue(FIFO)**에 순서대로 들어간다.
- **Event Loop**가 Call Stack이 비어 있는 것을 계속 체크하고 Call Stack이 빈다면 Task Queue에서 가장 오래된(가장 먼저 처리되어 들어온) 작업을 Call Stack으로 보낸다.

> ※참고1 : 코드들이 동기적으로 실행될 때 콜 스택에 차례대로 들어갔다 나오게 됨. 하지만, 이 동안 콜 스택은 실제로 "비어 있지 않음". 즉, **하나의 동기적 작업이 끝나고 다른 동기적 작업이 시작되기까지의 사이에는 극히 짧은 시간이지만 콜 스택은 항상 무언가를 처리하고 있는 상태**이기 때문. → Task Queue에 대기중인 작업이 있다고 가정했을 때, x 작업이 Call Stack에서 나오고  y 작업이 Call Stack에 들오기 전인 Call Stack이 잠깐 비는 순간에 Task Queue에 있던 작업이 Call Stack으로 올라가지 않을까? 싶었는데 **결론은 Call Stack은 동기적 코드들의 실행이 완전히 끝날 때까지 비는 순간이 없음.**
>
> ※참고2 : setTimeout 등 비동기 함수에서 설정한 시간은 해당 시간이 지난후에 동작이 실행된다는 뜻이 아니고 Task Queue에 들어간다는게 정확한 의미임

![1](https://github.com/JeongJonggil/TIL/assets/139416006/6f736ae3-b62b-4ddf-af2d-4e4dcde5c4ad)
![2](https://github.com/JeongJonggil/TIL/assets/139416006/888df333-931b-4198-9a2a-92b221837af8)
![3](https://github.com/JeongJonggil/TIL/assets/139416006/fd8228c8-cb71-4677-9dca-8d60de96b3cd)
![4](https://github.com/JeongJonggil/TIL/assets/139416006/b8166e3c-b8d2-4e1c-a08f-c5ae102f8ecf)
![5](https://github.com/JeongJonggil/TIL/assets/139416006/67ceebef-f69c-4820-9f45-80d8ef43e660)
![6](https://github.com/JeongJonggil/TIL/assets/139416006/e025b049-5e79-47f2-9c3a-645540a05d14)
![7](https://github.com/JeongJonggil/TIL/assets/139416006/1fbdf6f0-4bc9-46d4-bfb3-3f674e6ac669)
![8](https://github.com/JeongJonggil/TIL/assets/139416006/41db3a78-a01a-41af-be98-6b8f3c963201)
![9](https://github.com/JeongJonggil/TIL/assets/139416006/54587fe4-e779-40a3-ba81-a9589a32f0c0)
![10](https://github.com/JeongJonggil/TIL/assets/139416006/1aec10ae-b047-4c53-8541-ce8841a54c9c)
![11](https://github.com/JeongJonggil/TIL/assets/139416006/3181da56-ae9a-4419-8d4a-0ed4e6c180ae)
![12](https://github.com/JeongJonggil/TIL/assets/139416006/77b45068-f8c0-40cc-b2af-ed77864b203e)
![13](https://github.com/JeongJonggil/TIL/assets/139416006/276dc563-3dca-4a68-af4b-3c57e348cd08)

### 4. AJAX (Asynchronous JavaScript + XML)

- **자바스크립트의 비동기 구조와 XML 객체를 활용해 비동기적으로 서버와 통신하여 웹 페이지의 일부분만을 업데이트하는 웹 개발 기술**
- 'X'가 XML을 의미하긴 하지만 요즘은 더 가벼운 용량과 자바스크립트의 일부라는 장점 때문에 'JSON'을 더 많이 사용

- ##### XML HttpRequest 객체 (XHR 객체)

  - 서버와 상호작용할 때 사용하며 페이지의 새로고침 없이도 URL에서 데이터를 가져올 수 잇음


  - 사용자의 작업을 방해하지 않고 페이지의 일부를 업데이트

  - 주로 AJAX 프로그래밍에 많이 사용됨


- ##### 이벤트 핸들러는 비동기 프로그래밍의 한 형태

  - 이벤트가 발생할 때마다 호출되는 함수(콜백 함수)를 제공하는 것

  - XMLHttpRequest(XHR)는 자바스크립트를 사용하여 서버에 HTTP 요청을 할 수 있는 객체

  - HTTP 요청은 응답이 올 때 까지의 시간이 걸릴 수 있는 작업이라 비동기 API이며, 이벤트 핸들러를 XHR 객체에 연결해 요청의 진행 상태 및 최종 완료에 대한 응답을 받음


### 5. Axious

- JavaScript에서 사용되는 **Promise 기반 HTTP 클라이언트 라이브러리**
- 서버와의 HTTP 요청과 응답을 간편하게 처리할 수 있도록 도와주는 도구

- ##### Axious 설치

  - Axious 공식 문서 CDN 사용

- ##### Axious 구조

  - get, post 등 여러 http request method 사용가능

  - **then** 메서드를 사용해서 "성공하면 수행할 로직"을 작성

    - axios로 요청을 보내면 요청에 대한 응답은 then의 인자로 들어옴

    ```javascript
    //버튼을 누르면 랜덤 고양이 사진 가져오기 실습 코드
     
    // 가독성을 위해 .then과 .catch는 줄바꿈 한 번 하고 사용 권장
    // then인자에 있는 response가 axious 요청에 대한 응답 객체임
    // .catch는 위쪽 코드의 동작에서 에러가 발생하면 넘어가게 되는거임
    
    <script src ="Axious CDN주소">
    const URL = '~~~'
    const btn = document.querySelector('button')
    const getCats = function() {
        })
      .then((response) => {
        const imgUrl = response.data[0].url
        const imgTag = document.CreateElement('img')  //img 태그 만들기
        imgTag.setAttribute('src', imgUrl) // img 태그에 src 속성 값 설정
        document.body.appendChild(imgTag) //body 태그에 imgTag 자식으로 추가하기    
      })
      .catch((error) => {
        console.log(error)
    })
    }
    
    btn.addEventListener('click',getCats)
    
    axios({
    	method : 'get',
    	url : URL
    
    
    ```

  - **catch** 메서드를 사용해서 "실패하면 수행할 로직"을 작성 

- #### 정리

  - axios는 브라우저에서 비동기로 데이터 통신을 가능하게 하는 라이브러리
    - 브라우저를 위해 XMLHttpRequest 생성
  - 같은 방식으로 DRF로 만든 API 서버로 요청을 보내서 데이터를 받아온 후 처리할 수 있도록 함

### 6. Callback과 Promise

- 비동기 처리의 단점
  - 비동기 처리의 핵심은 Web API로 들어오는 순서가 아니라 **작업이 완료되는 순서에 따라 처리**한다는 것
  - 그런데 이는 개발자 입장에서 **코드의 실행 순서가 불명확**하다는 단점 존재
  - 이와 같은 단점은 실행 결과를 예상하면서 코드를 작성할 수 없게 함
  - 콜백 함수를 사용하여 개선하자!

- 비동기 콜백
  - 비동기적으로 처리되는 작업이 완료되었을 때 실행하는 함수
  - 연쇄적으로 발생하는 비동기 작업을 콜백함수안에 콜백함수를 넣어 **순차적으로 동작**할 수 있게 함
    - 쉽게 함수안에 함수를 인자로 받는 구조니까, 바깥 함수의 실행이 완료되어야 내부 함수가 실행된다 라고 생각하면 조금 이해가 됨
  - 사용하다보면 콜백 지옥으로 인해 유지보수가 어려워짐 → 다른 표기 형태 필요

### 7. Promise

- 콜백 지옥 문제를 해결하기 위해 등장한 비동기 처리를 위한 객체
- **"작업이 끝나면 실행 시켜 줄게"라는 약속(promise)**
- **비동기 작업의 완료 또는 실패를 나타내는 객체**
- Promise 기반의 클라이언트가 바로 이전에 사용한 Axios 라이브러리
  - 성공에 대한 약속 then(callback)
    - 요청한 작업이 성공하면 callback 실행
    - **callback은 이전 작업의 성공 결과를 인자로 전달 받음**
    - **then을 이어가려면 return으로 값을 반환해야 됨**
  - 실패에 대한 약속 catch(callback)
    - then()이 하나라도 실패하면 callback 실행
    - callback은 이전 작업의 실패 객체를 인자로 전달 받음
    
![1](https://github.com/JeongJonggil/TIL/assets/139416006/a3697652-45c6-4b30-a0f3-6265037c0b51)
![2](https://github.com/JeongJonggil/TIL/assets/139416006/644868a4-1fa1-4804-92bf-8f31c894673f)
![3](https://github.com/JeongJonggil/TIL/assets/139416006/be32e8e1-ed75-41a4-b559-27af61215b21)

### 8. async - await (교수님 수업)

- **promise 기반의 비동기 작업을 다루기 위한 가장 최신 기술**
- **async**:
  - 비동기로 처리할 함수 앞에 `async`를 붙이면 해당 함수는 항상 프로미스(Promise)를 반환.
  - 심지어 그 함수가 프로미스가 아닌 값을 반환해도 자바스크립트 엔진이 그 값을 프로미스로 감싸서 반환함
- **await**
  -  `await` 키워드는 `async` 함수 내에서만 사용 가능.
  -  `await` 다음에 오는 표현식의 결과(프로미스)가 나올 때까지 기다림.
  -  즉, 해당 프로미스가 해결(resolve)되거나 거부(reject)될 때까지 함수의 실행을 일시적으로 중단.
