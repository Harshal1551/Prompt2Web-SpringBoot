package com.prompt2web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PreviewResponse {

    private String message;
    private String url;
}