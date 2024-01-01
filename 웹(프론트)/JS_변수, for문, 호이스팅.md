# Basic syntax of JavaScript

### 1. 변수

- 식별자(변수명) 작성 규칙

  - 반드시 문자,달러($) 또는 밑줄(_)로 시작
  - 대소문자를 구분
  - 예약어 사용 불가
    - for, if, function 등

  - 카멜 케이스(camelCase)
    - 변수,객체,함수에 사용
    - 첫 글자는 소문자, 그리고 각 단어의 첫 글자는 대문자로 시작
    - ex) userProfile`, `isUserLoggedIn`, `findByName
  - 파스칼 케이스(PascalCaes)
    - 클래스, 생성자에 사용
    - 첫 글자도 대문자로 시작, 그리고 각 단어의 첫 글자도 대문자로 시작.
    - ex) UserProfile, IsUserLoggedIn, FindByNam
  - 대문자 스네이크 케이스(SNAKE_CASE)
    - 상수(constants)에 사용
    - 모든 문자가 대문자이며, 단어 사이에 언더스코어(`_`)를 사용하여 구분.
    - ex) USER_PROFILE, IS_USER_LOGGED_IN, FIND_BY_NAME

-  **변수 선언 키워드**(let, const)
  - **const는 선언 시 반드시 초기값 설정 필요, let은 초기값 없어도 선언 가능**

