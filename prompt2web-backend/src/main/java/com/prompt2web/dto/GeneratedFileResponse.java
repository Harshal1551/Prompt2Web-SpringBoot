package com.prompt2web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeneratedFileResponse {

    private String filePath;
    private String fileName;
    private String language;
    private String content;
}