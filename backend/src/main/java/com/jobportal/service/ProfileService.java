package com.jobportal.service;
import com.jobportal.dto.ProfileDtos.*; import com.jobportal.entity.*; import com.jobportal.repository.*; import org.springframework.stereotype.Service;
@Service public class ProfileService{
 private final UserRepository users; private final CandidateProfileRepository profiles; private final EmbeddingService embedding;
 public ProfileService(UserRepository u,CandidateProfileRepository p,EmbeddingService e){users=u;profiles=p;embedding=e;}
 public ProfileResponse get(String email){User u=users.findByEmail(email).orElseThrow();CandidateProfile p=profiles.findByUserId(u.getId()).orElseGet(()->{CandidateProfile x=new CandidateProfile();x.setUser(u);return x;});return dto(u,p);}
 public ProfileResponse save(String email,ProfileRequest r){User u=users.findByEmail(email).orElseThrow();CandidateProfile p=profiles.findByUserId(u.getId()).orElseGet(()->{CandidateProfile x=new CandidateProfile();x.setUser(u);return x;});p.setPhone(r.phone());p.setLocation(r.location());p.setEducation(r.education());p.setSkills(r.skills());p.setExperience(r.experience());p.setResumeUrl(r.resumeUrl());p.setEmbeddingJson(embedding.serialize(embedding.embed(String.join(" ",r.skills()==null?"":r.skills(),r.education()==null?"":r.education(),r.experience()==null?"":r.experience()))));profiles.save(p);return dto(u,p);}
 private ProfileResponse dto(User u,CandidateProfile p){return new ProfileResponse(p.getId(),u.getName(),u.getEmail(),p.getPhone(),p.getLocation(),p.getEducation(),p.getSkills(),p.getExperience(),p.getResumeUrl());}
}