![1](https://github.com/JeongJonggil/TIL/assets/139416006/7714f5a3-ee6a-4131-8b24-4e96d213e3e9)
![2](https://github.com/JeongJonggil/TIL/assets/139416006/4973d72e-ef32-4e5a-84bd-a49049e71687)
![3](https://github.com/JeongJonggil/TIL/assets/139416006/6f013b70-a777-430f-8c5f-1f180219a769)


> - 기본적으로 const 사용을 권장
>
> - 재할당이 필요한 변수는 let으로 변경해서 사용



### 2. 데이터 타입

- 원시 자료형(Primitive type)

  - Number, String, Boolean, undefined, Null

  - **변수에 값이 직접 저장되는 자료형 (불변, 값이 복사) → 메모리 주소가 복사되는게 아님**

  - 변수에 할당될 때 값이 복사되므로 변수 간에 서로 영향을 미치지 않음

    ```javascript
    ex)
    let a = 10
    let b = a
    b = 20
    console.log(a) // 10
    console.log(b) // 20
    ```

    

- 참조 자료형(Reference type)

  - objects (objects, Array, Function)

  - 여기서 말하는 object는 파이썬의 객체와는 다른 의미임. 그냥 자료타입 중의 하나로 object를 의미함**(object는 dictionary와 비슷한 타입)**

  - 객체의 메모리 주소가 저장되는 자료형(가변, 주소가 복사)

  - 객체를 생성하면 객체의 메모리 주소를 변수에 할당 → 변수간에 서로 영향을 미침

    ```javascript
    const obj1 = { name : 'Alice', age:30 }
    const obj2 = obj1
    obj2.age = 40
    
    console.log(obj1.age) // 40
    console.log(obj2.age) // 40
    ```



### 3. 원시 자료형

- Number : 정수 또는 실수형 숫자를 표현하는 자료형

![4](https://github.com/JeongJonggil/TIL/assets/139416006/39c9850c-072c-45f0-9a84-82cacd97b62b)


- String : 텍스트 데이터를 표현하는 자료형

![5](https://github.com/JeongJonggil/TIL/assets/139416006/b75c59cb-0439-43bf-b5d6-cac0280071d0)

- Template literals(템플릿 리터럴) : 파이썬의 f' 스트링과 비슷한 자료형
  
![6](https://github.com/JeongJonggil/TIL/assets/139416006/5a2e0023-4ec0-42c4-a6e0-ba1497a0e99d)

- null & undefined

  - null : 변수의 값이 없음을 의도적으로 표현할 때 사용
  - undefined : 변수 선언 이후 직접 값을 할당하지 않으면 자동으로 할당됨

  ```javascript
  let a = null
  console.log(a) // null
  
  let b
  console.log(b) // undefined
  ```
  
![7](https://github.com/JeongJonggil/TIL/assets/139416006/642e5665-3c72-494d-92e6-d44e09ce8c98)

- Boolean

  - 조건문 또는 반복문에서 Boolean이 아닌 데이터 타입은 하기 "자동 형변환 규칙"에 따라 true 또는 false로 변환됨

  - 자동 형변환

    | 데이터 타입 |   false    |       true       |
    | :---------: | :--------: | :--------------: |
    |  undefined  | 항상 false |        X         |
    |    null     | 항상 false |        X         |
    |   Number    | 0, -0, NaN | 나머지 모든 경우 |
    |   String    | 빈 문자열  | 나머지 모든 경우 |

    

### 4.연산자

- 할당 연산자

  - 오른쪽에 엤는 피연산자의 평가 결과를 왼쪽 피연산자에 할당하는 연산자
  - 단축 연산자 지원  
    	(1) && : 피연산자를 왼쪽에서 오른쪽으로 평가하면서 "첫" 거짓 같은 피연산자를 만나면, '즉시' 그 '값을 반환'한다, 만약 모든값이 참이면, 마지막을 반환한다  
    	(2) || : 피연산자를 왼쪽에서 오른쪽으로 평가하면서 "첫" 참 같은 피연산자를 만나면, '즉시' 그 '값을 반환'한다, 만약 모든값이 거짓이면, 마지막을 반환한다

  ```javascript
  let a = 0
  
  a += 10
  console.log(a) // 10
  
  a -= 3
  console.log(a) // 7
  
  a *= 10
  console.log(a) // 70
  
  a %= 7
  console.log(a) // 0
  ```

- 증가 & 감소 연산자

  - 증가 연산자(++)
    - 피연산자를 증가(1을 더함)시키고 연산자의 위치에 따라 증가하기 전이나 후의 값을 반환
  - 감소 연산자(--)
    - 피연산자를 감소(1을 뺌)시키고 연산자의 위치에 따라 감소하기 전이나 후의 값을 반환
  - **위 두가지 보다는  += 또는 -=와 같이 더 명시적인 표현으로 작성 하는 것을 권장**

  ```javascript
  let x = 3
  const y = x++
  console.log(x,y) // 4 3
  
  let a = 3
  const b = ++a
  console.log(a, b) // 4 4
  ```

- 동등 연산자 ( == )

  - 두 피연산자가 같은 값으로 펴가되는지 비교 후 boolean 값을 반환
  - **'암묵적 타입 변환' 통해 타입을 일치시킨 후 같은 값인지 비교 → 이 때문에 코딩 시 예상치 못한 에러에 부딪힐 수 있음**
  - 두 피연산자가 모두 객체일 경우 메모리의 같은 객체를 바라보는지 판별

  ```javascript
  console.log(1 == 1) // true
  console.log('hello' == 'hello') // true
  console.log('1' == 1) // true
  console.log(0 == false) // true
  ```

- 일치 연산자 ( === )

  - 두 피연산자의 값과 타입이 모두 같은 경우 true를 반환
  - 같은 객체를 가리키거나, 같은 타입이면서 같은 값인지를 비교
  - **엄격한 비교과 이뤄지며 암묵적 타입 변환이 발생하지 않음**
  - 특수한 경우를 제외하고는 동등 연산자가 아닌 **일치 연산자 사용 권장**

  ```javascript
  console.log(1 === 1) // true
  console.log('hello' === 'hello') // true
  console.log('1' === 1) // false
  console.log(0 === false) //false
  ```

- 논리 연산자

  - and : &&
  - or : ||
  - not : !
  - 단축 평가 지원

### 5. 조건문

- if 

  ```javascript
  //예시
  
  const name = 'customer'
  
  if (name === 'admin') {
  	console.log('관리자님 환영해요')
  } else if (name === 'customer') {
  	console.log('고객님 환영해요')
  } else {
  	console.log('반갑습니다. ${name}님')
  }
  ```

- 조건(삼항) 연산자

  - 세 개의 피연산자를 받는 유일한 연산자
  - 앞에서부터 조건문, 물음표(?), 조건문이 참일 경우 실행할 표현식, 콜론(:), 조건문이 거짓일 경우 실행할 표현식이 배치

  ```javascript
  //예시
  
  const func2 = function (person) {
  	return person > 17 ? 'Yes' : 'No'
  }
  ```

### 6. 반복문

- while 
  - while(조건문){ 실행문 } 형태

```javascript
//예시

while(i<6){
	console.log(i)
    i += 1
}
```

- for(초기문; 조건문; 증감문){ 실행문 } 형태
  -  for문에서 초기문, 조건문, 증감문은 생략 가능
```javascript
//예시1
for(let i = 0; i<6; i++){
	console.log(i)
}

//예시2 (초기문 생략)
let i = 5;
for (; i < 10; i++) {
    console.log(i);
}

//동작원리
1. 반복문 진입 및 변수 i 선언
2. 조건문 평가 후 코드 블럭 실행
3. 코드 블록 실행 이후 i 값 증가
4. 다시 2~3번 반복
```

- for ... in

  - 객체의 **열거 가능한 속성(property)에** 대해 반복 **(object자료형의 key를 속성이라고 지칭함)**
  - **for ... in은 object 자료형에서만 사용 한다고 생각하면 문제없음.**

  ```javascript
  //예시
  
  const fruits = { a:'apple', b:'banana'}
  
  for (const property in fruits){
  	console.log(property) // a, b 출력
  	console.log(object[property]) // apple, banana 출력
  }
  ```

- for ... of

  - **반복 가능한 객체(배열,문자열 등)**에 대해 반복
  - **파이썬에서는 딕셔너리 형이 반복가능한 자료형으로 분류되지만, JavaScript에서는 object 자료형은 반복 가능한 자료형이로 분류되지 않음 → 그래서  object 자료형은 위 처럼 for ... of 을 사용하면 에러뜸**

  ```javascript
  //예시
  
  const numbers = [0,1,2,3]
  
  for (const number of numbers){
  	console.log(number) // 0, 1, 2, 3 출력
  }
  ```

- 배열 반복과 for ... in 

![8](https://github.com/JeongJonggil/TIL/assets/139416006/0829dd13-30ed-493d-81c7-117e9fad3705)
![9](https://github.com/JeongJonggil/TIL/assets/139416006/cb225be7-a284-41b3-9ad2-a4224f84f5c5)


- for ... in 과 for ... of 차이 예시 
  - **결과적으로 for ... in은 object 자료형에서만 사용한다고 정리하면 된다 !!** 
  - 그 외 자료형은 for ... of 사용한다고 생각하면 정리가 쉬움

![10](https://github.com/JeongJonggil/TIL/assets/139416006/5274f895-8b7d-4056-a9fd-1afb9a877117)


- 반복문 사용 시 const 사용 여부

![11](https://github.com/JeongJonggil/TIL/assets/139416006/547575d7-2c84-4256-b152-b4868a6536da)

- 반복문 종합

![12](https://github.com/JeongJonggil/TIL/assets/139416006/4ff8bed2-740a-425d-91f8-a6a8b6b70f8c)


### 7. 참고(세미콜론, var, 함수 스코프, 호이스팅, NAN)

- 세미콜론(semicolon)
  - 자바스크립트는 세미콜론을 선택적으로 사용 가능
  - 세미콜론이 없으면 ASI에 의해 자동으로 세미콜론이 삽입됨
    - ASI(Automatic Semicolon Insertion, 자동 세미콜론 삽입 규칙)
  - JavaScript를 만든 Brendan Eich 또한 세미콜론 작성을 반대
- 변수 선언 키워드 - 'var'
  - ES6 이전에 변수 선언에 사용했던 키워드
  - 재할당 가능 & 재선언 가능
  - "호이스팅" 되는 특성으로 인해 예기치 못한 문제 발생 가능
    - **따라서 ES6 이후부터는 var 대신 const와 let을 사용하는 것을 권장**
  - 함수 스코프(function scope)를 가짐
  - **변수 선언 시 var,const,let 키워드 중 하나를 사용하지 않으면 자동으로 var로 선언됨**
  - var의 경우 초기화(할당) 이전의 위치에서 접근 시 undefined를 반환  

- 함수 스코프(function scope)

  - 함수의 중괄호 내부를 가리킴
  - 함수 스코프를 가지는 변수는 함수 바깥에서 접근 불가능

  ```javascript
  function foo(){
  	var x = 1
  	console.log(x) // 1
  }
  
  console.log(x) // ReferenceError: x is not defined
  ```

- 호이스팅(hoisting)
  - '런타임 이전'에(=코드가 실행되기 전) '선언문'들이 끌어올려지는 행위
  - JavaScript 엔진이 코드를 해석하는 과정에서 변수나 함수의 선언을 코드의 최상단으로 끌어올리는 현상. 그러나 호이스팅에서 중요한 점은 오직 선언만이 끌어올려진다는 것. 할당(초기화) 부분은 원래 코드에 있던 그 위치에서 그대로 수행됨.


  ```javascript
  console.log(name) //undefined => 선언 이전에 참조
  var name = '홍길동' //선언
  
  // 위 코드를 암묵적으로 아래와 같이 이해함
  var name
  console.log(name)
  var name = '홍길동'
  ```

  - JavaScript에서 변수들은 실제 실행시에 코드의 최상단으로 끌어 올려지게 되며 (hoisted) 이러한 이유 때문에 var로 선언된 변수는 선언 시에 undefined로 값이 초기화되는 과정이 동시에 발생

  ```javascript
  console.log(name) // 변수명을 name으로 하면 windows내부에서 name이라는 프로퍼티를 사용하기 때문에 처음에 공백 출려되고 다시 저장하면 계속 홍길동이 출력됨 (변수명 다른거쓰면 정상적으로 호이스팅 확인 가능)
  var name = '홍길동'
  
  console.log(age) // ReferenceError : Cannot access 'age' before initialization
  let age = 30
  
  console.log(height) // ReferenceError : Cannot access 'height' before initialization
  const height = 170
  ```
### 8. 참고 - let 호이스팅
- 결론 : let, const는 호이스팅은 동작하지만 에러가 뜰 뿐 호이스팅이 동작하지 않는건 아니다
  
- let 선언 후 console.log 출력 
```
let age
console.log(age)

# 내부 동작:
let age
//여기 사이에 age = undefined 가 자동으로 실행됨 ※이게 핵심 포인트※
console.log(age)
```

- 호이스팅 로직
```
console.log(age)
let age = 30

# 내부동작:
let age

console.log(age) //여기서 에러 발생
age = 30 
```

### 9. 참고 - 단축 연산자 응용  
- 단축 연산자  
        (1) && : 피연산자를 왼쪽에서 오른쪽으로 평가하면서 "첫" 거짓 같은 피연산자를 만나면, '즉시' 그 '값을 반환'한다, 만약 모든값이 참이면, 마지막을 반환한다  
    	(2) || : 피연산자를 왼쪽에서 오른쪽으로 평가하면서 "첫" 참 같은 피연산자를 만나면, '즉시' 그 '값을 반환'한다, 만약 모든값이 거짓이면, 마지막을 반환한다  
  	**(3) &&와 ||가 섞여 있으면 and가 우선순위가 높으므로 &&가 먼저 연산됨**  
- 위에 로직을 이해하면 3개 이상이 단축연산자로 연결 되었을 때도 이해가 쉬움  
- **실무에서는 아래처럼 응용됨**  
```
#html
1. { data && <div> {data} </div> } # data에 코드가 없는경우의 방어코드로 사용할때 
2. if (func1() || func2())	# func1의 성능이 좋아서 가능하다면 func1()을 함수를 우선적으로 실행 시키고 싶을 때 
```

