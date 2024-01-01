# map() 실행 로직

1. iterable의 요소가 function의 인자로 들어가서 function이 실행되는게 아니고,
   **fucntion을 iterable의 각각의 요소에 적용한다고 생각하는게 옳음**

   2. `map()` 함수는 `function`을 `iterable`의 각 요소에 적용하여 그 결과를 iterator로 반환함. (값을 호출하기 위한 대기중인 상태로 저장된다고 생각하면 됨)



    ※ **iterator** 
    : iterator는 값을 **필요할 때**마다 하나씩 값을 반환할 수 있는 메커니즘 또는 접근방법을 제공하는 객체. iterator 자체는 값을 가지는 것이 아님. 실제로 iterator에는 데이터가 저장되어 있지 않음.



##### 결과적으로

```
def rental_book(user):
    import book
    num_books = book.decrease_book(user['age'] // 10)
    print(f"{user['name']}님이 {num_books}권의 책을 대여하였습니다.")

#1. map(rental_book, info)
#2. list(map(rental_book, info))
```

#1의 경우
     * rental_book 함수를 info 요소에 적용시킨 iterator를 반환한 상태 (대기중인 상태)임
       → **print문 실행 안됨**

#2의 경우
     * list()를 실행하면서 iterator상태(대기중 상태)였던 값을 반환하면서 print문을 실행하게 됨
