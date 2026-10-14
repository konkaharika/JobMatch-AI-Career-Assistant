
package com.jobportal.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "recruiter_id")
    private User recruiter;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String company;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String requiredSkills;

    private String location;
    private String employmentType;
    private Integer minExperience;
    private Double salaryMin;
    private Double salaryMax;
    private Boolean active = true;

    @Column(columnDefinition = "TEXT")
    private String embeddingJson;

    private Instant createdAt = Instant.now();

    public Long getId() {
        return id;
    }

    public User getRecruiter() {
        return recruiter;
    }

    public void setRecruiter(User v) {
        recruiter = v;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String v) {
        title = v;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String v) {
        company = v;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String v) {
        description = v;
    }

    public String getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(String v) {
        requiredSkills = v;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String v) {
        location = v;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String v) {
        employmentType = v;
    }

    public Integer getMinExperience() {
        return minExperience;
    }

    public void setMinExperience(Integer v) {
        minExperience = v;
    }

    public Double getSalaryMin() {
        return salaryMin;
    }

    public void setSalaryMin(Double v) {
        salaryMin = v;
    }

    public Double getSalaryMax() {
        return salaryMax;
    }

    public void setSalaryMax(Double v) {
        salaryMax = v;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean v) {
        active = v;
    }

    public String getEmbeddingJson() {
        return embeddingJson;
    }

    public void setEmbeddingJson(String v) {
        embeddingJson = v;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
