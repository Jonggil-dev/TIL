# 리스트 내 특정 요소를 기준으로 max, min 찾기

리스트 내의 각 튜플의 특정 인덱스에 대한 최대값이나 최소값을 구하고 싶다면, `key` 매개변수를 사용해 특정 요소를 기준으로 최대 또는 최소를 찾습니다.

```python
# 주어진 리스트 예시
tuples_list = [(5, 2), (3, 4), (9, 1)]

# 첫 번째 요소 중 최대값 찾기
max_first = max(tuples_list, key=lambda x: x[0])[0]

# 두 번째 요소 중 최소값 찾기
min_second = min(tuples_list, key=lambda x: x[1])[1]

print("첫 번째 요소의 최대값:", max_first)
print("두 번째 요소의 최소값:", min_second)
```

- `max(tuples_list, key=lambda x: x[0])[0]`: `key=lambda x: x[0]`는 각 튜플의 첫 번째 요소를 최대값 비교 기준으로 사용하게 합니다. `max()` 함수가 반환하는 것은 최대값을 가진 튜플 전체이므로, `[0]`을 사용하여 그 튜플의 첫 번째 요소만 추출합니다.
- `min(tuples_list, key=lambda x: x[1])[1]`: 이는 각 튜플의 두 번째 요소를 최소값 비교의 기준으로 삼아 `min()` 함수로 최소값을 가진 튜플을 찾고, `[1]`로 그 튜플의 두 번째 요소만 추출합니다.

