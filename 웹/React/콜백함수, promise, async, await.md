# 콜백함수/ promise / async, await

### 1. 콜백함수

- 다른 함수에 인자로 전달되는 함수

- **함수를 등록하기만 하고 어떤 이벤트가 발생했거나 특정 시점에 도달했을 때 시스템에서 호출하는 함수**

  - ```javascript
    //ex
    
    function fetchData(register) {
      setTimeout(() => {
        const data = '데이터';
        register();
      }, 1000);
    }
    
    function testcallback() {
    	 console.log("받은 데이터:", receivedData);
    }
    
    fetchData(callback);
    
    //위 testcallback() 함수는 처음 fetchData 함수에 인자(register)로서 먼저 등록 되었다가 const data = '데이터';  다음 줄에서 호출되어 실행됨
    ```

- 주로 비동기 작업을 다룰때 사용

  - **A라는 비동기 작업이 있는데 A 비동기 작업이 끝난 뒤에 행해져야 함을 보장받고자 할 때 사용된다는 의미**

### 2. promise

- `Promise`: 자바스크립트에서 비동기 작업의 진행 상태인 **대기 or 완료 or  실패**를 나타내는 객체. 

- `Promise`는 다음 세 가지 상태 중 하나를 가짐

  - `promise`가 반환되었다고 표현되는 상태 1가지
    - **Pending(대기)**: 비동기 작업이 아직 완료되지 않은 상태
  - `promise`가 해결되었다고 표현되는 상태 2가지
    - **Fulfilled(이행됨)**: 비동기 작업이 성공적으로 완료된 상태
    - **Rejected(거부됨)**: 비동기 작업이 실패한 상태.

- 사용법

  - ```javascript
    const myPromise = new Promise((resolve, reject) => {
      // 비동기 작업을 여기에서 수행
      if (/* 성공 조건 */) {
        resolve(value); // 작업 성공 시
      } else {
        reject(error); // 작업 실패 시
      }
    });
    
    // resolve(value): 비동기 작업이 성공적으로 완료되었을 때 호출. 이 함수를 호출하면 Promise는 Fulfilled 상태가 되며, value는 Promise의 결과 값이 됨.
    // reject(error): 비동기 작업이 실패했을 때 호출. 이 함수를 호출하면 Promise는 Rejected 상태가 되며, error는 오류의 원인을 나타냄.
    
    myPromise.then(result => {
      console.log(result); // 성공 결과 처리
    }).catch(error => {
      console.error(error); // 오류 처리
    });
    
    //.then(): Promise가 성공적으로 완료되었을 때 실행. 이 메소드는 Fulfilled 상태의 Promise의 결과 값을 인자로 받는 콜백 함수를 등록
    //.catch(): Promise가 실패했을 때 실행. 이 메소드는 Rejected 상태의 Promise의 오류를 인자로 받는 콜백 함수를 등록
    
    myPromise.finally(() => {
      console.log('Promise 처리 완료');
    });
    
    //.finally(): Promise의 성공 여부와 관계없이 실행. 이 메소드는 Promise 처리가 완료된 후에 실행되어야 할 로직을 정의.
    
    ```

### 3. async, await

- `async`

  - 함수 선언에 `async`를 붙임으로써, 그 함수는 **'비동기 함수'**가 됨 

  - **'비동기 함수가 된다'**는 의미는 `async`가 붙은 함수의 작업이 완료되지 않아도 다음줄에 작성된 코드가 실행된다는 뜻.  즉, 함수 내부에서 일어나는 작업의 완료를 기다리지 않고, 함수 외부의 다음 코드로 진행.

  - **`async`가 붙은 함수를 호출하면 해당 함수에 대한 `promise`를 pending 상태로 생성함 (반환한다라고 표현) 
     -> 이 때 생성된 `promise`는 async 함수 내부에 적힌 모든 작업이 완료될 때 까지 pending 상태를 유지함
     -> 다른 말로,  함수 내부의 return문을 만나거나 끝이 나면 해결(Fulfilled)상태가 됨**
  - `async` 함수 내에서 return에 일반 값(value)을 적으면, 자바스크립트는 그 값을 `Promise`로 감싸서 반환함. 즉, `async` 함수는 무조건 `Promise`를 반환하게 됨.


