
package com.jobportal.service;

import com.jobportal.entity.Job;
import com.jobportal.repository.JobRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AIService {

    private final JobRepository jobs;

    public AIService(JobRepository jobs) {
        this.jobs = jobs;
    }

    private List<Job> getAllActiveJobs() {
        List<Job> allJobs = new ArrayList<>();
        int pageNumber = 0;
        Page<Job> page;

        do {
            Pageable pageable = PageRequest.of(
                    pageNumber,
                    100,
                    Sort.by(Sort.Direction.DESC, "createdAt")
            );

            page = jobs.findByActiveTrue(pageable);
            allJobs.addAll(page.getContent());
            pageNumber++;

        } while (page.hasNext());

        return allJobs;
    }

    public String chat(String question) {

        if (question == null || question.isBlank()) {
            return "Please type a question about jobs, skills, "
                    + "career guidance, resumes, or interviews.";
        }

        String q = question.toLowerCase(Locale.ROOT).trim();

        // Greetings
        if (q.matches("(?s).*\\b(hello|hi|hey)\\b.*")
                || q.equals("good morning")
                || q.equals("good afternoon")
                || q.equals("good evening")) {
            return "Hello! Welcome to JobMatch Assistant.\n\n"
                    + "You can ask me to:\n"
                    + "1. Show all jobs\n"
                    + "2. Search jobs by company\n"
                    + "3. Search jobs by location\n"
                    + "4. List required skills\n"
                    + "5. Recommend jobs based on your skills\n\n"
                    + "Try: Show all jobs available in JobMatch.";
        }

        if (q.contains("how are you")) {
            return "I'm doing well! I can help you explore jobs "
                    + "and improve your career.";
        }

        if (q.contains("thank")) {
            return "You're welcome! Ask me anytime about jobs, "
                    + "skills, resumes, interviews, or careers.";
        }

        if (q.contains("who are you") || q.contains("your name")) {
            return "I am the JobMatch Assistant. I help you search "
                    + "active jobs and understand their required skills.";
        }

        if (q.contains("resume") || q.contains("cv")) {
            return "Resume tips:\n\n"
                    + "1. Keep your resume clear and concise.\n"
                    + "2. Include education, projects, and technical skills.\n"
                    + "3. Describe your project contributions accurately.\n"
                    + "4. Tailor your resume to the target role.\n"
                    + "5. Check spelling and formatting.";
        }

        if (q.contains("interview")) {
            return "Interview preparation tips:\n\n"
                    + "1. Research the company and role.\n"
                    + "2. Practise your self-introduction.\n"
                    + "3. Review the job's technical skills.\n"
                    + "4. Prepare to explain your projects.\n"
                    + "5. Practise role-related questions.";
        }

        // Explicit requests for all jobs.
        // "show jobs in Hyderabad" must NOT trigger this branch.
        boolean asksForAllJobs =
                q.contains("all jobs")
                || q.contains("all the jobs")
                || q.contains("all available jobs")
                || q.contains("every job")
                || q.contains("all openings")
                || q.contains("latest jobs")
                || q.equals("list jobs")
                || q.equals("show jobs")
                || q.equals("show all")
                || q.equals("list all");

        boolean asksForRequiredSkills =
                q.contains("required skills")
                || q.contains("required skill")
                || q.contains("skills are required")
                || q.contains("skills required")
                || q.contains("what skills")
                || q.contains("which skills")
                || q.contains("skills needed")
                || q.contains("skills for jobs")
                || q.contains("skills for job")
                || q.contains("job requirements")
                || q.contains("requirements for jobs");

        boolean asksForRecommendation =
                q.contains("suggest")
                || q.contains("recommend")
                || q.contains("suitable")
                || containsPhrase(q, "match")
                || containsPhrase(q, "matches")
                || q.contains("my skills")
                || q.contains("i know")
                || q.contains("i have")
                || q.contains("based on my skills")
                || q.contains("which job should")
                || q.contains("best job for me");

        boolean asksAboutJobs =
                q.contains("job")
                || q.contains("jobs")
                || q.contains("position")
                || q.contains("vacancy")
                || q.contains("vacancies")
                || q.contains("company")
                || q.contains("companies")
                || q.contains("employer")
                || q.contains("location")
                || q.contains("hiring")
                || q.contains("opening")
                || q.contains("openings")
                || q.contains("role")
                || q.contains("roles")
                || q.contains("requirement")
                || q.contains("requirements")
                || q.contains("skills");

        List<Job> activeJobs = null;

        if (asksAboutJobs || asksForRecommendation
                || asksForRequiredSkills) {
            activeJobs = getAllActiveJobs();
        }

        if (activeJobs != null && activeJobs.isEmpty()) {
            return "There are currently no active jobs listed in JobMatch.";
        }

        // 1. All jobs
        if (asksForAllJobs) {
            return formatJobs(
                    "All active jobs in JobMatch:",
                    activeJobs,
                    true
            );
        }

        // 2. Search by company name.
        // Example: "What jobs are available at TechNova Solutions?"
        if (activeJobs != null && asksAboutJobs) {

            List<Job> companyMatches = activeJobs.stream()
                    .filter(j -> j.getCompany() != null
                            && containsPhrase(q, j.getCompany()))
                    .collect(Collectors.toList());

            if (!companyMatches.isEmpty()) {
                return formatJobs(
                        "Jobs matching the company you asked about:",
                        companyMatches,
                        true
                );
            }

            // 3. Search by location.
            // Only return a location error if the question explicitly
            // asks about a location and no location matches.
            List<Job> locationMatches = activeJobs.stream()
                    .filter(j -> j.getLocation() != null
                            && containsPhrase(q, j.getLocation()))
                    .collect(Collectors.toList());

            if (!locationMatches.isEmpty()) {
                return formatJobs(
                        "Jobs available in the requested location:",
                        locationMatches,
                        true
                );
            }

            boolean explicitlyAsksLocation =
                    q.contains("location")
                    || q.contains("where")
                    || q.contains(" in ");

            if (explicitlyAsksLocation
                    && !asksForRequiredSkills
                    && (q.contains("job") || q.contains("jobs"))) {
                return "I couldn't find active jobs matching that "
                        + "location. Try Hyderabad, Bengaluru, Chennai, "
                        + "Pune, or Visakhapatnam.";
            }
        }

        // 4. Required skills.
        // This runs before recommendations and does not ask for
        // a location when the user asks about required skills.
        if (asksForRequiredSkills && !asksForRecommendation) {
            return formatSkillsOnly(
                    "Required skills for all active jobs:",
                    activeJobs
            );
        }

        // 5. Recommendations based on the user's skills.
        if (asksForRecommendation) {

            Set<String> knownSkills = new TreeSet<>(
                    String.CASE_INSENSITIVE_ORDER
            );

            for (Job job : activeJobs) {
                knownSkills.addAll(
                        extractSkills(job.getRequiredSkills())
                );
            }

            Set<String> userSkills = new TreeSet<>(
                    String.CASE_INSENSITIVE_ORDER
            );

            for (String skill : knownSkills) {
                if (containsPhrase(q, skill)) {
                    userSkills.add(skill);
                }
            }

            addAlias(userSkills, q, "SQL",
                    "mysql", "postgresql", "mariadb");
            addAlias(userSkills, q, "JavaScript", "js");
            addAlias(userSkills, q, "React", "react.js");
            addAlias(userSkills, q, "Spring Boot", "springboot");
            addAlias(userSkills, q, "C++", "cpp");

            if (userSkills.isEmpty()) {
                return "I can suggest jobs for you!\n\n"
                        + "Please tell me your skills, for example "
                        + "Java, SQL, Python, React, HTML, or CSS.\n\n"
                        + "Example: I know Java and SQL. "
                        + "Suggest jobs that match my skills.";
            }

            class Match {
                Job job;
                List<String> matched;
                List<String> missing;
                double score;

                Match(Job job, List<String> matched,
                      List<String> missing, double score) {
                    this.job = job;
                    this.matched = matched;
                    this.missing = missing;
                    this.score = score;
                }
            }

            List<Match> matches = new ArrayList<>();

            for (Job job : activeJobs) {
                List<String> required =
                        extractSkills(job.getRequiredSkills());

                List<String> matched = new ArrayList<>();
                List<String> missing = new ArrayList<>();

                for (String skill : required) {
                    if (hasEquivalentSkill(userSkills, skill)) {
                        matched.add(skill);
                    } else {
                        missing.add(skill);
                    }
                }

                double score = required.isEmpty()
                        ? 0.0
                        : 100.0 * matched.size() / required.size();

                matches.add(
                        new Match(job, matched, missing, score)
                );
            }

            matches.sort(
                    Comparator.comparingDouble(
                                    (Match m) -> m.score)
                            .reversed()
                            .thenComparing(m -> safe(m.job.getTitle()))
            );

            StringBuilder result = new StringBuilder();

            result.append("Your mentioned skills: ")
                    .append(String.join(", ", userSkills))
                    .append("\n\nJob recommendations:\n\n");

            for (Match match : matches) {
                Job job = match.job;

                result.append("Position: ")
                        .append(safe(job.getTitle()))
                        .append("\nCompany: ")
                        .append(safe(job.getCompany()))
                        .append("\nLocation: ")
                        .append(safe(job.getLocation()))
                        .append("\nRequired skills: ")
                        .append(safe(job.getRequiredSkills()))
                        .append("\nMatching skills: ")
                        .append(match.matched.isEmpty()
                                ? "No direct match identified"
                                : String.join(", ", match.matched))
                        .append("\nSkills to consider learning: ")
                        .append(match.missing.isEmpty()
                                ? "No additional listed skills"
                                : String.join(", ", match.missing))
                        .append("\nRequirement match: ")
                        .append(String.format(
                                Locale.ROOT, "%.0f%%", match.score))
                        .append("\n\n");
            }

            result.append("The percentage measures matching listed "
                    + "requirements. It is not a guarantee of getting hired.");

            return result.toString();
        }

        // 6. General technical and career guidance.
        if (q.contains("python")) {
            return "Python is useful for automation, data analysis, "
                    + "web development, and machine learning. Start with "
                    + "variables, conditions, loops, functions, and projects.";
        }

        if (q.contains("java")) {
            return "Java is used in backend and enterprise development. "
                    + "Learn variables, loops, methods, classes, objects, "
                    + "collections, exception handling, and SQL.";
        }

        if (q.contains("skill") || q.contains("learn")
                || q.contains("career")) {
            return "Choose a target role, review its job requirements, "
                    + "identify the skills you need to learn, and build "
                    + "practical projects. You can ask me to compare your "
                    + "skills with active JobMatch jobs.";
        }

        if (activeJobs != null && asksAboutJobs) {
            return formatJobs(
                    "All active jobs in JobMatch:",
                    activeJobs,
                    true
            );
        }

        return "I can help you search active jobs, filter by company "
                + "or location, list required skills, recommend jobs, "
                + "and provide career guidance.\n\n"
                + "Try:\n"
                + "1. Show all jobs available in JobMatch.\n"
                + "2. What skills are required for jobs in JobMatch?\n"
                + "3. What jobs are available at TechNova Solutions?\n"
                + "4. Show jobs in Hyderabad.\n"
                + "5. I know Java and SQL. Suggest jobs for me.";
    }

    private List<String> extractSkills(String requiredSkills) {
        if (requiredSkills == null || requiredSkills.isBlank()) {
            return Collections.emptyList();
        }

        return Arrays.stream(requiredSkills.split("[,;\\n|]"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }

    private void addAlias(Set<String> skills, String question,
                          String canonical, String... aliases) {
        for (String alias : aliases) {
            if (containsPhrase(question, alias)) {
                skills.add(canonical);
                return;
            }
        }
    }

    private boolean hasEquivalentSkill(
            Set<String> userSkills, String requiredSkill) {

        for (String skill : userSkills) {
            if (skill.equalsIgnoreCase(requiredSkill)) {
                return true;
            }

            if (skill.equalsIgnoreCase("SQL")
                    && (requiredSkill.equalsIgnoreCase("MySQL")
                    || requiredSkill.equalsIgnoreCase("PostgreSQL")
                    || requiredSkill.equalsIgnoreCase("MariaDB"))) {
                return true;
            }

            if (skill.equalsIgnoreCase("JavaScript")
                    && requiredSkill.equalsIgnoreCase("JS")) {
                return true;
            }

            if (skill.equalsIgnoreCase("React")
                    && requiredSkill.equalsIgnoreCase("React.js")) {
                return true;
            }

            if (skill.equalsIgnoreCase("Spring Boot")
                    && requiredSkill.equalsIgnoreCase("SpringBoot")) {
                return true;
            }
        }

        return false;
    }

    private boolean containsPhrase(String text, String phrase) {
        if (text == null || phrase == null || phrase.isBlank()) {
            return false;
        }

        String regex = "(?i)(?<![a-z0-9])"
                + Pattern.quote(phrase.trim())
                + "(?![a-z0-9])";

        return Pattern.compile(regex).matcher(text).find();
    }

    private String formatJobs(
            String heading, List<Job> jobList, boolean includeSkills) {

        StringBuilder result = new StringBuilder(heading)
                .append("\n\n");

        for (Job job : jobList) {
            result.append("Position: ")
                    .append(safe(job.getTitle()))
                    .append("\nCompany: ")
                    .append(safe(job.getCompany()))
                    .append("\nLocation: ")
                    .append(safe(job.getLocation()));

            if (job.getDescription() != null
                    && !job.getDescription().isBlank()) {
                result.append("\nDescription: ")
                        .append(job.getDescription());
            }

            if (includeSkills) {
                result.append("\nRequired skills: ")
                        .append(safe(job.getRequiredSkills()));
            }

            result.append("\n\n");
        }

        result.append("Showing ")
                .append(jobList.size())
                .append(" job(s) from the active jobs stored in JobMatch.");

        return result.toString();
    }

    private String formatSkillsOnly(
            String heading, List<Job> jobList) {

        StringBuilder result = new StringBuilder(heading)
                .append("\n\n");

        for (Job job : jobList) {
            result.append("Position: ")
                    .append(safe(job.getTitle()))
                    .append("\nCompany: ")
                    .append(safe(job.getCompany()))
                    .append("\nRequired skills: ")
                    .append(safe(job.getRequiredSkills()))
                    .append("\n\n");
        }

        result.append("Showing required skills for ")
                .append(jobList.size())
                .append(" active job(s) stored in JobMatch.");

        return result.toString();
    }

    private String safe(String value) {
        return value == null || value.isBlank()
                ? "Not specified"
                : value;
    }
}
