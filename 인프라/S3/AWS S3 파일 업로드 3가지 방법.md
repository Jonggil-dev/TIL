# AWS S3 파일 업로드 3가지 방법

### 출처 : [우아한기술블로그](https://techblog.woowahan.com/11392/)

- Stream, MultipartFile : Client에서 백엔드 서버로 파일을 전송하고, 백엔드 서버에서 AWS S3로 파일을 업로드 하는 방식
- AWS Multipart : 백엔드 서버에서 PresignedURL을 클라이언트에게 알려주고, 클라이언트가 AWS S3로 파일을 직접 업로드 하는 방식

![AWS S3 업로드 방식 3가지](https://github.com/Jonggil-dev/TIL/assets/155353613/3f684379-cdc5-44e3-a08d-ae074d129390)
