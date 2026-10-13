package com.jobportal.service;
import com.jobportal.dto.AuthDtos.*; 
import com.jobportal.entity.*; 
import com.jobportal.repository.*; 
import com.jobportal.security.JwtService; 
import org.springframework.http.HttpStatus; 
import org.springframework.security.crypto.password.PasswordEncoder; 
import org.springframework.stereotype.Service; 
import org.springframework.web.server.ResponseStatusException;
@Service public class AuthService{
 private final UserRepository users; private final PasswordEncoder encoder; private final JwtService jwt; public AuthService(UserRepository u,PasswordEncoder e,JwtService j){users=u;encoder=e;jwt=j;}
 public AuthResponse register(RegisterRequest r){if(users.existsByEmail(r.email()))throw new ResponseStatusException(HttpStatus.CONFLICT,"Email already registered");User u=new User();u.setName(r.name());u.setEmail(r.email().toLowerCase().trim());u.setPassword(encoder.encode(r.password()));u.setRole(r.role()==null?Role.CANDIDATE:r.role()); if(u.getRole()==Role.ADMIN) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"ADMIN registration is disabled");users.save(u);return response(u);}
 public AuthResponse login(LoginRequest r){User u=users.findByEmail(r.email().toLowerCase().trim()).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid email or password"));if(!encoder.matches(r.password(),u.getPassword()))throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid email or password");return response(u);}
 private AuthResponse response(User u){return new AuthResponse(jwt.generate(u.getEmail()),u.getId(),u.getName(),u.getEmail(),u.getRole());}
}
