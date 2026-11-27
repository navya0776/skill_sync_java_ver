package com.skillsynch.model;

import java.time.LocalDateTime;

/**
 * One row of the applications table.
 * The optional fields below are filled only by queries that JOIN
 * students / internships (for example the company and admin views).
 */
public class Application {

    private int applicationId;
    private int studentId;
    private int internId;
    private ApplicationStatus status;
    private LocalDateTime applicationDate;

    // Optional joined display fields
    private String studentName;
    private String studentEmail;
    private String branch;
    private Double cgpa;
    private String companyName;
    private String role;

    public Application(int applicationId, int studentId, int internId,
                       ApplicationStatus status, LocalDateTime applicationDate) {
        this.applicationId = applicationId;
        this.studentId = studentId;
        this.internId = internId;
        this.status = status;
        this.applicationDate = applicationDate;
    }

    public int getApplicationId() { return applicationId; }
    public int getStudentId() { return studentId; }
    public int getInternId() { return internId; }
    public ApplicationStatus getStatus() { return status; }
    public LocalDateTime getApplicationDate() { return applicationDate; }

    public String getStudentName() { return studentName; }
    public String getStudentEmail() { return studentEmail; }
    public String getBranch() { return branch; }
    public Double getCgpa() { return cgpa; }
    public String getCompanyName() { return companyName; }
    public String getRole() { return role; }

    public void setStatus(ApplicationStatus status) { this.status = status; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }
    public void setBranch(String branch) { this.branch = branch; }
    public void setCgpa(Double cgpa) { this.cgpa = cgpa; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setRole(String role) { this.role = role; }
}
