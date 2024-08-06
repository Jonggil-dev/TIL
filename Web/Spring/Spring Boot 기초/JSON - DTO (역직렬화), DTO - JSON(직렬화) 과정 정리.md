# JSON -> DTO (역직렬화), DTO -> JSON(직렬화) 과정 정리

### 1. JSON -> DTO (역직렬화)
- **request DTO에 기본 생성자만 있으면 request JSON body를 DTO에 매핑(역직렬화)이 가능함**
- Json을 DTO에 Mapping하는 과정은 Reflection을 이용함.  Reflection은 기본 생성자를 필요로 하고 Reflection을 통해 필드에 직접 접근이 가능함으로 Setter 메서드가 필요 없음. 

### 2. DTO -> JSON(직렬화) 과정
- **response DTO에 Getter만 있으면 response JSON body에 매핑(직렬화)이 가능함**
- DTO 객체를 JSON 데이터로 변환하는 과정은 Reflection을 통해 객체의 필드에 직접 접근하는 것이 아니라, getter를 통해 값을 가져오기 때문. 즉, 기본 생성자는 이 과정에서 필요하지 않음
