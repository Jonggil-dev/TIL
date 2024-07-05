# bisect

### 1. bisect

- **정렬된 리스트에서 특정 값이 삽입될 위치를 이진 탐색(binary search)을 통해 찾아주는 Python의 표준 라이브러리 모듈.**
- **정렬된 리스트에서 특정 값의 삽입 위치를 빠르게 찾을 때 사용**됩니다.



### 2. 주요 함수

1. **bisect.bisect_left(a, x)**:
   - 정렬된 리스트 `a`에서 **값 `x`가 삽입될 수 있는 가장 왼쪽 위치의 인덱스를 반환**

2. **bisect.bisect_right(a, x)**:
   - 정렬된 리스트 `a`에서 **값 `x`가 삽입될 수 있는 가장 오른쪽 위치의 인덱스를 반환**

```python
import bisect

arr = [1, 3, 4, 4, 5, 7, 9]
value = 4

index1 = bisect.bisect_left(arr, value)  	# index1 = 2
index2 = bisect.bisect_right(arr, value)	# index2 = 4
print(index1, index2)
```



3. **bisect.insort_left(a, x)**:

   - 정렬된 리스트 `a`에 값을 삽입할 때 이진 탐색을 사용하여 정렬된 상태를 유지하며 삽입합니다.

   - `insort_right` 함수도 있으며, 오른쪽 위치에 삽입합니다.


```python
import bisect

arr = [1, 3, 5, 7, 9]
value = 4

bisect.insort_left(arr, value)
print(arr)  # 출력: [1, 3, 4, 5, 7, 9] (정렬된 상태로 4를 삽입)
```