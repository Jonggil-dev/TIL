# MongoDB - Spring 연동

1. MongoDB Atlas를 이용해 DB 생성 (프로젝트 1개까지 무료)

2. MongoDB Compass를 이용해 GUI로 관리

3. Spring Properties 작성

   ```yml
   spring:
     data:
       mongodb:
         uri: mongodb+srv://username:password@cluster0.q3hjp.mongodb.net/database?retryWrites=true&w=majority 
         #기본적으로 Compass 연결할 때 사용하는 주소에 ?retryWrites=true&w=majority 이부분만 추가하면 됨
         auto-index-creation: true #JPA를 통해 Index 생성 하려면 해당 설정 추가 해야됨
   ```

   