package com.jobportal.repository;
import com.jobportal.entity.Job; import org.springframework.data.domain.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface JobRepository extends JpaRepository<Job,Long>{ Page<Job> findByActiveTrue(Pageable pageable); Page<Job> findByActiveTrueAndTitleContainingIgnoreCaseOrActiveTrueAndCompanyContainingIgnoreCase(String title,String company,Pageable pageable); }
