
package com.jobportal.service;

import com.jobportal.dto.JobDtos.*;
import com.jobportal.entity.*;
import com.jobportal.repository.*;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

import java.util.*;

@Service
public class JobService {

    private final JobRepository jobs;
    private final UserRepository users;
    private final CandidateProfileRepository profiles;
    private final EmbeddingService embedding;
    private final JobApplicationRepository apps;

    public JobService(
            JobRepository j,
            UserRepository u,
            CandidateProfileRepository p,
            EmbeddingService e,
            JobApplicationRepository a) {
        jobs = j;
        users = u;
        profiles = p;
        embedding = e;
        apps = a;
    }

    // Get applications received by the logged-in recruiter
    public List<JobApplication> recruiterApplications(String email) {
        User recruiter = users.findByEmail(email).orElseThrow();

        return apps.findByJobRecruiterIdOrderByAppliedAtDesc(
                recruiter.getId()
        );
    }

    // Update an application's status to SHORTLISTED or REJECTED
    public JobApplication updateApplicationStatus(
            String email,
            Long applicationId,
            ApplicationStatus status) {

        User recruiter = users.findByEmail(email).orElseThrow();

        JobApplication application = apps.findById(applicationId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Application not found"
                        ));

        // Verify that the application belongs to this recruiter's job
        if (!application.getJob().getRecruiter()
                .getId().equals(recruiter.getId())) {
            throw new AccessDeniedException(
                    "You cannot update this application"
            );
        }

        // Allow only Accept or Reject statuses
        if (status != ApplicationStatus.SHORTLISTED
                && status != ApplicationStatus.REJECTED) {
            throw new IllegalArgumentException(
                    "Status must be SHORTLISTED or REJECTED"
            );
        }

        application.setStatus(status);

        return apps.save(application);
    }

    public Page<JobResponse> list(
            int page, int size, String sort, String keyword) {

        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(size, 50),
                Sort.by(sort == null ? "createdAt" : sort).descending()
        );

        Page<Job> result =
                (keyword == null || keyword.isBlank())
                ? jobs.findByActiveTrue(pageable)
                : jobs.findByActiveTrueAndTitleContainingIgnoreCaseOrActiveTrueAndCompanyContainingIgnoreCase(
                        keyword, keyword, pageable
                );

        return result.map(j -> dto(j, null));
    }

    public Page<JobResponse> recommended(
            String email, int page, int size) {

        User user = users.findByEmail(email).orElseThrow();

        CandidateProfile profile =
                profiles.findByUserId(user.getId()).orElse(null);

        double[] candidateVector = embedding.embed(
                profile == null
                ? ""
                : String.join(
                        " ",
                        Objects.toString(profile.getSkills(), ""),
                        Objects.toString(profile.getEducation(), ""),
                        Objects.toString(profile.getExperience(), "")
                )
        );

        List<JobResponse> all = jobs.findByActiveTrue(
                        PageRequest.of(
                                0, 100,
                                Sort.by("createdAt").descending()
                        )
                )
                .getContent()
                .stream()
                .map(job -> dto(
                        job,
                        embedding.similarity(
                                candidateVector,
                                embedding.embed(
                                        job.getTitle() + " "
                                        + job.getDescription() + " "
                                        + job.getRequiredSkills()
                                )
                        )
                ))
                .sorted(
                        Comparator.comparing(
                                JobResponse::matchScore
                        ).reversed()
                )
                .toList();

        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());

        return new PageImpl<>(
                all.subList(from, to),
                PageRequest.of(page, size),
                all.size()
        );
    }

    @CacheEvict(value = "jobs", allEntries = true)
    public JobResponse create(String email, JobRequest request) {

        User recruiter = users.findByEmail(email).orElseThrow();

        Job job = new Job();
        job.setRecruiter(recruiter);

        apply(job, request);

        job.setEmbeddingJson(
                embedding.serialize(
                        embedding.embed(
                                request.title() + " "
                                + request.description() + " "
                                + request.requiredSkills()
                        )
                )
        );

        jobs.save(job);

        return dto(job, null);
    }

    @CacheEvict(value = "jobs", allEntries = true)
    public JobResponse update(
            String email, Long id, JobRequest request) {

        Job job = jobs.findById(id).orElseThrow();

        checkOwner(email, job);
        apply(job, request);

        job.setEmbeddingJson(
                embedding.serialize(
                        embedding.embed(
                                request.title() + " "
                                + request.description() + " "
                                + request.requiredSkills()
                        )
                )
        );

        jobs.save(job);

        return dto(job, null);
    }

    @CacheEvict(value = "jobs", allEntries = true)
    public void delete(String email, Long id) {

        Job job = jobs.findById(id).orElseThrow();

        checkOwner(email, job);

        job.setActive(false);
        jobs.save(job);
    }

    public void apply(String email, Long jobId, String cover) {

        User user = users.findByEmail(email).orElseThrow();

        if (apps.existsByJobIdAndCandidateId(jobId, user.getId())) {
            throw new IllegalStateException(
                    "You already applied to this job"
            );
        }

        JobApplication application = new JobApplication();

        application.setJob(jobs.findById(jobId).orElseThrow());
        application.setCandidate(user);
        application.setCoverLetter(cover);

        apps.save(application);
    }

    public List<JobApplication> myApplications(String email) {

        User user = users.findByEmail(email).orElseThrow();

        return apps.findByCandidateIdOrderByAppliedAtDesc(
                user.getId()
        );
    }

    @Async
    public void recalculateEmbeddingAsync(Long jobId) {

        jobs.findById(jobId).ifPresent(job -> {
            job.setEmbeddingJson(
                    embedding.serialize(
                            embedding.embed(
                                    job.getTitle() + " "
                                    + job.getDescription() + " "
                                    + job.getRequiredSkills()
                            )
                    )
            );

            jobs.save(job);
        });
    }

    private void checkOwner(String email, Job job) {

        if (!job.getRecruiter().getEmail().equals(email)) {
            throw new AccessDeniedException("Not your job");
        }
    }

    private void apply(Job job, JobRequest request) {

        job.setTitle(request.title());
        job.setCompany(request.company());
        job.setDescription(request.description());
        job.setRequiredSkills(request.requiredSkills());
        job.setLocation(request.location());
        job.setEmploymentType(request.employmentType());
        job.setMinExperience(request.minExperience());
        job.setSalaryMin(request.salaryMin());
        job.setSalaryMax(request.salaryMax());
    }

    private JobResponse dto(Job job, Double score) {

        return new JobResponse(
                job.getId(),
                job.getTitle(),
                job.getCompany(),
                job.getDescription(),
                job.getRequiredSkills(),
                job.getLocation(),
                job.getEmploymentType(),
                job.getMinExperience(),
                job.getSalaryMin(),
                job.getSalaryMax(),
                score
        );
    }
}
