package com.jobportal.config;
import com.jobportal.security.JwtAuthenticationFilter; 
import org.springframework.context.annotation.*; 
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; 
import org.springframework.security.config.annotation.web.builders.HttpSecurity; 
import org.springframework.security.config.http.SessionCreationPolicy; 
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; 
import org.springframework.security.crypto.password.PasswordEncoder; 
import org.springframework.security.web.SecurityFilterChain; 
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; 
import org.springframework.web.cors.*; 
import java.util.List;
@Configuration @EnableMethodSecurity
public class SecurityConfig{
 private final JwtAuthenticationFilter jwt; public SecurityConfig(JwtAuthenticationFilter jwt){this.jwt=jwt;}
 @Bean SecurityFilterChain filterChain(HttpSecurity http)throws Exception{return http.csrf(c->c.disable()).cors(c->c.configurationSource(cors())).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).authorizeHttpRequests(a->a.requestMatchers("/auth/**","/swagger-ui/**","/v3/api-docs/**","/actuator/health").permitAll().requestMatchers("/api/recruiter/**").hasAnyRole("RECRUITER","ADMIN").requestMatchers("/api/admin/**").hasRole("ADMIN").anyRequest().authenticated()).addFilterBefore(jwt,UsernamePasswordAuthenticationFilter.class).build();}
 @Bean CorsConfigurationSource cors(){CorsConfiguration c=new CorsConfiguration();c.setAllowedOrigins(List.of("http://localhost:5173","http://localhost:4173"));c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));c.setAllowedHeaders(List.of("*"));c.setAllowCredentials(true);UrlBasedCorsConfigurationSource s=new UrlBasedCorsConfigurationSource();s.registerCorsConfiguration("/**",c);return s;}
 @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder(12);}
}
