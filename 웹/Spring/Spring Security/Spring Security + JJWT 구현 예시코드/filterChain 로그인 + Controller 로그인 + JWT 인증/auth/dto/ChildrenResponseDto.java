package com.example.icecream.domain.user.auth.dto;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChildrenResponseDto {
    private final int user_id;
    private final String profile_image;
    private final String username;
    private final String phone_number;
}
