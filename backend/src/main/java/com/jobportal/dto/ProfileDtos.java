package com.jobportal.dto;
public final class ProfileDtos { private ProfileDtos(){}
 public record ProfileRequest(String phone,String location,String education,String skills,String experience,String resumeUrl){}
 public record ProfileResponse(Long id,String name,String email,String phone,String location,String education,String skills,String experience,String resumeUrl){}
}
