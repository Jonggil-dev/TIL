# 컴프리헨션

## : 리스트를 쉽게, 짧게 한 줄로 만들 수 있는 파이썬의 문법 (if문 없이 리스트 초기화로도 사용가능 함)

```
[expression for item in iterable if condition]
```

1. expression: 각 요소를 처리하고 새로운 리스트에 추가할 표현식입니다.

2. item: iterable에서 각 요소를 가져옵니다.

3. iterable: 순회 가능한(iterable) 객체로, 리스트, 튜플, 문자열 등이 될 수 있습니다.

4. condition (선택적): 조건식으로, 이 조건이 참(True)인 경우에만 expression이 리스트에 추가됩니다.

## (예시) 
  [x*2 for x in my_list if x != value_to_remove]`에서는 `my_list`의 각 요소를 `x`로 가져와서, `x`와 `value_to_remove`가 같지 않은 경우에만 `x`*2를 새로운 리스트에 추가합니다. 이를 통해 `value_to_remove`와 일치하지 않는 모든 요소를 제거한 새로운 리스트를 생성할 수 있습니다.

                
