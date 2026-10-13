package com.jobportal.security;

import io.jsonwebtoken.*; 
import io.jsonwebtoken.security.Keys; 
import org.springframework.beans.factory.annotation.Value; 
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets; 
import javax.crypto.SecretKey; 
import java.time.Instant; 
import java.util.Date;

@Service
public class JwtService {
 private final SecretKey key; private final long expirationMs;
 public JwtService(@Value("${app.jwt.secret}") String secret,@Value("${app.jwt.expiration-ms:86400000}") long expirationMs){
  if(secret.length()<32) throw new IllegalArgumentException("JWT secret must contain at least 32 characters"); this.key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); this.expirationMs=expirationMs;
 }
 public String generate(String email){return Jwts.builder().subject(email).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis()+expirationMs)).signWith(key).compact();}
 public String extractEmail(String token){return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();}
 public boolean valid(String token,String email){try{return extractEmail(token).equals(email)&&Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getExpiration().after(Date.from(Instant.now()));}catch(Exception e){return false;}}
}
