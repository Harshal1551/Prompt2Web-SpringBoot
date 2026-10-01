package com.prompt2web.controller;

import com.prompt2web.service.OpenRouterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final OpenRouterService openRouterService;

    @PostMapping("/test")
    public Map<String, String> testAI(
            @RequestBody Map<String, String> request
    ) {

        String prompt = request.get("prompt");

        String response =
                openRouterService.generate(prompt);

        return Map.of(
                "response",
                response
        );
    }


    @PostMapping("/website-test")
    public Map<String, String> testWebsiteGeneration(
            @RequestBody Map<String, String> request
    ) {

        String prompt = request.get("prompt");

        String response =
                openRouterService.generateWebsite(prompt);

        return Map.of(
                "response",
                response
        );
    }



}