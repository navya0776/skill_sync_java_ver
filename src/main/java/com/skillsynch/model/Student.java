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
    private int year;
    private double cgpa;
    private String email;
    private String resumeUrl;

    // Optional: from the Users table
    private Integer userId;
    private String username;

    public Student(int studentId, String name, String branch, int year,
                   double cgpa, String email, String resumeUrl) {
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

    public int getYear() {
        return year;
    }

    public double getCgpa() {
        return cgpa;
    }

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

    public void setYear(int year) {
        this.year = year;
    }

    public void setCgpa(double cgpa) {
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
