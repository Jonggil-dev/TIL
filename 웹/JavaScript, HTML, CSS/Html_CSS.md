# 1. HTML

### (1) 용어

- Tag(태그) : 여는 꺽쇠와 닫는 꺽쇠 한 쌍(여는태그, 닫는태그 등)

- Contents(내용) : 여는 태그와 닫는 태그 사이에 들어가는 내용

- Elements(요소) : 여는 태그 + contetns + 닫는 태그를 합친 1part를

### (2) Block, Inline, Inline-Block Type

**1) Block Type(h1~6,p,div)**  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ㄱ. 줄바꿈이 발생한다(한 줄을 차지한다)  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ㄴ. width와 height 속성을 사용하여 너비와 높이를 지정할 수 있음(지정하지 않으면 너비는 한 줄을 차지함)  

**2) Inline Type(a, img, span)**    
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ㄱ. 줄바꿈이 발생하지 않는다(한줄을 차지하지 않는다)  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ㄴ. width와 height 속성을 사용할 수 없음  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ㄷ, padding,margins,border로 **수평 방향은 밀어낼 수 있으나, 수직 방향으로는 못 밀어냄**  

**3) Inline-Block Type**    
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**ㄱ. 요소가 줄 바꿈 되는 것을 원하지 않으면서 너비와 높이를 적용하고 싶은 경우 사용**  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ㄴ. style에서 display: inline-block;으로 지정함

---

# 2. CSS

### (1) 적용 방식

#### : 실무에서 선택자는 '클래스 선택자, 자손결합자'를 거의 90% 사용

- inline 스타일 : 태그 안에 style을 직접 정의 **--> 거의 안씀**

- internal(내부) 스타일 : Head 쪽에 style을 정의해서 사용

- external(외부) 스타일 : 외부 파일에 정의된 style을 link 태그로 땡겨와서 사용 
  **--> 현업에서 제일 많이 사용하는 방식**

### (2) 상속

#### : 암기할 필요는 없고 오류 발생 시 상속이 아닌지 찾아보기

- 상속 되는 속성 : Text 관련 요소(font,color,text-align)

- 상속 되지 않는 속성 : Box model, position 관련 요소  

---

# 3. CSS Layout

### ※ 사용빈도 flex > position > box model,  중요순위 box-model > flex > position

### (1) CSS Box Model

#### : 모든 HTML 요소를 사각형 박스로 표현하는 개념

  **-> 내용(content), 안쪽 여백(padding), 테두리(border), 외부 간격(margin)으로 구성되는 개념**  

<img title="" src="https://github.com/JeongJonggil/TIL/assets/139416006/ac8c59df-99df-4f06-8a99-e5e4cc26eda3" alt="image" width="716" data-align="left">

<img title="" src="https://github.com/JeongJonggil/TIL/assets/139416006/ed415a1f-20c2-466e-97fd-d0a647a70ee6" alt="image" width="734" data-align="left">

### (2) CSS Position

#### - 유형 : static, relative, absolute, fixed, sticky

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; **※ position : absolute -> 가장 가까운 부모 중"static"이 아닌 부모를 기준으로 움직임**

#### - 요소를 Normal Flow에서 제거하여 다른 위치로 배치하는 것

### (3) CSS Flexbox (일반적으로 가장 많이 이용하는 방식)

#### : 요소를 행과 열 형태로 배치하는 1차원 레이아웃 방식

    *Flexbox는 2가지만 기억하면 사용하기 쉬움
    1. main axis(주 축)을 찾아라
    2. 배치는 Flex Container를 조작해라 

### (4) CSS Grid

#### : 요소를 12개의 column으로 분배하여 배치하는 2차원 레이아웃 방식

    *Grid는 3가지만 기억하면 사용하기 쉬움
    1. 12개의 column을 적절히 분배하여 레이아웃을 배치하는 것
    2. 2차원 배열이기 때문에 flexbox에서 row가 추가된 세팅이라고 생각하면 쉬움
    3. flexbox와 비슷한 동작 방식임(container, row, column을 조작)
