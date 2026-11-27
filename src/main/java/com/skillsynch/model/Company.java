package com.skillsynch.model;

/** One row of the companies table. */
public class Company {

    private int companyId;
    private String companyName;
    private String industry;      // may be null
    private String description;   // may be null

    public Company(int companyId, String companyName, String industry, String description) {
        this.companyId = companyId;
        this.companyName = companyName;
        this.industry = industry;
        this.description = description;
    }

    public int getCompanyId() { return companyId; }
    public String getCompanyName() { return companyName; }
    public String getIndustry() { return industry; }
    public String getDescription() { return description; }

    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setIndustry(String industry) { this.industry = industry; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return companyName;
    }
}
