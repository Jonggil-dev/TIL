# JavaScript Reference data types

### 1. 함수

- Function : 참조 자료형에 속하며 모든 함수는 function object

- 함수 구조

  - return값이 없다면 undefined를 반환

  ```javascript
  function name([param[,param[,...]]]){
  	return value
  }
  ```

- 함수 정의 2가지 방법 **`→ 하기 두가지 보다 화살표 함수 사용권장`**

  - 선언식(function declartion)

    ```javascript
    function funcName(){
    	statements
    }
    
    //예시
    function add(num1,num2){
        return num1+num2
    }
    
    add(1,2) // 3
    ```

  - **표현식(function expression)**

    ```javascript
    const funcName = function(){
    	statements
    }
    
    //예시 -> sub라는 변수명에 익명함수 function을 할당한 느낌
    const sub = function(num1.num2){
        return num1 - num2
    }
    
    sub(2,1) // 1
    ```

- 함수 **표현식** 특징

  - 함수 이름이 없는 **'익명 함수 (함수 생성 시 명시적인 이름 없이 function()으로 생성되는 함수)'**를 사용할 수 있음

  - **선언식과 달리 표현식으로 정의한 함수는 호이스팅 되지 않으므로 함수를 정의하기 전에 먼저 사용할 수 없음** → 표현식으로 정의된 함수는 호이스팅 시 변수 선언문만 최상단으로 올라감

  - ※ 함수 호이스팅은 함수를 작성한 코드 블록부분 전체가 최상단으로 올라감 (선언문만 최상단으로 올라가는 변수 호이스팅과 다름)

  - 함수 선언식과 표현식 종합
  
    |      |                    선언식                    |                  표현식                  |
    | :--: | :------------------------------------------: | :--------------------------------------: |
    | 특징 | - 익명 함수 사용 불가능<br />- 호이스팅 있음 | - 익명함수 사용가능<br />- 호이스팅 없음 |
    | 기타 |                                              |              **사용 권장**               |



### 2. 매개변수

- 매개변수 정의 방법

  - 기본 함수 매개변수(Default function parameter)
    - 값이 없거나 undefined가 전달될 경우 이름 붙은 매개변수를 기본값으로 초기화

  ```javascript
  const greeting = function (name = 'Anonymous'){
  	return 'Hi ${name}'
  }
  
  greeting() // Hi Anonymous
  ```

  - 나머지 매개변수(Rest parameters)
    - 임의의 수의 인자를 '배열'로 허용하여 가변 인자를 나타내는 방법
    - 작성규칙
      - 함수 정의 시 나머지 매개변수 하나만 작성할 수 있음
      - 나머지 매개변수는 함수 정의에서 매개변수 마지막에 위치해야 함

  ```javascript
  const myFunc = function(param1, param2, ../ restParams){
  	return [param1, param2, restParams]
  }
  
  myFunc(1,2,3,4,5) // [1,2,[3,4,5]]
  myFunc(1,2)//[1,2,[]]
  ```

  - 매개변수와 인자의 개수 불일치 
    - **매개변수 개수 > 인자 개수 : 누락된 인자는 undefined로 할당**

  ```javascript
  const threeArgs = function(param1, param2, param3){
  	return [param1, param2, param3]
  }
  threeArgs() // [undefined,undefined,undefined]
  threeArgs(1) // [1, undefined, undefined]
  threeArgs(2,3) // [2,3,undefined]
  ```
  
  - 매개변수와 인자의 개수 불일치 
    - **매개변수 개수 < 인자 개수 : 초과 입력한 인자는 사용하지 않음**
  
  ```
  const noArgs = function (){
  	return 0
  }
  noArgs(1, 2, 3) // 0
  
  const twoArgs = function (param1,param2){
  	return [param1, param2]
  }
  twoArgs(1,2,3) // [1,2]
  ```



### 3. Spread syntax(전개 구문, '...')


