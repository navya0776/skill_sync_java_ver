package com.skillsynch.model;

import java.time.LocalDate;

/** One row of the internships table. */
public class Internship {

    private int internId;
    private int companyId;
    private String companyName;
    private String role;
    private String description;           // may be null
    private Double minCgpa;               // may be null
    private int numberOfPositions;
    private LocalDate applicationDeadline; // may be null
    private InternshipStatus status;

    public Internship(int internId, int companyId, String companyName, String role,
                      String description, Double minCgpa, int numberOfPositions,
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
    public int getCompanyId() { return companyId; }
    public String getCompanyName() { return companyName; }
    public String getRole() { return role; }
    public String getDescription() { return description; }
    public Double getMinCgpa() { return minCgpa; }
    public int getNumberOfPositions() { return numberOfPositions; }
    public LocalDate getApplicationDeadline() { return applicationDeadline; }
    public InternshipStatus getStatus() { return status; }

    public void setStatus(InternshipStatus status) { this.status = status; }
}
