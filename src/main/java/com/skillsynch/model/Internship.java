package com.skillsynch.model;

import java.time.LocalDate;

/** One row of the internships table. */
public class Internship {

    private int internId;
    private Integer companyId;            // null if the company was deleted
    private String companyName;
    private String role;
    private String description;           // may be null
    private Double minCgpa;               // may be null
    private Integer numberOfPositions;    // may be null
    private LocalDate applicationDeadline; // may be null
    private InternshipStatus status;

    public Internship(int internId, Integer companyId, String companyName, String role,
                      String description, Double minCgpa, Integer numberOfPositions,
                      LocalDate applicationDeadline, InternshipStatus status) {
        this.internId = internId;
        this.companyId = companyId;
        this.companyName = companyName;
        this.role = role;
        this.description = description;
        this.minCgpa = minCgpa;
        this.numberOfPositions = numberOfPositions;
        this.applicationDeadline = applicationDeadline;
        this.status = status;
    }

    public int getInternId() { return internId; }
    public Integer getCompanyId() { return companyId; }
    public String getCompanyName() { return companyName; }
    public String getRole() { return role; }
    public String getDescription() { return description; }
    public Double getMinCgpa() { return minCgpa; }
    public Integer getNumberOfPositions() { return numberOfPositions; }
    public LocalDate getApplicationDeadline() { return applicationDeadline; }
    public InternshipStatus getStatus() { return status; }

    public void setStatus(InternshipStatus status) { this.status = status; }
}
