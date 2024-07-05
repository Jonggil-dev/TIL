# Funcition : 인자

## 1. 매개변수(parameter) vs 인자(argument)

        (1) 매개변수 : 함수를 정의할 때, 함수가 받을 값을 나타내는 변수

        (2) 인자 : 함수를 호출할 때, 실제로 전달되는 값

```
ex)
    def add_numbers(x,y) # x와 y는 매개변수(parameter)
        result = x + y
        return result

    sum_ressult = add_numbers(2,3) # a와 b는 인자(argument)
```

## 2. Positional Arguments(위치 인자)

        (1) 함수 호출 시 인자의 위치에 따라 전달되는 인자

```
def g(reet(name, age)
    print(f'안녕하세요, {name}님! {age}살이시군요')


greet('Alice',25) 
#여기서 Alice, 25는 매개변수의 위치에 맞게 입력되는 위치인자
```

  

## 3. Default Argument Values (기본 인자 값)

        (1) 함수 정의에서 매개변수에 기본 값을 할당하는 것

        (2) 함수 호출 시 인자를 전달하지 않으면, 기본값이 매개변수에 할돵됨

```
def greet(name, age=30):
    print(f'안녕하세요, {name}님! {age}살이시군요')

 #age에 인자 전달되지 않으면 age는 기본 값인 30으로 할당 
```

## 4.  Keyword Argument (키워드 인자)

        (1) 함수 호출 시 인자의 이름과 함께 값을 전달하는 인자

        (2) 인자의 순서는 중요하지 않으며, 인자의 이름을 명시하여 전달

        **※ 단, 호출 시 키워드 인자는 위치 인자 뒤에 위치해야 함****

```
def greet(name, age):
    print(f'안녕하세요, {name}님! {age}살이시군요.')


greet(name='Dave', age=35) # 안녕하세요, Dave님! 35살이시군요.
greet(age=35, 'Dave') #이름과 나이의 위치 인자가 바껴있어 오류 발생

```



## 5. Arbitrary Argument Lists (임의의 인자 목록)

        (1) 정해지지 않은 개수의 인자를 처리하는 인자

        (2) 함수 정의 시 매개변수 앞에 '*'를 붙여 사용하며, 여러 개의 인자를 tuple로 처리

        (3) 메모리 사용 측면에서 과한 메모리를 사용하므로 불필요한 경우 사용을 지양함



## 6. Arbitrary Keyword Argument Lists (임의의 키워드 인자 목록)

        (1) 정해지지 않은 개수의 키워드 인자를 처리하는 인자

        (2) 함수 정의 시 매개변수 앞에 '**'를 붙여 사용하며, 여러 개의 인자를 dictionary로 처리   

        (3) 5. 임의의 인자 목록의 경우는 데이터의 안전성(불변성)이 중요할 때 사용하는 반면,

             6. 임의의 키워드 인자 목록은 데이터 처리 속도(신속성)이 중요할 떄 주로 사용


