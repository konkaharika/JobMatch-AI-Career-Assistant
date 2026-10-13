
package com.jobportal.dto;

import jakarta.validation.constraints.*;

public final class JobDtos {
    private JobDtos() {}

    public record JobRequest(
        @NotBlank String title,
        @NotBlank String company,
        @NotBlank String description,
        @NotBlank String requiredSkills,
        String location,
        String employmentType,
        Integer minExperience,
        Double salaryMin,
        Double salaryMax
    ) {}

    public record JobResponse(
        Long id,
        String title,
        String company,
        String description,
        String requiredSkills,
        String location,
        String employmentType,
        Integer minExperience,
        Double salaryMin,
        Double salaryMax,
        Double matchScore
    ) {}

    public record ApplicationRequest(String coverLetter) {}

    public record ApplicationResponse(
        Long id,
        Long jobId,
        String title,
        String company,
        String status,
        String appliedAt
    ) {}
}
