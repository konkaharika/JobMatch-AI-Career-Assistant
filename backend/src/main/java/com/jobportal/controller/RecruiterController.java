
package com.jobportal.controller;

import com.jobportal.dto.JobDtos.*;
import com.jobportal.entity.ApplicationStatus;
import com.jobportal.entity.JobApplication;
import com.jobportal.service.JobService;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recruiter/jobs")
public class RecruiterController {

    private final JobService s;

    public RecruiterController(JobService s) {
        this.s = s;
    }

    // Publish a job
    @PostMapping
    public JobResponse create(
            Authentication a,
            @Valid @RequestBody JobRequest r) {

        return s.create(a.getName(), r);
    }

    // Update a job
    @PutMapping("/{id}")
    public JobResponse update(
            Authentication a,
            @PathVariable Long id,
            @Valid @RequestBody JobRequest r) {

        return s.update(a.getName(), id, r);
    }

    // Delete a job
    @DeleteMapping("/{id}")
    public void delete(
            Authentication a,
            @PathVariable Long id) {

        s.delete(a.getName(), id);
    }

    // View applications for jobs posted by this recruiter
    @GetMapping("/applications")
    public List<JobApplication> applications(Authentication a) {

        return s.recruiterApplications(a.getName());
    }

    // Accept or reject a candidate's application
    @PutMapping("/applications/{id}/status")
    public JobApplication updateApplicationStatus(
            Authentication a,
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String statusValue = body.get("status");

        if (statusValue == null) {
            throw new IllegalArgumentException(
                    "Status is required"
            );
        }

        ApplicationStatus status;

        try {
            status = ApplicationStatus.valueOf(
                    statusValue.toUpperCase()
            );
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid status. Use SHORTLISTED or REJECTED."
            );
        }

        if (status != ApplicationStatus.SHORTLISTED
                && status != ApplicationStatus.REJECTED) {
            throw new IllegalArgumentException(
                    "Only SHORTLISTED or REJECTED status is allowed"
            );
        }

        return s.updateApplicationStatus(
                a.getName(),
                id,
                status
        );
    }
}
