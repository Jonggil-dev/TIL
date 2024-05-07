package com.example.icecream.common.auth.dto;

import com.example.icecream.domain.user.entity.User;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ParentLoginResponseDto implements LoginResponseDto {

    private String username;
    private String loginId;
    private String phoneNumber;
    private String profileImage;
    private List<User> children;
    private String accessToken;
    private String refreshToken;
}
