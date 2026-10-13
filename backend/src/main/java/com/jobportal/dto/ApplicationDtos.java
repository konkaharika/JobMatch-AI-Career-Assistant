
package com.jobportal.dto;

import com.jobportal.entity.ApplicationStatus;
import java.time.Instant;

public final class ApplicationDtos {

    private ApplicationDtos() {
    }

    public record ApplicationResponse(
            Long id,
            Long jobId,
            String title,
            String company,
            ApplicationStatus status,
            String coverLetter,
            Instant appliedAt
    ) {
    }
}
