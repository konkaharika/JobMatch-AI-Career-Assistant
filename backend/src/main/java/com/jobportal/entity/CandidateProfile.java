
package com.jobportal.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "candidate_profiles")
public class CandidateProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    private String phone;
    private String location;
    private String education;

    @Column(columnDefinition = "TEXT")
    private String skills;

    @Column(columnDefinition = "TEXT")
    private String experience;

    private String resumeUrl;

    @Column(columnDefinition = "TEXT")
    private String embeddingJson;

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User v) {
        user = v;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String v) {
        phone = v;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String v) {
        location = v;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String v) {
        education = v;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String v) {
        skills = v;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String v) {
        experience = v;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String v) {
        resumeUrl = v;
    }

    public String getEmbeddingJson() {
        return embeddingJson;
    }

    public void setEmbeddingJson(String v) {
        embeddingJson = v;
    }
}
