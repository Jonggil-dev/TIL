# Many to many relationships 1

### 1. Many to many relationships (N:M, M:N)

- 한 테이블의 0개 이상의 레코드가 다른 테이블의 0개 이상의 레코드와 관련된 경우
  - 양쪽 모두에서 N:1 관계를 가짐



### 2. 환자와 의사 2개의 모델을 사용하여 모델 구조 구상하기

![1](https://github.com/JeongJonggil/TIL/assets/139416006/81913a73-f1cd-4fd0-82c5-5217d82b990a)
![2](https://github.com/JeongJonggil/TIL/assets/139416006/27cddfa5-f035-46e6-a4b3-4ebbf6912f93)
![3](https://github.com/JeongJonggil/TIL/assets/139416006/d75a5abf-bc22-4d3b-b12e-0df9b0f0a033)
![4](https://github.com/JeongJonggil/TIL/assets/139416006/65c1f278-6def-451a-bda9-10040c7564d7)
![5](https://github.com/JeongJonggil/TIL/assets/139416006/9411bb6b-d41b-4642-b1c4-b62ab7c8f0c6)


### 

### 3. 중개 모델

![6](https://github.com/JeongJonggil/TIL/assets/139416006/0ea74842-d7f1-4083-a7fc-587a95b7a863)
![7](https://github.com/JeongJonggil/TIL/assets/139416006/fbacb696-044b-403f-8d92-ffa2df648566)
![8](https://github.com/JeongJonggil/TIL/assets/139416006/917d183c-dbff-451a-8837-f42fd53a5f9e)

### 4. ManyToManyField

- django에서는 위의 중개모델을 ManyToManyField로 자동으로 생성해줌(중개 테이블을 하나 만들어줌)
- N:M 관계에서는 ManyTomanyField를 한쪽에만 필드로 정의하는데 어디쪽에 생성할껀지는 설계자 마음임.

대신 추후 db조회 시 참조와 역참조의 관계를 잘 생각해서 생성해야됨

![9](https://github.com/JeongJonggil/TIL/assets/139416006/e9ba94a4-8388-48e5-ba58-20714351e25e)
![10](https://github.com/JeongJonggil/TIL/assets/139416006/3537ec5d-ba55-4d9a-8efd-8708ce748291)
![11](https://github.com/JeongJonggil/TIL/assets/139416006/8427a675-f6b6-4275-b695-b439ad02c8b4)
![12](https://github.com/JeongJonggil/TIL/assets/139416006/e00485ce-f10a-4f7f-96c9-c4f1e6640dab)
![13](https://github.com/JeongJonggil/TIL/assets/139416006/45ac0fc4-c852-4baf-b87c-8db7049bf666)
![14](https://github.com/JeongJonggil/TIL/assets/139416006/4280fb93-98e9-450f-8a7a-08aa4275a963)


### 5. 'through' argument

- 중개 테이블에 '추가 데이터'를 사용해 M:N 관계를 형성하려는 경우에 사용

![15](https://github.com/JeongJonggil/TIL/assets/139416006/2dbe4a63-57a0-4da6-a8c3-fa367bff4da5)
![16](https://github.com/JeongJonggil/TIL/assets/139416006/75fbd030-0e30-4351-9aa4-f3e4c65e798a)
![17](https://github.com/JeongJonggil/TIL/assets/139416006/001c9585-bdf5-45ba-86d3-7bfe08efa043)
![18](https://github.com/JeongJonggil/TIL/assets/139416006/5ff03627-50fc-43c3-a9fb-cc3dd1c06765)
![19](https://github.com/JeongJonggil/TIL/assets/139416006/1bf81af8-7db2-465d-b1e5-c5db75b78379)
![20](https://github.com/JeongJonggil/TIL/assets/139416006/70764b56-0ef7-4631-b4cd-00f35a86fb2a)

### 6. ManyToManyField arguments

- ManyToManyField(to, **options) : many to many관계 설정 시 사용하는 모델 필드

**(1) related_name : 역참조시 사용하는 manager name을 변경**

```python
ex)
class Patient(models.Model):
	doctors = models.ManyToManyField(Doctor, related_name='patients')

# 변경 전
docotor.patient_set.all()

# related_name 적용 후
docotor.patients.all()
```

**(2) symmetrical options : ManyToManyField가 동일한 모델을 가리키는 정의에서만 사용**

- 기본 값 : True
  - True일 경우
    - source 모델의 인스턴스가 target 모델의 인스턴스를 참조하면 자동으로 target 모델 인스턴스도 source모델 인스턴스를 자동으로 참조하도록 함(대칭)
    - 즉, 자동으로 내가 당신의 친구라면 당신도 내 친구가 됨
  - False일 경우
    - True 였을 때와 반대(대칭되지 않음)

```python
ex)

class Person(models.Model):
	friends = models.ManyToManyField('self')
	# friends = models.ManyToManyField('self',symmetrical=False)
```



### 7. 좋아요 기능  
- Article(M) - User(N)  
  - 게시글은 회원으로부터 0개 이상의 좋아요를 받을 수 있고, 회원은 0개 이상의 게시글에 좋아요를 누를 수 있음  
  
![21](https://github.com/JeongJonggil/TIL/assets/139416006/238745fd-e348-46b5-95c7-8f28d0d3c08e)
![22](https://github.com/JeongJonggil/TIL/assets/139416006/f10125ab-3012-4dee-990c-7d672d2e4580)
![23](https://github.com/JeongJonggil/TIL/assets/139416006/7179d532-f857-4f35-bdb6-47f1a86b5110)
![24](https://github.com/JeongJonggil/TIL/assets/139416006/49362132-3f45-49e2-8ebf-d45a869f36b1)
![25](https://github.com/JeongJonggil/TIL/assets/139416006/1d313ccd-06c3-410e-b96b-1b553a4f8e85)
![26](https://github.com/JeongJonggil/TIL/assets/139416006/3ed77baa-314d-43a4-abdd-c8d1d4ec6fda)


- **좋아요 기능 구현**
  
![27](https://github.com/JeongJonggil/TIL/assets/139416006/8e3c6d2e-cb93-4ae7-862a-e3d9042cb1e1)
![28](https://github.com/JeongJonggil/TIL/assets/139416006/b191ac98-6eec-43ac-8a43-3a6a91f861b6)
![29](https://github.com/JeongJonggil/TIL/assets/139416006/57a4648f-ba91-458b-9b26-894bd873a163)
![30](https://github.com/JeongJonggil/TIL/assets/139416006/b87656ad-f16c-4c89-9f93-519ff5158a4c)
![31](https://github.com/JeongJonggil/TIL/assets/139416006/c439aba0-65dc-4bec-ba25-b02d5d544a28)
