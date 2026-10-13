package com.jobportal.dto;
public final class ChatDtos { private ChatDtos(){} public record ChatRequest(String message){} public record ChatResponse(String answer){} }
