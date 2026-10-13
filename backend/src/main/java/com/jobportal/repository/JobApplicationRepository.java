package com.jobportal.repository;
import com.jobportal.entity.JobApplication; import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface JobApplicationRepository extends JpaRepository<JobApplication,Long>{ boolean existsByJobIdAndCandidateId(Long jobId,Long candidateId); List<JobApplication> findByCandidateIdOrderByAppliedAtDesc(Long candidateId); List<JobApplication> findByJobRecruiterIdOrderByAppliedAtDesc(Long recruiterId); }