![1](https://github.com/JeongJonggil/TIL/assets/139416006/151afc21-03d8-4d7e-94c2-6d15ffb6ec25)
![2](https://github.com/JeongJonggil/TIL/assets/139416006/a780b3e1-5cc7-4272-ace0-2e7ad545441a)
![3](https://github.com/JeongJonggil/TIL/assets/139416006/e75b5e6e-1475-4ea3-abc3-cfc4fce8b293)



### 4. 화살표 함수(Arrow function expressions)

- 함수 표현식의 간결한 표현법
- **화살표 함수의 this → (렉시컬 this) → "바로 위"의 this를 따라간다. **(그래서 함수 선언 시 화살표 함수 사용하는 것을 권장함)
- 
![4](https://github.com/JeongJonggil/TIL/assets/139416006/157210fd-eba6-4135-99af-c10cb616e8ab)
![5](https://github.com/JeongJonggil/TIL/assets/139416006/c68e8491-085f-4815-94d4-0449d0792b4d)
![6](https://github.com/JeongJonggil/TIL/assets/139416006/70ea79ff-56ac-4b9c-ad5a-a47f8cd77997)
![7](https://github.com/JeongJonggil/TIL/assets/139416006/78b913b2-7aa6-4aa0-a80e-a59dbda8e8d7)

- 참고

![8](https://github.com/JeongJonggil/TIL/assets/139416006/81f7a605-07eb-41a4-bd6f-e4f697976ae7)

### 5. 객체(object)

- 키로 구분된 데이터 집합(data collection)을 저장하는 자료형
- 속성 참조 시 key값은 작은 따옴표를 안해도 문자열로 인식함.
- Object내부 요소를 동적으로 정의하고 싶을 때 key 는 [keyname], value는 변수나 표현식으로 할당 가능함

![9](https://github.com/JeongJonggil/TIL/assets/139416006/aa4c7227-1a9c-4c43-a3d2-cf451f7ab95a)
![10](https://github.com/JeongJonggil/TIL/assets/139416006/e36de127-e094-4a24-9c62-5511e745b00b)
![11](https://github.com/JeongJonggil/TIL/assets/139416006/3b9b2c7d-3fde-494f-b109-baee5379f164)
![12](https://github.com/JeongJonggil/TIL/assets/139416006/20e6b454-58d7-4546-b1e0-61fe68a84fef)


### 6. 객체와 함수

- Method
  - **객체 속성에 정의된 함수** → 객체 안에 value로 정의된 함수를 메서드라고 함
  - object.method() 방식으로 호출
  - 메서드는 객체를 '행동'할 수 있게 함
  - 'this'키워드를 사용해 객체에 대한 특정한 작업을 수행 할 수 있음

- 'this' keyword
  - 함수나 메서드를 호출한 객체를 가리키는 키워드
  - **"자기참조변수", "호출되는 방식에 따라 지칭하는 대상이 다르다"**
    - 함수 내에서 객체의 속성 및 메서드에 접근하기 위해 사용
    - **하기 이미지 "호출방법 : 메서드 호출"은 메서드를 통해서 this가 호출되었을 때를 말하는거임 →쉽게 그냥 객체 안에 작성된 함수(메서드) 의 this의 코드를 호출할 때 의미하는 거임**
  - 참고) **화살표 함수의 this → (렉시컬 this) → "바로 위"의 this를 따라간다. **(그래서 함수 선언 시 화살표 함수 사용하는 것을 권장함)
  - **정리**
  
  | 호출방법                      | 지칭                                                         |
  | ----------------------------- | ------------------------------------------------------------ |
  | 1. 일반함수로 호출되었을 떄   | 전역객체<br />(만드시 window를 의미하는게 아님 어디 환경에서 호출되느냐에 따라 다름. "전역객체"를 의미함) |
  | 2. 메서드로 호출되었을때      | 그 호출한 객체                                               |
  | 3. 생성자 함수로 호출되었을때 | new 호출되었을때 -> 미래에 생성할 "인스턴스"<br/>(파이썬 클래스에서 'self'와 비슷함) |
  | 4. 간접 호출 되었을때         | (생략)                                                       |
  
  

![13](https://github.com/JeongJonggil/TIL/assets/139416006/9bd305ae-662d-485b-9a46-192ed4df284f)
![14](https://github.com/JeongJonggil/TIL/assets/139416006/1fcf5bb1-5e3b-4f64-a668-b1d78a2363ac)
![15](https://github.com/JeongJonggil/TIL/assets/139416006/b92b6c8a-7a1e-4208-bbc7-679bf5ebdd32)
![16](https://github.com/JeongJonggil/TIL/assets/139416006/65b7f5cf-e6af-4b9a-9156-b283649ecce1)
![17](https://github.com/JeongJonggil/TIL/assets/139416006/94ed7f57-c824-4082-8302-55a7847250ec)
![35](https://github.com/JeongJonggil/TIL/assets/139416006/45816ba1-efe3-40c2-a059-0cb2114e4033)



### 7. 추가 객체 문법

- 단축 속성

![18](https://github.com/JeongJonggil/TIL/assets/139416006/da52d180-9a4a-48b8-99f3-40370c307039)

- 단축 메서드

![19](https://github.com/JeongJonggil/TIL/assets/139416006/9b53b4db-153b-4f66-b8b9-016b73b2b80d)

- 계산된 속성

![20](https://github.com/JeongJonggil/TIL/assets/139416006/bd7db97d-3e97-422d-b511-d55ea428ad73)
![21](https://github.com/JeongJonggil/TIL/assets/139416006/9fbb67cb-ded1-444b-8d1e-e80018d7b6ac)
![22](https://github.com/JeongJonggil/TIL/assets/139416006/d9aeacf7-7a89-42ab-817d-5c67c80fc16a)

- Object with '전개 구문'

![23](https://github.com/JeongJonggil/TIL/assets/139416006/d41afc34-0ecd-4b53-b278-8bd2902a0355)


- 유용한 객체 메서드

![24](https://github.com/JeongJonggil/TIL/assets/139416006/b19ca897-2cc1-4201-b064-ef297c70f288)


- Optional chaining('?.')

![25](https://github.com/JeongJonggil/TIL/assets/139416006/3876e88e-fc16-4643-b8fa-c32b89ff6caa)
![26](https://github.com/JeongJonggil/TIL/assets/139416006/7cf1b4d8-6fd8-46d4-a699-a59a8b720b29)
![27](https://github.com/JeongJonggil/TIL/assets/139416006/cc7e5d10-32f9-454a-a36e-c6eed75d09df)
![28](https://github.com/JeongJonggil/TIL/assets/139416006/aedd5dc8-005a-45a4-b855-060264bf7997)
![29](https://github.com/JeongJonggil/TIL/assets/139416006/e2a89189-045e-4ce6-b98b-46d236eabdbc)

### 8. JSON

![30](https://github.com/JeongJonggil/TIL/assets/139416006/3557917a-5568-4e88-8b24-1279240cddf8)
![31](https://github.com/JeongJonggil/TIL/assets/139416006/bc146333-b0c4-42a8-a87e-e13df0d3b906)

### 9. 배열(Array)

- 개요

  - Object(객체) : 키로 구분된 데이터 집합(data collection)을 저장하는 자료형 , **순서가 없음**

  - **Array(배열) : 순서가 있는** 데이터 집합을 저장하는 자료구조
    - 배열 요소 자료형 : 제약 없음
    - length 속성 사용 가능

- 메서드

  - |     메서드      |                      역할                      |
    | :-------------: | :--------------------------------------------: |
    |   push / pop    | 배열 끝 요소를 추가 / 제거(제거한 요소를 반환) |
    | unshift / shift | 배열 앞 요소를 추가 / 제거(제거한 요소를 반환) |

- Array Helper Methods

  - 배열을 **순회**하며 **특정 로직을 수행**하는 메서드

  - 메서드 호출 시 인자로 함수를 받는 것이 특징

  - | 메서드  | 역할                                                         |
    | ------- | ------------------------------------------------------------ |
    | forEach | 인자로 주어진 함수(콜백함수)를 배열 요소 각각에 대해 실행<br /> **(반환값이 없는 Array Helper)** |
    | map     | 배열 내의 모든 요소 각각에 대해 함수(콜백함수)를 호출하고, <br/>함수 호출 결과를 모아 **새로운 배열**을 반환<br />→ input과 output의 원소 개수가 일치할거 같다면 처음 map 사용을 고려해볼 것 |
    | filter  | 특정 조건문에 해당하는 값만 반환함<br/>→ input개수보다 output 개수가 작고 + output 개수가 2개 이상일 것 같으면 filter 사용을 고려해 볼 것 |
    | reduce  | JavaScript의 `reduce` 메서드는 배열의 각 요소에 대해 주어진 리듀서(reducer) 함수를 실행하고, 그 결과를 단일 값으로 축적<br />→ object.reduce(before,current,index,array) 방식 |

![36](https://github.com/JeongJonggil/TIL/assets/139416006/853ac716-e935-4b0b-ba38-57f9d2b245ca)
![37](https://github.com/JeongJonggil/TIL/assets/139416006/b835ab3e-ee35-4684-b229-d643de9f0722)
![38](https://github.com/JeongJonggil/TIL/assets/139416006/7d358c34-7e89-4e24-935a-d7fb48c5447b)
![39](https://github.com/JeongJonggil/TIL/assets/139416006/893c1a0f-3c18-4118-8bb7-53e888ee6e6e)
![40](https://github.com/JeongJonggil/TIL/assets/139416006/897f68c9-a790-47dc-9271-d023dcc170ae)
![41](https://github.com/JeongJonggil/TIL/assets/139416006/8a613de7-3cd0-4408-a63d-2e87b27068e8)
![42](https://github.com/JeongJonggil/TIL/assets/139416006/581b9e8a-aff2-4565-93fa-2e604e8ec453)
![43](https://github.com/JeongJonggil/TIL/assets/139416006/c8488923-e67f-456c-908d-6b2379ffb83c)
![44](https://github.com/JeongJonggil/TIL/assets/139416006/0a39ef0e-5a07-4c0a-8c85-9cb5041a0233)
![45](https://github.com/JeongJonggil/TIL/assets/139416006/eb39db50-1768-420e-8c2c-d7630b2562ed)

- 콜백함수(Callback function)

  - 다른 함수에 인자로 전달되는 함수

### 10. 참고(추가 배열 문법)

![46](https://github.com/JeongJonggil/TIL/assets/139416006/a0dbd5b6-6bd0-4118-b600-c55b98449446)
![47](https://github.com/JeongJonggil/TIL/assets/139416006/4b7f2ef1-b0db-46a8-a015-d1743e3c56ae)


### 11.참고(콜백함수 구조를 사용하는 이유, 배열은 객체다)

![48](https://github.com/JeongJonggil/TIL/assets/139416006/99c1b4e0-f55e-4227-bd7c-ee2d7c1b87a3)
![49](https://github.com/JeongJonggil/TIL/assets/139416006/cbb4f6cd-c3a1-42ee-9db7-02b6c55c98ab)
![50](https://github.com/JeongJonggil/TIL/assets/139416006/06510e3f-7191-44b2-95eb-c3d516b44934)
![51](https://github.com/JeongJonggil/TIL/assets/139416006/ecf2f558-b859-4745-b030-f76663762c4a)

### 12. 참고(클래스, new 연산자)
- 클래스 생성 예시 코드
```
class Person {
    // 생성자
    constructor(name, age) {
        this.name = name;
        this.age = age;
    }
    // 메소드
    introduce() {
        console.log(`Hello, my name is ${this.name} and I am ${this.age} years old.`);
    }
}
// 객체 생성
const john = new Person('John', 30);

// 메소드 호출
john.introduce();  // 출력: Hello, my name is John and I am 30 years old.
```
![32](https://github.com/JeongJonggil/TIL/assets/139416006/3b29baf5-1dc6-44fa-8136-54bc3409b9b3)
![33](https://github.com/JeongJonggil/TIL/assets/139416006/3e1325c1-072d-470b-ab50-2053500f020c)
![34](https://github.com/JeongJonggil/TIL/assets/139416006/af0be267-b4c3-49bc-baf3-a3a5c012ecef)
