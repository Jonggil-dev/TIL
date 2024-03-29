package com.e102.simcheonge_server.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
    private int userID;
    private String userLoginId;
    private String userNickname;
    private String accessToken;
    private String refreshToken;
}
