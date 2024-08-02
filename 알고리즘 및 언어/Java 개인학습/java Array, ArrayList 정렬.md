# java 배열 및 리스트 정렬

### 0. 참고

- https://velog.io/@dlzlqlzl/Java-%EB%B0%B0%EC%97%B4-%EC%A0%95%EB%A0%AC%ED%95%98%EA%B8%B0-%EC%98%A4%EB%A6%84%EC%B0%A8%EC%88%9C-%EB%82%B4%EB%A6%BC%EC%B0%A8%EC%88%9C-%EC%82%AC%EC%9A%A9%EC%9E%90-%EC%A0%95%EB%A0%AC
- https://velog.io/@dlzlqlzl/Java-ArrayList-%EC%A0%95%EB%A0%AC%ED%95%98%EA%B8%B0-%EC%98%A4%EB%A6%84%EC%B0%A8%EC%88%9C-%EB%82%B4%EB%A6%BC%EC%B0%A8%EC%88%9C-%EC%82%AC%EC%9A%A9%EC%9E%90-%EC%A0%95%EC%9D%98-%EC%A0%95%EB%A0%AC



### 1. ★★ 정리 ★★

- **배열을 정렬하는 방법은 `Arrays.sort()`, 리스트를 정렬하는 방법은 `Collections.sort() 또는 인스턴스.sort()`**

  - `Collections.sort()`로 배열을 정렬하고 싶으면 배열을 리스트로 바꿔서 써야됨 
  - `Arrays.sort()` 와 `Collections.sort()`는 사용하는 정렬 알고리즘에 차이가 있어서 시간 복잡도, 공간 복잡도에 차이가 존재함

- 위 방법 모두에서 정렬 조건을 정의해서 사용하고 싶으면 `Comparator`를 사용

  - `sort()`의 매개변수로 사용자가 정의한 `Comparator`를 넘겨주면 됨

  - `Comparator` 사용법

    - `Comparator.compare(T o1, T o2)`메서드를 람다식으로 구현해서 사용
      
      - **Comparator의 반환 값에 따라서 정렬이 이루어짐**
      - **람다식의 return 값이 양수를 반환할 경우 o1과 o2의 위치를 바꿈**
        **(이것만 기억하면 사용하기 편함)**
      - 예시
        
        ```java
        import java.util.*;
        
        public class Main {
            public static void main(String[] args) {
                List<String> strings = Arrays.asList("apple", "banana", "kiwi", "cherry");
                Collections.sort(strings, (s1, s2) -> Integer.compare(s1.length(), s2.length()));
            }
        }
        ```
      
    - **Comparator 체이닝 방식으로 구현하는게 더 쉬워 보임**
      
      - 예시
      
        ```java
        import java.util.*;
        
        public class Main {
            public static void main(String[] args) {
                List<String> strings = Arrays.asList("apple", "banana", "kiwi", "cherry");
                Collections.sort(strings, Comparator.comparingInt(String::length)
                              .thenComparing(Comparator.naturalOrder()));
            }
        }
        ```
      
        

### 2. 배열 정렬 (int[], Integer[], String[] 등)

- #### `Arrays.sort()` 

  - `Arrays.sort(Array)` : 배열을 오름차순으로 정렬
  - `Arrays.sort(Array,fromIndex,toIndex)` : 정렬 범위를 인덱스로 지정해서 해당 범위를 오름차순으로 정렬함

  - `Arrays.sort(Array, comparator)` : Comparator를 이용하여 배열을 정렬, Comparator의 메서드는 인자로 객체를 사용하기 때문에 Array의 요소가 Wrapper Class로 정의 되어 있어야 사용 가능함

    - ```java
      Integer[] intArr = new Integer[] {1,2,1,1,1};
      Arrays.sort(intArr,Comparator.naturalOrder()); // 오름차순
      Arrays.sort(intArr,Comparator.reverseOrder()); // 내림차순
      ```

    - **Comparator를 활용해 직접 정렬 기준을 정의할 수 있음**

      - 람다식을 활용해 Comparator를 사용한 예시

      ```java
      Integer[] intArr = new Integer[] {1,3,2,5,4};
      // 오름차순 정렬
      Arrays.sort(intArr, (a, b) -> a - b);
      // 내림차순 정렬
      Arrays.sort(intArr, (a, b) -> b - a);
      ```

      

### 3. 리스트 정렬

- #### `Collections.sort()`

  - `Collections.sort(List)` : 리스트를 오름차순으로 정렬

  - `Collections.sort(List, comparator)` : Comparator를 이용하여 리스트를 정렬
    - **사용법은 1. 정리 참고**

- #### `인스턴스.sort()`

  - **collections.sort()와 사용법 동일한데 직접 인스턴스를 사용하므로, 매개변수에 List는 빠지고 comparator만 넣으면 됨**
