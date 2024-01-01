# 07_pjt

### 1. Serializer

- serializer는 API에서 받은 JSON 데이터를 Django 모델 인스턴스로 변환할 때, DRF의 serialize `fields` 옵션에 정의된 필드만을 사용. 이는 자동으로 필터링을 수행하는 효과.

- read_only_field : 서버측에서는 데이터 조작(저장,수정) 가능, 클라이언트 측에서 데이터 조작하는것 을 방지하기 위함
