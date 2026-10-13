package com.jobportal.controller;
import com.jobportal.dto.ProfileDtos.*; 
import com.jobportal.service.ProfileService; 
import jakarta.validation.Valid; 
import org.springframework.security.core.Authentication; 
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/profile") 
public class ProfileController{private final ProfileService s;public ProfileController(ProfileService s){this.s=s;}@GetMapping public ProfileResponse get(Authentication a){return s.get(a.getName());}@PutMapping public ProfileResponse save(Authentication a,@Valid @RequestBody ProfileRequest r){return s.save(a.getName(),r);}}
