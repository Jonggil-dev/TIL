package com.drive.sidepjt.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class RefreshTokenDto {

    @NotNull
    private Integer userId;

    @NotBlank
    private String refreshToken;
}
