package com.skillsynch.model;

/**
 * Represents one row of the Students table.
 * userId and username are only filled when the student is loaded together
 * with their login account (see StudentDAO.getAllStudents).
 */
public class Student {

    private int studentId;
    private String name;
    private String branch;
    private Integer year;    // may be null
    private Double cgpa;     // may be null
    private String email;    // may be null
    private String resumeUrl; // may be null

    // Optional: from the Users table
    private Integer userId;
    private String username;

    public Student(int studentId, String name, String branch, Integer year,
                   Double cgpa, String email, String resumeUrl) {
        this.studentId = studentId;
        this.name = name;
        this.branch = branch;
        this.year = year;
        this.cgpa = cgpa;
        this.email = email;
        this.resumeUrl = resumeUrl;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public String getBranch() {
        return branch;
    }

    /** May be null if not yet specified. */
    public Integer getYear() {
        return year;
    }

    /** May be null if not yet specified. */
    public Double getCgpa() {
        return cgpa;
    }

    /** May be null if not provided. */
    public String getEmail() {
        return email;
    }

    /** May be null if no resume has been uploaded. */
    public String getResumeUrl() {
        return resumeUrl;
    }

    /** May be null if the student has no login account. */
    public Integer getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public void setCgpa(Double cgpa) {
        this.cgpa = cgpa;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
