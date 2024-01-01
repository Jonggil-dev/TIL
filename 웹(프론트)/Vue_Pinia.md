# Vue_Pinia

### 1. State Mangement (상태관리)

- 컴포넌트 구조
  - 상태(State)
    - 앱 구동에 필요한 기본 데이터
  - 뷰(View)
    - 상태를 선언적으로 매핑하여 시각화
  - 기능(Actions)
    - 뷰에서 사용자 입력에 대해 반응적으로 상태를 변경할 수 있게 정의된 동작

```javascript
export const useCounterStore = defineStore('counter', () => {
  
  //상태(State)
  const count = ref(0)
  const doubleCount = computed(() => count.value * 2)
  
  //기능(Actions)
  function increment() {
    count.value++
  }
```

### 2. Pinia

- props, emit은 계층 구조가 싶어질 경우 비효율적, 관리가 어려워 짐 → 각 컴포넌트의 공유 상태를 추출하여, 전역에서 참조할 수 있는 저장소에서 관리 → **Pinia**

![1](https://github.com/JeongJonggil/TIL/assets/139416006/d88b5129-8c84-47fa-a8af-c03874318b1e)


- Vue 공식 상태 관리 라이브러리

- Vite 프로젝트 빌드 시 pinia 라이브러리 추가 부분 Yes로 설정 

### 3. Pinia 구조

- 구성 요소

  - store
![2](https://github.com/JeongJonggil/TIL/assets/139416006/9cfc5b75-a1fc-4361-aa4b-b70c80823b81)

  - state
![3](https://github.com/JeongJonggil/TIL/assets/139416006/f4b65dd5-7c97-48c9-95a4-03a0fd73c4b8)

  - getters
![4](https://github.com/JeongJonggil/TIL/assets/139416006/4cc02419-35c6-4aca-acf8-7fdfc132bbb7)

  - actions
![5](https://github.com/JeongJonggil/TIL/assets/139416006/3218d523-4047-452d-9f70-335b3aaad0d9)

  - plugin

    - 애플리케이션의 상태 관리에 필요한 추가 기능을 제공하거나 확장하는 도구나 모듈
    - 애플리케이션의 상태 관리를 더욱 간편하고 유연하게 만들어주며 패키지 매니저로 설치 이후 별도 설정을 통해 추가 됨

- **요약**
  - Pinia는 store라는 저장소를 가짐
  - store state,getters,actions으로 이루어지며 각각 ref(), computed(), function()과 동일함

### 4.Pinia 구성 요소 활용

- State
  - store 인스턴스로 state에 접근하여 직접 읽고 쓸 수 있음
  - 만약 store에 state를 정의하지 않았다면 컴포넌트에서 새로 추가할 수 없음
    - store에서 정의하여 가져다가 쓸 수는 있는데 component에서 store로 추가를 할 수는 없다는 의미

- Getters
  - store의 모든 getters를 state 처럼 직접 접근 할 수 있음

- Actions
  - store의 모든 actions를 직접 접근 및 호출 할 수 있음
  - **getters와 달리 state 조작, 비동기, API 호출이나 다른 로직을 실행할 수 있음**

```vue
<template>
    <div>
      <p>state : {{ store.count }}</p>
      <p>getters : {{ store.doubleCount }}</p>
      <button @click="store.increment()"> +++ </button>
    </div>
</template>


<script setup>
import { useCounterStore } from '@/stores/counter'

const store = useCounterStore()

// state 참조 및 변경
console.log(store.count)
const newNumber = store.count + 1

// getters 참조
console.log(store.doubleCount)

// actions 호출
// <button @click ="store.increment()"> 부분
</script>
```
![6](https://github.com/JeongJonggil/TIL/assets/139416006/da1e8f6f-e652-494b-b623-b2fcbeb5033d)

### 5. Pinia를 활용한 Todo 프로젝트 구현

- Todo CURD
![7](https://github.com/JeongJonggil/TIL/assets/139416006/52817dcc-e3d2-4161-b9c6-47889aef8ff5)

```javascript
# stores/counter.js

import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

export const useCounterStore = defineStore('counter', () => {
  let id = 0
  const todos = ref([
    {id: id++, text: 'todo 1', isDone: false},
    {id: id++, text: 'todo 2', isDone: false}
    ])

  const addTodo = function(todoText) {
    todos.value.push(
    {
      id: id++, 
      text: todoText,
      isDone: false
    }
    )}

  const deleteTodo = function(todoId) {
    const ItemIdx = todos.value.findIndex((item) => { return item.id === todoId} )    
    todos.value.splice(ItemIdx,1)
  }

  const updatedTodo = function(todoId) {
    todos.value = todos.value.map((Item) => {
      if(Item.id === todoId){
        Item.isDone = !Item.isDone
      }
      return Item
    })

  }

  return { todos,addTodo,deleteTodo,updatedTodo }
})
```

```vue
# App.vue
<template>
    <div>
      <h1>Todo PJT</h1>
      <TodoForm/>
      <TodoList/>
    </div>
</template>

<script setup>
import { useCounterStore } from './stores/counter';
import TodoForm from '@/components/TodoForm.vue'
import TodoList from '@/components/TodoList.vue'
const store = useCounterStore()
</script>
```

```vue
# TodoForm.vue

<template>
    <div>
      <form @submit.prevent="createTodo()">
        <input type="text" v-model="todoText">
        <input type="submit">
      </form>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { useCounterStore } from '@/stores/counter';

const todoText = ref('')
const store = useCounterStore()

const createTodo = function () {
  store.addTodo(todoText.value)
  todoText.value=''
}

</script>
```

```vue
# TodoList.vue

<template>
    <div>
      <TodoListItem 
      v-for="todo in store.todos"
      :key = "todo.id"
      :todo-data = "todo"
      />
    </div>
</template>

<script setup>
import TodoListItem from './TodoListItem.vue';
import { useCounterStore } from '@/stores/counter';

const store = useCounterStore()

</script>
```

```vue
#TodoListItem.vue

<template>
    <div>
      <span @click="store.updatedTodo(todoData.id)"> {{ todoData.text }} </span>
      <button @click="store.deleteTodo(todoData.id)">delete</button>
    </div>
</template>

<script setup>
import { useCounterStore } from '../stores/counter';

const store = useCounterStore()

defineProps({
  todoData:Object
})
</script>

```

