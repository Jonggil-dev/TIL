# S3 - Spring Boot 연동

### 참고

- [S3 접근용 AWS 계정 엑세스 키 발급 참고 자료](https://gaeggu.tistory.com/33)
- [Spring Boot 연동하기 참고 자료](https://innovation123.tistory.com/197)

---

### 1. S3 버킷 생성 (AWS S3 권한 설정과 버킷 생성 TIL 참고)



### 2. S3 접근용 AWS 계정 엑세스 키 발급

1. 사용자 생성

   - S3에 접근하기 위해서는 IAM 사용자에게 S3 **접근 권한**을 주고, 엑세스 키를 만들어 **액세스 키**, **비밀 엑세스 키**를 통해 접근해야 함

   1. AWS console -> 보안 자격 증명 -> 엑세스 관리 > 사용자 > **사용자 생성** 클릭
   2. 사용자 이름 입력
   3. 직접 정책 연결 클릭 + **AmazonS3FullAccess** 선택
   4. 사용자 생성 클릭

2. 생성한 사용자 이름 클릭 

   1. 보안 자격 증명 탭-> 액세스키 만들기 클릭
   2. 사용사례는 적절한거나 아무거나 클릭
   3. 태그를 입력하고 엑세스 키 만들기 클릭

3.  생성된 공개키와 비밀키 .csv 파일로 받기

   **(생성 완료 화면이 아니면 비밀 엑세스 키를 볼 수 없기 때문에 .csv 파일로 받아두기)**

### 3. Spring Boot 연동 전 참고 사항

- Content-Type 메타데이터

  - 파일이 웹 브라우저에서 어떻게 처리될지를 결정하는 역할

  - Content-Type을 이상하게 작성하면, 브라우저가 해석을 하지 못해서 기본 동작인 다운로드 동작을 함.

    즉, URL 입력하면 파일이 다운로드가 됨
    (ex) image/.jpg : 다운로드,  image/jpg: 출력

- S3 버킷의 객체 소유권 설정이 ACL 비활성화 되어 있으면, Spring으로 이미지 업로드 할 때 "The bucket does not allow ACL"이 발생 할 수 있음 

### 4. Spring Boot 연동하기

1. 의존성 추가

   ```groovy
   implementation 'org.springframework.cloud:spring-cloud-starter-aws:2.2.6.RELEASE'
   ```

2. application.properties 설정 추가

   ```properties
   cloud.aws.credentials.accessKey={S3_ACCESS_KEY}
   cloud.aws.credentials.secretKey={S3_SECRET_KEY}
   cloud.aws.s3.bucketName={S3_BUCKET_NAME}
   cloud.aws.region.static={S3_REGION}
   cloud.aws.stack.auto-=false
   ```

3. S3Config 작성

   ```java
   package com.example.icecream.domain.user.config;
   
   import com.amazonaws.auth.AWSCredentials;
   import com.amazonaws.auth.AWSStaticCredentialsProvider;
   import com.amazonaws.auth.BasicAWSCredentials;
   import com.amazonaws.services.s3.AmazonS3;
   import com.amazonaws.services.s3.AmazonS3ClientBuilder;
   import org.springframework.beans.factory.annotation.Value;
   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.Configuration;
   
   @Configuration
   public class S3Config {
       @Value("${cloud.aws.credentials.accessKey}")
       private String accessKey;
       @Value("${cloud.aws.credentials.secretKey}")
       private String secretKey;
       @Value("${cloud.aws.region.static}")
       private String region;
   
       @Bean
       public AmazonS3 amazonS3() {
           AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
   
           return AmazonS3ClientBuilder
                   .standard()
                   .withCredentials(new AWSStaticCredentialsProvider(credentials))
                   .withRegion(region)
                   .build();
       }
   
   }
   ```

4. S3ImageService 작성

   ```java
   package com.example.icecream.domain.user.s3.service;
   
   import com.amazonaws.services.s3.AmazonS3;
   import com.amazonaws.services.s3.model.CannedAccessControlList;
   import com.amazonaws.services.s3.model.DeleteObjectRequest;
   import com.amazonaws.services.s3.model.ObjectMetadata;
   import com.amazonaws.services.s3.model.PutObjectRequest;
   import com.amazonaws.util.IOUtils;
   
   import java.io.*;
   import java.net.MalformedURLException;
   import java.net.URL;
   import java.net.URLDecoder;
   import java.nio.charset.StandardCharsets;
   import java.util.*;
   
   import com.example.icecream.common.exception.BadRequestException;
   import com.example.icecream.common.exception.InternalServerException;
   import com.example.icecream.common.exception.NotFoundException;
   import com.example.icecream.domain.user.entity.User;
   import com.example.icecream.domain.user.error.UserErrorCode;
   import com.example.icecream.domain.user.repository.UserRepository;
   import com.example.icecream.domain.user.s3.error.S3ErrorCode;
   import com.example.icecream.domain.user.util.UserValidationUtils;
   import jakarta.transaction.Transactional;
   import lombok.RequiredArgsConstructor;
   import lombok.extern.slf4j.Slf4j;
   import org.springframework.beans.factory.annotation.Value;
   import org.springframework.stereotype.Component;
   import org.springframework.web.multipart.MultipartFile;
   
   @Slf4j
   @RequiredArgsConstructor
   @Component
   public class S3ImageService {
   
       private final AmazonS3 amazonS3;
       private final UserRepository userRepository;
       private final UserValidationUtils userValidationUtils;
   
       @Value("${cloud.aws.s3.bucketName}")
       private String bucketName;
   
       @Transactional
       public String uploadImage(MultipartFile image, int userId, int parentId) {
           User user = userRepository.findById(userId)
                   .orElseThrow(() -> new NotFoundException(UserErrorCode.USER_NOT_FOUND.getMessage()));
   
           if(!user.getIsParent()){
               userValidationUtils.isValidChild(parentId, userId);
           }
   
           if(image.isEmpty() || Objects.isNull(image.getOriginalFilename())){
               throw new BadRequestException(S3ErrorCode.Image_NOT_FOUND.getMessage());
           }
   
           this.validateImageFileExtention(image.getOriginalFilename());
   
           String newImageUrl = null;
   
           try {
               newImageUrl = this.uploadImageToS3(image);
               deleteImageFromS3(userId, null);
               user.updateProfileImage(newImageUrl);
               userRepository.save(user);
               return newImageUrl;
           } catch (InternalServerException e) {
               if (newImageUrl != null) {
                   try {
                       amazonS3.deleteObject(new DeleteObjectRequest(bucketName, newImageUrl));
                   } catch (Exception s3Exception) {
                       throw new InternalServerException(S3ErrorCode.UPLOAD_ROLLBACK_ERROR.getMessage());
                   }
               }
               throw e;
           } catch (Exception e) {
               throw new InternalServerException(S3ErrorCode.EXCEPTION_ON_IMAGE_UPLOAD.getMessage());
           }
       }
   
   
       private String uploadImageToS3(MultipartFile image) throws IOException {
           String originalFilename = image.getOriginalFilename();
           String extention = Objects.requireNonNull(originalFilename).substring(originalFilename.lastIndexOf(".") + 1);
           String s3FileName = UUID.randomUUID().toString().substring(0, 10) + originalFilename;
           InputStream is = image.getInputStream();
           byte[] bytes = IOUtils.toByteArray(is);
           ObjectMetadata metadata = new ObjectMetadata();
           metadata.setContentType("image/" + extention);
           metadata.setContentLength(bytes.length);
           ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
   
           try{
               PutObjectRequest putObjectRequest =
                       new PutObjectRequest(bucketName, s3FileName, byteArrayInputStream, metadata)
                               .withCannedAcl(CannedAccessControlList.PublicRead);
               amazonS3.putObject(putObjectRequest);
           }catch (Exception e){
               throw new InternalServerException(S3ErrorCode.PUT_OBJECT_EXCEPTION.getMessage());
           }finally {
               byteArrayInputStream.close();
               is.close();
           }
   
           return amazonS3.getUrl(bucketName, s3FileName).toString();
       }
   
       public void deleteImageFromS3(int userId, Integer parentId){
           User user = userRepository.findById(userId)
                   .orElseThrow(() -> new NotFoundException(UserErrorCode.USER_NOT_FOUND.getMessage()));
   
           if(!user.getIsParent() && parentId != null){
               userValidationUtils.isValidChild(parentId, userId);
           }
   
           Optional.ofNullable(user.getProfileImage()).ifPresent(profileImage -> {
               String key = getKeyFromImageAddress(profileImage);
               try {
                   amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
               } catch (Exception e) {
                   throw new InternalServerException(S3ErrorCode.IO_EXCEPTION_ON_IMAGE_DELETE.getMessage());
               }
           });
       }
   
       private String getKeyFromImageAddress(String imageAddress){
           try{
               URL url = new URL(imageAddress);
               String decodingKey = URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8);
               return decodingKey.substring(1); // 맨 앞의 '/' 제거
           }catch (MalformedURLException e){
               throw new InternalServerException(S3ErrorCode.IO_EXCEPTION_ON_IMAGE_DELETE.getMessage());
           }
       }
   
       private void validateImageFileExtention(String filename) {
           int lastDotIndex = filename.lastIndexOf(".");
           if (lastDotIndex == -1) {
               throw new BadRequestException(S3ErrorCode.NO_FILE_EXTENTION.getMessage());
           }
   
           String extention = filename.substring(lastDotIndex + 1).toLowerCase();
           List<String> allowedExtentionList = Arrays.asList("jpg", "jpeg", "png", "gif");
   
           if (!allowedExtentionList.contains(extention)) {
               throw new BadRequestException(S3ErrorCode.INVALID_FILE_EXTENTION.getMessage());
           }
       }
   }
   ```

5. S3관련 Controller 작성

   ```java
    @PostMapping("/profile")
       public ResponseEntity<ApiResponseDto<String>> updateProfile(@RequestPart(value = "profile_image", required = false) MultipartFile profile,
                                                                   @RequestParam("user_id") @NotNull Integer userId,
                                                                   @AuthenticationPrincipal UserDetails userdetails){
           String profileImage = s3ImageService.uploadImage(profile, userId, Integer.parseInt(userdetails.getUsername()));
           return ApiResponseDto.success("프로필 이미지가 등록/수정 되었습니다.", profileImage);
       }
   
       @DeleteMapping("/profile")
       public ResponseEntity<ApiResponseDto<String>> s3delete(@RequestParam("user_id") @NotNull Integer userId,
                                                              @AuthenticationPrincipal UserDetails userdetails){
           s3ImageService.deleteImageFromS3(userId, Integer.parseInt(userdetails.getUsername()));
           return ApiResponseDto.success("프로필 이미지가 삭제 되었습니다.");
       }
   ```