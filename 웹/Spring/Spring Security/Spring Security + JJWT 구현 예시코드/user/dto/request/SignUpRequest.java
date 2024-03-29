package com.e102.simcheonge_server.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {
    private String userLoginId;
    private String userPassword;
    private String userPasswordCheck;
    private String userNickname;
}
