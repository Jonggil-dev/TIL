# 08_pjt (Locust)

### 1. 성능 테스트

- 특정 상황에서 시스템이 어느 정도 수준을 보이는가 혹은 어떻게 대처를 하는가를 테스트 하는 과정
- 목적
  - 성능 저하가 발생하는 요인을 발견하고 제거
  - 시장에 출시되기 전에 발생할 수 있는 위험과 개선사항을 파악
  - 안정적이고 신뢰할 수 있는 제품을 빠르게 만들기 위함
  - 성능 테스트는 다양한 종류가 있음 ( 프로젝트에서는 핵심인 `부하테스트`와 `스트레스 테스트` 사용)



### 2. 부하 테스트 (Load Testing)

- 시스템 임계점의 부하가 계속될 때 문제가 없는가?
- 목적 : 시스템의 신뢰도와 성능을 측정
- 임계점 : 사용자 혹은 요청이 점점 늘어나다가, 응답 시간이 급격히 느려지는 시점
- ex) 임계점에 해당하는 인원이 30시간 동안 계속해서 사용 했을 때 성능 측정



### 3. 스트레스 테스트 (Stress Testing)

- 시스템에 과부하가 오면 어떻게 작동할까 ?
- 목적 : 장애 조치와 복구 절차가 효과적이고 효율적인지 확인
- ex) 임계점 이상의 인원이 서비스를 이용할 때 어떻게 대처하는가 ?

![1](https://github.com/JeongJonggil/TIL/assets/139416006/8aa623bd-edf3-4438-94bc-984b74d72539)



### 4. Locust

- 오픈 소스 부하 테스트 도구
- 번역하면 메뚜기. 테스트 중 메뚜기 떼가 웹 사이트를 공격한다는 의미로 착안된 이름
- 내가 만든 서버에 **수많은 사용자들이 동시에 들어올 때 어떤 일이 벌어 지**는 지를 확인하는 도구
- Locust를 선택한 이유
  - 파이썬 언어로 테스트 시나리오를 간편하게 작성할 수 있습니다.
  - 결과를 웹에서 확인할 수 있는 UI를 지원합니다.
![2](https://github.com/JeongJonggil/TIL/assets/139416006/eaf87401-86f5-4c0a-82cf-adb4a9bb7783)
![3](https://github.com/JeongJonggil/TIL/assets/139416006/c6828bce-4971-4bd0-8048-c4630489ac06)
![4](https://github.com/JeongJonggil/TIL/assets/139416006/d08a611f-2ea4-4e77-a879-5498ea5c5e00)
![5](https://github.com/JeongJonggil/TIL/assets/139416006/f7ff2d21-3926-430e-a730-baf7c25eb871)
![6](https://github.com/JeongJonggil/TIL/assets/139416006/15e3cebd-9cac-4114-a1bc-ecbdef0ff805)
![7](https://github.com/JeongJonggil/TIL/assets/139416006/19472448-a279-4c9a-9c37-f41e22cd3653)
![8](https://github.com/JeongJonggil/TIL/assets/139416006/ff11d423-b68c-49d1-bdd4-9c1fd98e555a)
![9](https://github.com/JeongJonggil/TIL/assets/139416006/f95fc189-786a-4fb3-9a5a-39da9538299c)
![10](https://github.com/JeongJonggil/TIL/assets/139416006/a9f1cf4f-2cb0-4ea9-a182-9def52de6237)
![11](https://github.com/JeongJonggil/TIL/assets/139416006/77980895-f79f-4da7-8c01-fee9594c46fe)
