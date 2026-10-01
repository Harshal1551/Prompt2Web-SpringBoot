package com.prompt2web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AIGeneratedFile {

    private String filePath;

    private String fileName;

    private String language;

    private String content;
}