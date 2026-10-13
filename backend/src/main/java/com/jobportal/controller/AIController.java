package com.jobportal.controller;
import com.jobportal.dto.ChatDtos.*; 
import com.jobportal.service.AIService; 
import jakarta.validation.Valid; 
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/ai") public class AIController{private final AIService s;public AIController(AIService s){this.s=s;}@PostMapping("/chat")public ChatResponse chat(@Valid @RequestBody ChatRequest r){return new ChatResponse(s.chat(r.message()));}}
