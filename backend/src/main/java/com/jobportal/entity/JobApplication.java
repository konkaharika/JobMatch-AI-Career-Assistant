package com.jobportal.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="job_applications", uniqueConstraints=@UniqueConstraint(columnNames={"job_id","candidate_id"}))
public class JobApplication {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false) @JoinColumn(name="job_id") private Job job;
 @ManyToOne(optional=false) @JoinColumn(name="candidate_id") private User candidate;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private ApplicationStatus status=ApplicationStatus.APPLIED;
 @Column(length=2000) private String coverLetter; private Instant appliedAt=Instant.now();
 public Long getId(){return id;} 
 public Job getJob(){return job;} 
 public void setJob(Job v){job=v;} 
 public User getCandidate(){return candidate;} 
 public void setCandidate(User v){candidate=v;}
 public ApplicationStatus getStatus(){return status;} 
 public void setStatus(ApplicationStatus v){status=v;} 
 public String getCoverLetter(){return coverLetter;} 
 public void setCoverLetter(String v){coverLetter=v;} 
 public Instant getAppliedAt(){return appliedAt;}
}
