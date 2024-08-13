package com.drive.sidepjt.domain.auth.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChildrenResponseDto {
    private final int userId;
    private final String profileImage;
    private final String username;
    private final String phoneNumber;
}
