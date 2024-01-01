# 09_pjt (Vue를 활용한 장바구니 구현)

- 임시로 데이터를 저장할 공간
- 브라우저의 스토리지 중 Local storage 사용
- **Local storage**
  - **반영구적으로 브라우저에 저장할 수 있는 공간**
  - 브라우저를 껐다 켜도 데이터가 유지됨
  - 5MB 정도의 데이터를 저장할 수 있음 (쿠키보다 많은 데이터를 저장 할 수 있다)
  - 보안이 조금 위험 (최대한 위험 수준이 낮은 데이터만 저장 권장)
  - **value값은 문자열만 저장 가능**
    - setItem을 할 때는 value값을 `JSON.stringify`(json -> 문자열) 형태로 변경해서 저장
    - getItem을 할 때는 value값을  `JSON.parse`(문자열-> json) 형태로 변경해서 사용하기



- 사용법
  - localStorage는 따로 import 필요 없음
  - setItem : item을 key, value형태로 저장하는 메서드
  - getItem : 저장된 자료를 가져오는 메서드

```java
//localStorag에 데이터 저장하기
const addCart = (product) =>{
    //하나의 데이터만 저장하기
    //문제점 Local Storage에 데이터가 덮어쓰기가 됨(데이터가 append가 아니라 재할당이 되는 느낌)
    localStorage.setItem('cart',Json.stringify(product))	//key: cart, value:Json.stringify(product)
    
    //여러 데이터 저장하기
    //현재 localStorage에 저장된 데이터 가져오기
    //만약 없다면 비어있는 리스트로 초기화
    const exisitingCart = JSON.parse(localStorage.getItem('cart')) || []
    
    // 중복된 제품이 있는지 확인
    const isDuplicate = exisitingCart.length > 0 && existingCard.find((item) => item.id === product.id)
    //const isDuplicate = existingCart.value.includes(product.id) 해도 됨
    
    if(!isDuplicated){
      alert('장바구니에 추가합니다')
      existingCart.push(product)
    }else{
    	alert('이미 있는 상품입니다. 장바구니로 이동합니다')
    }
    
    //수정된 카트 데이터를 localStorage에 저장
    localStorage.setItem('cart',JSON.stringify(existingCart))
}


//localStorag에서 데이터 가져오기
const cartItems = ref(null)
const getCart == () => {
  cartItems.value = JSON.parse(localStorage.getItem('cart'))
}

//localStorag의 데이터 삭제하기 (현재 cartItems.value에서 삭제)
  const removeCart = (product) =>{
  //1. 현재 localStorage에 저장된 데이터를 가져오기
  const exisitingCart = JSON.parse(localStorage.getItem('cart'))
    
  //2. 삭제할 아이템 index 검색
  const itemIdx = existingCart.findIndex((item)=> item.id === product.id)

	//3. 데이터 삭제
  exisitingCart.splice(itemIdx,1)
  
  //4.삭제된 데이터를 기준으로 데이터를 반영
  localStorage.setItem('cart',JSON.stringify(existingCart))
  cartItems.value = existingCart
}
```