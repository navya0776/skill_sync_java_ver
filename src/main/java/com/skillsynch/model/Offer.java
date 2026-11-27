package com.skillsynch.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * One row of the offers table.
 * The optional fields below are filled only by queries that JOIN
 * applications / students / internships.
 */
public class Offer {

    private int offerId;
    private int applicationId;
    private LocalDateTime offerDate;
    private LocalDate joiningDate;   // may be null
    private OfferStatus status;

    // Optional joined display fields
    private String studentName;
    private String studentEmail;
    private String companyName;
    private String role;

    public Offer(int offerId, int applicationId, LocalDateTime offerDate,
                 LocalDate joiningDate, OfferStatus status) {
        this.offerId = offerId;
        this.applicationId = applicationId;
        this.offerDate = offerDate;
        this.joiningDate = joiningDate;
        this.status = status;
    }

    public int getOfferId() { return offerId; }
    public int getApplicationId() { return applicationId; }
    public LocalDateTime getOfferDate() { return offerDate; }
    public LocalDate getJoiningDate() { return joiningDate; }
    public OfferStatus getStatus() { return status; }

    public String getStudentName() { return studentName; }
    public String getStudentEmail() { return studentEmail; }
    public String getCompanyName() { return companyName; }
    public String getRole() { return role; }

    public void setStatus(OfferStatus status) { this.status = status; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setRole(String role) { this.role = role; }
}
