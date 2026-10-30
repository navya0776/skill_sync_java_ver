
package com.skillsynch.model;

public class User {

    private int userId;
    private String username;
    private String passwordHash;
    private String role;
    private Integer studentId;
    private Integer companyId;

    public User(int userId, String username, String passwordHash,
                String role, Integer studentId, Integer companyId) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.studentId = studentId;
        this.companyId = companyId;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getRole() {
        return role;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public Integer getCompanyId() {
        return companyId;
    }
}
