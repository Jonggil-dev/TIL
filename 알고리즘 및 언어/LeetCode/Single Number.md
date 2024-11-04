# Single Number

### 내 풀이

```java
import java.util.*;

class Solution {
    public int singleNumber(int[] nums) {
        Set<Integer> tmp= new HashSet<>();
        
        for(int i = 0; i < nums.length; i++){
            if(tmp.contains(nums[i])){
                tmp.remove(nums[i]);
                continue;
                
            } else {
                tmp.add(nums[i]);
            }
        }
        
        int element = tmp.iterator().next();
        return element;
    }
}
```

- 정렬된 순서가 아니기 때문에 최소 전체 순회 1번은 무조건 필요함
  1. sort 한 후 재순회-> O(NlogN + N) -> X
  2. **순회 1번만에 끝내는 방법?**
     - 무조건 원소가 2개니까 -> 전체 순회 하며 set에 추가 + 이미 드간거는 제거 -> 마지막에 1개만 남음 



### 더 최적화된 코드

```java
class Solution {
    public int singleNumber(int[] nums) {
       
        int res = 0;
        
        for(int num : nums){
            res ^= num;
        }
        
        return res;
    }
}
```

- XOR 연산 사용 -> a ^= a 를 하면 0 이됨
- 2번 씩 숫자가 나오니까, XOR 연산 누적하면 남는 숫자 1개만 남게됨
- a ^ b ^ c ^ b ^ a > 교환 법칙 -> (a ^ a) ^ (b ^ b) ^ c = c