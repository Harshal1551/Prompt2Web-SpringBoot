package com.prompt2web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {

    private String id;
    private String name;
    private String email;
}