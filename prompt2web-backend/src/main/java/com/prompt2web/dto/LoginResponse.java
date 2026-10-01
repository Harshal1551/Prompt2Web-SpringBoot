package com.prompt2web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private String message;
    private String token;
    private String userId;
    private String name;
    private String email;
}