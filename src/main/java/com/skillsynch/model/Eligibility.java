package com.skillsynch.model;

/**
 * One row of the eligibility table (primary key: studentId + internId).
 * companyName and role are optional and filled by JOIN queries.
 */
public class Eligibility {

    private int studentId;
    private int internId;
    private double matchPercentage;
    private EligibilityStatus eligibilityStatus;

    // Optional joined display fields
    private String companyName;
    private String role;

    public Eligibility(int studentId, int internId, double matchPercentage,
                       EligibilityStatus eligibilityStatus) {
        this.studentId = studentId;
        this.internId = internId;
        this.matchPercentage = matchPercentage;
        this.eligibilityStatus = eligibilityStatus;
    }

    public int getStudentId() { return studentId; }
    public int getInternId() { return internId; }
    public double getMatchPercentage() { return matchPercentage; }
    public EligibilityStatus getEligibilityStatus() { return eligibilityStatus; }

    public String getCompanyName() { return companyName; }
    public String getRole() { return role; }

    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setRole(String role) { this.role = role; }
}
