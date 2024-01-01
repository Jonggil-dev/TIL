# vue- Computed 추가자료 (ref vs computed)

### 1. ref vs computed

1. **반응형 데이터 (`ref`)의 기본 개념**:

   - `ref`를 사용하면 변수를 반응형으로 만들 수 있습니다. 이는 Vue가 해당 변수의 값이 변경되는 것을 감지하고, 해당 변수를 사용하는 UI 부분을 자동으로 업데이트하게 합니다.

2. **`computed` 속성과 의존성 추적**:

   - `computed`는 하나 이상의 반응형 데이터를 기반으로 한 복잡한 계산이나 파생된 상태를 위해 사용. 이는 종속된 데이터가 변경될 때만 재계산되며, 이를 통해 효율적으로 리소스를 사용합니다.
   - 반응형 데이터가 다른 반응형 데이터에 의존하기 위해서는, 의존되는 데이터가 변경될 때마다 의존하는 데이터가 업데이트되어야 합니다. 이때 `computed` 사용

   - 예를 들어, `const b = computed(() => c.value * 2);`에서 `b`는 `c`의 변경에 반응하여 자동으로 업데이트됩니다.

3. **Vue의 효율적인 DOM 업데이트**:
   - Vue는 가상 DOM을 사용하여 효율적으로 실제 DOM을 업데이트합니다. 데이터가 변경될 때, Vue는 변경된 부분만을 실제 DOM에 반영하여 성능을 최적화합니다.
   - `computed` 속성이나 반응형 데이터에 의존하는 UI 요소만 변경됩니다.

### 2. `ref` 사용 예시

`ref`는 단순한 반응형 데이터를 생성할 때 사용됩니다. 예를 들어, 사용자의 입력을 추적하는 간단한 경우에 적합합니다.

```vue
<template>
  <input v-model="userInput">
  <p>입력한 내용: {{ userInput }}</p>
</template>

<script setup>
import { ref } from 'vue'
const userInput = ref('')

}
</script>
```

이 예시에서, 사용자가 입력 필드에 무엇인가를 타이핑하면 `userInput` 변수가 업데이트되고, 이에 따라 `<p>` 태그 내의 내용도 자동으로 업데이트됩니다.

### 3. `computed` 사용 예시

`computed`는 파생된 상태를 계산할 때 사용됩니다. 예를 들어, 여러 반응형 데이터를 조합하여 새로운 값을 생성하는 경우에 적합합니다.

```vue
<template>
  <input v-model="firstName">
  <input v-model="lastName">
  <p>전체 이름: {{ fullName }}</p>
</template>

<script setup>
import { ref, computed } from 'vue'

const firstName = ref('')
const lastName = ref('')
const fullName = computed(() => firstName.value + ' ' + lastName.value)

return { firstName, lastName, fullName }
  }
}
</script>
```

이 예시에서는 `firstName`과 `lastName`이라는 두 개의 입력 필드가 있고, `computed` 속성인 `fullName`을 사용하여 이 두 값을 결합한 전체 이름을 생성합니다. `firstName` 또는 `lastName` 중 하나라도 변경되면 `fullName`도 자동으로 업데이트됩니다.

### 요약

- `ref`는 단순한 반응형 데이터를 위해 사용되며, 템플릿에서 직접적으로 바인딩될 때 그 값의 변경을 UI에 자동으로 반영합니다.
- `computed`는 하나 이상의 반응형 데이터를 기반으로 한 복잡한 계산이나 파생된 상태를 위해 사용됩니다. 이는 종속된 데이터가 변경될 때만 재계산되며, 이를 통해 효율적으로 리소스를 사용합니다.
