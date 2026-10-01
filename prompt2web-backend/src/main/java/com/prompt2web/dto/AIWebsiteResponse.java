package com.prompt2web.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AIWebsiteResponse {

    private List<AIGeneratedFile> files;
}