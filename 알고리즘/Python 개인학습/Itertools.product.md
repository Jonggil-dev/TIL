# Itertools.product

### `itertools.product(iterable1, iterable2, ..., repeat=N)`

- iterable 요소를 바탕으로 데카르트 곱을 만들어줌

  > **데카르트 곱셈 : 두 개 이상의 집합에서 요소를 하나씩 선택하여 모든 가능한 조합을 생성하는 연산**

  - 예시 

    ```python
    import itertools
    
    list1 = [1, 2]
    list2 = ['a', 'b']
    
    result = list(itertools.product(list1, list2)
    
    """
    result = [[1, 'a'], [1, 'b'], [2, 'a'], [2, 'b']]
    """
    ```

- `repeat` 매개변수는 각 iterable에서 반복해서 요소를 선택하는 횟수를 지정. 즉, 각 iterable 리스트가 repeat 갯수 만큼 있다고 생각하면 됨

  - 예시

    ```python
    import itertools
    
    list1 = [1, 2]
    list2 = ['a', 'b']
    
    result = list(itertools.product(list1, list2, repeat=2))
    # repeat = 2 이므로 각 iterable(리스트) 가 2개씩 있다고 생각하고 데카르트 곱을 구하면 됨
    # [1, 2], ['a', 'b'], [1, 2], ['a', 'b']
    
    """
    result = 
    [(1, 'a', 1, 'a'), (1, 'a', 1, 'b'), (1, 'a', 2, 'a'), (1, 'a', 2, 'b') ...
    """
    ```