# Component State Flow

### 1. Passing Props

- **부모는 자식에게 데이터를 전달(Pass Props)하며, 자식은 자신에게 일어난 일을 부모에게 알림(Emit event)**

- Props

  - 부모 컴포넌트로부터 자식 컴포넌트로 데이터를 전달하는 속성
  - 자식 컴포넌트에서 부모에게 받은 props를 변경하는 것은 불가능 함 (데이터의 수정은 넘겨주는 부모 컴포넌트에서 작업해야함)
  - One-Way Data Flow
    - 모든 props는 자식 속성과 부모 속성 사이에 하향식 단방향 바인딩을 형성
    - 데이터의 흐름을 이해하기 어렵게 만드는 것을 방지하기 위해 단방향 바인딩 방식을 사용

- Props 사용법

  - 주의사항

    - 부모 컴포넌트에서 보낸 props를 사용하기 위해서는 자식 컴포넌트에서 명시적인 props선언이 필요 **(부모에서 내려준다고 바로 사용 못함, 자식에서 선언 후에 사용해야 됨)**
    - 자바 스크립트는 카멜케이스, html은 케밥케이스를 사용하기 때문에 작성되는 곳의 언어의 타입에 맞춰서 속성명/변수명을 작성해주어야 함
    - Props는 바로 직계 자식에게 밖에 전달하지 못함. 자손에게 보내기 위해서는 자식에서 선언 후 v-bind로 속성을 만들어 넘겨주어야 됨

    ```vue
    1. Parent.vue
    #부모 컴포넌트에서 자식 컴포넌트 태그에 넘겨줄 데이터를 속성으로 작성
    <template>
        <div>
            <ParentChild my-msg="message"/> #my-msg는 그냥 임의의 명칭
        </div>
    </template>
    
    2. ParentChild.vue
    #defineProps로 자식 컴포넌트에서는 Props를 받아서 선언한 후에 사용해야됨
    
    <template>
        <div>
            <ParentGrandChild :my-msg="props.myMsg"/> //하기 <script>3번 방법 이용
        </div>
    </template>
    
    <script setup>
    //1. 문자열 배열 선언 방법 -> 유효성 검사로 인해 객체 선언 방법을 권장함
    defineProps(['myMsg'])
      
    //2. 객체를 사용한 선언 방법 (value에 데이터 타입을 작성해야 함 -> 유효성 검사에 사용됨)
    defineProps({
    	myMsg: String,
    })
    
    //3. props 데이터를 script에서 사용하려면 defineProps로 return 받아서 사용해야 됨
      const props = defineProps({
        myMsg: String
      })
      console.log(props.myMsg)
    </script>
    
    
    3. ParentGrandChild.vue
    <template>
        <div>
            {{ myMsg }}
        </div>
    </template>
    
    <script setup>
    defineProps({
        myMsg: String
    })
    </script>
    
    ```

### 2.Component Events

- 자식은 자신에게 일어난 일을 부모에게 알림(Emit event) -> **부모가 prop 데이터를 변경하도록 소리쳐야 한다**

- $emit(커스텀 이벤트 이름, ...추가 인자)

  - 자식 컴포넌트가 이벤트를 발생시켜 부모 컴포넌트로 데이터를 전달하는 역할의 메서드
  - '$' 표기는 Vue 인스턴스나 컴포넌트 내에서 제공되는 전역 속성이나 메서드를 식별하기 위한 접두어

  ```vue
  예시1 - inline방식(권장X)
  #ParentChild.vue
  <template>
      <div>
          <button @click="$emit('someEvent')">클릭</button>
      </div>
  </template>
  
  #Parent.vue
  <template>
      <div>
          <ParentChild 
              @some-event="someCallback"
              />
      </div>
  </template>
  <script setup>
  import ParentChild from '@/components/ParentChild.vue'
  const someCallback = function () {
      console.log("나이스 종길")
  }
  </script>
  ```

  ```vue
  예시2 - emit 선언 방식(권장)
  
  #ParentChild.vue
  <template>
      <div>
          <button @click="buttonClick">클릭</button>
      </div>
  </template>
  
  <script setup>
  import ParentGrandChild from '@/components/ParentGrandChild.vue'
  
  const emit = defineEmits(['someEvent'])
  const buttonClick = function(){
      emit('someEvent')
  }
  </script>
  
  #Parent.vue
  <template>
      <div>
          <ParentChild 
              @some-event="someCallback"
              />
      </div>
  </template>
  <script setup>
  import ParentChild from '@/components/ParentChild.vue'
  const someCallback = function () {
      console.log("나이스 종길")
  }
  </script>
  ```

- emit 이벤트 선언

  - defineEmits()를 사용하여 명시적으로 발신할 이벤트를 선언할 수 있음
  - **script에서 $emit 메서드를 접근할 수 없기 때문에 defineEmits()는 $emit 대신 사용할 수 있는 동등한 함수를 반환**

  ```vue
  <script setup>
  const emit = defineEmits(['somEvent','myFocus'])
  // defineEmits 함수를 사용하는 것은 컴포넌트에서 발생시킬 수 있는 이벤트의 "목록"을 만드는 것과 같음
  // defineEmits의 인자를 비워도 정상동작함 (권장 X, 인자는 유효성 검사시에 필요함)
  
  const buttonClick = function(){
  	emit('someEvent')
    // emit 함수는 실제로 그 이벤트를 "주문"하는 것과 같음. 부모 컴포넌트로 보내고 싶은 이벤트 이름을 emit 함수에 전달해서 "이 이벤트를 발생시켜주세요"라고 하는 것. 즉 emit('myFoucus')도 가능함
  }
  </script>
  ```

- emit으로 인자 전달하기

  - emit('someEvent', [1,2,3])으로 부모 요소에 전달하면 부모 요소에서 해당 event에 대한 콜백함수 정의시에 함수의 인자로 받아짐 
