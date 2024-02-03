# 예외 처리(try-catch, throw)

```
function A() {
  try {
    test2();
    throw new Error('에러 발생');
    test3();
   
  } catch (error) {
  	test4();
    throw error;
    test5();
   
  }
}

function B() {
  try {
  test1();
    A(); 
  test6();
    
  } catch (error) {
    // A 함수에서 throw된 에러가 여기서 잡힘
    test7()
    console.error(error.message);
    
  }
}
```



- 위 코드에서 B함수를 실행했을 때 A함수에서 에러가 발생햇을 경우

  - test1 -> test2 -> throw new Error('에러 발생') -> test4 ->  throw error -> test7 -> test 8 -> console.error() 순서로 실행됨

  - error객체의 message 키에 new Error()의 매개변수가 값으로 담겨서 error가 throw 됨

1. `try` 블록 안에서 `throw new Error()`를 만나게 되면, 그 시점에서 뒤에 작성된 다른 코드는 무시되고 즉시 해당 `try` 블록에 대응하는 `catch` 블록이 실행됩니다. 이는 예외가 발생하면 즉시 예외 처리 메커니즘으로 넘어가야 하기 때문입니다.
2. 함수 A가 `try-catch` 구문을 사용하여 예외를 처리하고 있고, 함수 B의 `try` 블록 안에서 함수 A가 호출될 때, 함수 A 내부에서 발생한 예외는 먼저 함수 A의 `catch` 블록에서 처리됩니다. 
3. 만약 함수 A의 `catch` 블록에서 다시 `throw`를 사용하여 예외를 던지게 되면, 이 예외는 함수 B로 전파되어 함수 B의 `catch` 블록에서 처리됩니다. 
4. 이때, 함수 A의 `catch` 블록 내의 `throw` 문 다음에 오는 코드는 실행되지 않으며, 함수 B의 `try` 블록 내에서 함수 A를 호출한 직후의 코드 역시 예외가 전파된 시점에서는 무시되고 바로 함수 B의 `catch` 블록으로 제어가 이동합니다.