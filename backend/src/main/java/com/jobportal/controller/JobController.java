package com.jobportal.controller;
import com.jobportal.dto.JobDtos.*; 
import com.jobportal.dto.ApplicationDtos.ApplicationResponse; 
import com.jobportal.service.JobService; 
import jakarta.validation.Valid; 
import org.springframework.data.domain.Page; 
import org.springframework.security.core.Authentication; 
import org.springframework.web.bind.annotation.*; 
import java.util.List;
@RestController @RequestMapping("/api/jobs") public class JobController{private final JobService s;public JobController(JobService s){this.s=s;}@GetMapping public Page<JobResponse> list(@RequestParam(defaultValue="0")int page,@RequestParam(defaultValue="10")int size,@RequestParam(defaultValue="createdAt")String sort,@RequestParam(required=false)String keyword){return s.list(page,size,sort,keyword);}@GetMapping("/recommended")public Page<JobResponse> recommended(Authentication a,@RequestParam(defaultValue="0")int page,@RequestParam(defaultValue="10")int size){return s.recommended(a.getName(),page,size);}@PostMapping("/{id}/apply")public void apply(Authentication a,@PathVariable Long id,@RequestBody ApplicationRequest r){s.apply(a.getName(),id,r.coverLetter());}@GetMapping("/applications")public List<ApplicationResponse> applications(Authentication a){return s.myApplications(a.getName()).stream().map(x->new ApplicationResponse(x.getId(),x.getJob().getId(),x.getJob().getTitle(),x.getJob().getCompany(),x.getStatus(),x.getCoverLetter(),x.getAppliedAt())).toList();}}
