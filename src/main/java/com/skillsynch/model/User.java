
package com.skillsynch.model;

/** One row of the users table. */
public class User {

    private int userId;
    private String username;
    private String passwordHash;
    private UserRole role;
    private Integer studentId;
    private Integer companyId;

    public User(int userId, String username, String passwordHash,
                UserRole role, Integer studentId, Integer companyId) {
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

    public UserRole getRole() {
        return role;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public Integer getCompanyId() {
        return companyId;
    }
}