- `await`
  - `await` 로 선언된 함수는 주로 비동기 작업을 나타냄 (동기 작업을 await로 선언 해도 되는데 그럴 꺼면 굳이 await를 쓰는 이유가 없음)
  - `await`는 await로 선언된 함수의 `promise` 해결을 기다림 (해당`promise`가 해결될 때 까지 함수 내부의 다음 줄로 안넘어감)
    - **다른 말로 하면 `await`로 선언된 함수는 promise를 반환해야 의미가 있음**
    - useState의 상태변경 set함수의 경우는 await를 반환하지 않으므로 await의 기술적인 의미가 없음
    - **즉, 하나의 함수에서 set으로 특정 변수의 값을 변경하고 변경된 값을 사용하기 위해 set함수에 await를 거는 방법은 의미가 없음**


### 4.  (예시) 비동기 함수가 아닌 일반 함수와 `async`를 사용한 비동기 함수의 차이


- **비동기 함수가 아닌 일반 함수**

```javascript
function fetchData() {
  let data = "데이터 로딩 중...";
  // 데이터 로딩 시뮬레이션 (동기적)
  data = "데이터 로딩 완료";
  return data;
}

console.log(fetchData()); // "데이터 로딩 완료"
console.log("이 코드는 데이터 로딩 후 실행됩니다.");
```

1. 이 경우, `fetchData` 함수는 동기적으로 작동함. 즉, 함수 내의 작업이 완료되어야 다음 줄의 코드로 넘어감. 

2. "데이터 로딩 중..."이 할당된 후, "데이터 로딩 완료"로 변경되고, 그 값을 반환

3. 함수가 종료된 후에 다음 줄의 `console.log("이 코드는 데이터 로딩 후 실행됩니다.")`가 실행

- `async`를 사용한 비동기 함수

```javascript
async function fetchData() {
  let data = "데이터 로딩 중...";
  // 데이터 로딩 시뮬레이션 (비동기적)
  data = await new Promise((resolve) => setTimeout(() => resolve("데이터 로딩 완료"), 2000));
  return data;
}

fetchData().then(data => console.log(data)); // 2초 후 "데이터 로딩 완료"
console.log("test");
```

- **큰 동작 과정**

  1.  **`fetchData()` 함수 호출**

  2. **`console.log("test")`코드 실행**
     - `fetchData` 함수는 async가 적용되어 비동기 함수이므로, 프로그램은 다음 줄로 넘어가 이 `console.log`를 즉시 실행합니다. 이 때 `fetchData` 함수 내부의 비동기 작업은 아직 진행 중.
  3. **fetchData() 뒤의 .then(data => console.log(data)) 실행**

- **세부 동작 과정**
  1.  **`fetchData` 함수 호출**: 이 함수는 `async` 함수이므로, 호출되면 즉시 **첫 번째`Promise` 객체**를 반환합니다. 이 `Promise`는 `fetchData` 함수의 내부 비동기 작업이 완료될 때까지 `Pending` 상태를 유지합니다.
  2. **`console.log("test");`**: 이 코드는 `fetchData` 함수의 `Promise` 반환 후 바로 실행됩니다. 이는 `fetchData` 함수가 비동기적으로 작동하기 때문에 가능합니다. 함수의 비동기 작업의 완료를 기다리지 않고 바로 다음 코드로 넘어갑니다.
  3. **비동기 작업 실행**: `fetchData` 함수 내부에서 `await new Promise(...)` 구문이 실행됩니다. 여기서 `new Promise`는 비동기 작업(여기서는 `setTimeout`)을 위한 **두 번째 `Promise` 객체**를 생성합니다.
  4. **`setTimeout`의 `resolve` 호출**: `setTimeout`은 2초 후에 `resolve` 함수를 호출합니다. 이 때 `resolve("데이터 로딩 완료")`를 실행하여, 두 번째 `Promise` 객체는 `"데이터 로딩 완료"`라는 값을 가진 `Fulfilled` 상태가 됩니다.
  5. **`await`의 역할**: `await`는 두 번째 `Promise` 객체가 `Fulfilled` 상태가 될 때까지 기다립니다. 즉, 2초를 기다린 후, `data` 변수에 `"데이터 로딩 완료"`라는 값이 할당됩니다.
  6. **`return data;`**: `fetchData` 함수는 `data` 변수의 값을 반환합니다. `async` 함수의 특성상, 이 반환값은 첫 번째 `Promise` 객체의 결과 값이 됩니다. 즉, 첫 번째 `Promise`는 `"데이터 로딩 완료"`라는 값을 가진 `Fulfilled` 상태가 됩니다.
  7. **.then(data => console.log(data));**: `fetchData().then(...)` 구문은 첫 번째 `Promise`의 결과를 기다립니다. 첫 번째 `Promise`가 `Fulfilled` 상태가 되면, `.then` 안의 콜백 함수가 실행되어 `"데이터 로딩 완료"`가 콘솔에 출력됩니다.

