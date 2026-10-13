package com.jobportal.repository;
import com.jobportal.entity.CandidateProfile; import java.util.Optional; import org.springframework.data.jpa.repository.JpaRepository;
public interface CandidateProfileRepository extends JpaRepository<CandidateProfile,Long>{ Optional<CandidateProfile> findByUserId(Long userId); }
