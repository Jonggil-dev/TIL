package com.example.icecream.common.auth.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class DeviceLoginRequestDto {

    @NotBlank
    private String deviceId;

    private String refreshToken;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9\\-_:]{100,}$", message = "올바른 FCM 토큰 형식이 아닙니다.")
    private String fcmToken;
}
