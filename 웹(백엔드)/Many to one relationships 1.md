# Many to one relationships(N:1)

### 1. Many to one relationships(N:1)

- 한 테이블의 0개 이상의 레코드가 다른 테이블의 레코드 한 개와 관련된 관계
- Comments - Article(게시글 - 댓글, N:1 관계)
  -  0개 이상의 댓글이 1개의 게시글에 작성 될 수 있다.
- ForeignKey() 
  - N : 1 관계 설정 모델 필드

### 2. 게시글 댓글 만들기

​	(1)  댓글 모델 정의  

![12](https://github.com/JeongJonggil/TIL/assets/139416006/a5919c8d-bd7c-4cce-9e78-e4492baf82e9)

> ForeignKey(to, on _delete)
>
> - to : 참조하는 모델 calss 이름
> - on_delete : 외래 키가 참조하는 객체(1)가 사라졌을 때, 외래 키를 가진 객체(N)를 어떻게 처리할 지를 정의하는 설정(데이터 무결성)
> - on_delte의 'CASCADE' 설정 : 부모 객체(참조 된 객체)가 삭제 됐을 때 이를 참조하는 객체도 삭제

​	(2) 댓글 생성 연습  

![19](https://github.com/JeongJonggil/TIL/assets/139416006/0da0e3f8-072a-4e0a-aad3-595ac0be822c)
![20](https://github.com/JeongJonggil/TIL/assets/139416006/d3f920ac-b170-4975-b34a-998b1738c72a)
![21](https://github.com/JeongJonggil/TIL/assets/139416006/bd61419b-ae47-48bf-aae9-6ecf447f4fc4)
![22](https://github.com/JeongJonggil/TIL/assets/139416006/40bdb28a-edaa-466f-8e22-a995ba577c54)
![23](https://github.com/JeongJonggil/TIL/assets/139416006/24d480de-efb5-44dc-b40f-e230f6f5a78f)
![24](https://github.com/JeongJonggil/TIL/assets/139416006/3d1e6e71-f61c-4a3b-98a4-3510dbf0922d)
![25](https://github.com/JeongJonggil/TIL/assets/139416006/9643d2ea-a0cc-428b-8b59-643c59ffba25)

### 3. 역참조

- N:1 관계에서 1에서 N을 참조하거나 조회하는 것 (1 → N)
- (ex) 해당 게시글에 작성된 모든 댓글을 조회
- N은 외래 키를 가지고 있어 물리적으로 참조가 가능하지만, 1은 N에 대한 참조 방법이 존재하지 않아 별도의 역참조 이름이 필요

```txt
#역참조 사용 예시
article.comment_set.all()
: 모델 인스턴스 - related manager(역참조 이름)- Queryset API
```

​	(1) related manager

​		● N:1 혹은 M:N 관계에서 역참조 시에 사용하는 매니저

​		●'objects' 매니저를 통해 queryset api를 사용했던 것처럼 realated manager를 통해 queryset api를 사용할 수 있게 됨

​	(2) related manager 이름 규칙	

​		● N:1 관계에서 생성되는 Related manager의 이름은 참조하는 "모델명_set"이름 규칙으로 만들어짐 

​		● 게시글의 댓글 목록(Aritlce → Comment)

​			: article.comment_set.all()

### 4. 댓글 CREATE 구현
![38](https://github.com/JeongJonggil/TIL/assets/139416006/923435d3-b32c-41e1-9eba-cb267ad08ade)
![39](https://github.com/JeongJonggil/TIL/assets/139416006/43c68c80-e468-4e8f-9b42-39a2ae9e48d9)
![40](https://github.com/JeongJonggil/TIL/assets/139416006/2778ec13-e248-4e0b-ab14-47d478c4c70b)
![41](https://github.com/JeongJonggil/TIL/assets/139416006/c64d9667-57f3-4556-b4c7-d6b62e87d508)
![42](https://github.com/JeongJonggil/TIL/assets/139416006/9fe54837-20e9-432c-b5d5-5d7709585c72)
![43](https://github.com/JeongJonggil/TIL/assets/139416006/2690af65-db9b-42cd-99b0-c2911a06a32c)
![44](https://github.com/JeongJonggil/TIL/assets/139416006/5b3fe03f-213d-4962-9c36-3f0e00aa6572)
![45](https://github.com/JeongJonggil/TIL/assets/139416006/5e177710-f0c2-4be9-a827-79279509f8d7)
![46](https://github.com/JeongJonggil/TIL/assets/139416006/5d1511b6-6139-445a-9c54-380c319fbec6)
![47](https://github.com/JeongJonggil/TIL/assets/139416006/9b88bcc8-41db-4c55-bb38-98e14f873626)
