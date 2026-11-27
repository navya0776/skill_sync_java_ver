package com.skillsynch.model;

/** Values of applications.status. */
public enum ApplicationStatus {
    APPLIED("Applied"),
    SHORTLISTED("Shortlisted"),
    INTERVIEW("Interview"),
    SELECTED("Selected"),
    REJECTED("Rejected"),
    OFFER_EXTENDED("Offer Extended"),
    OFFER_ACCEPTED("Offer Accepted"),
    OFFER_REJECTED("Offer Rejected");

    private final String label;

    ApplicationStatus(String label) {
        this.label = label;
    }

    /** The exact text stored in the database. */
    public String getLabel() {
        return label;
    }

    /** Converts the database text into the enum. Returns null for null. */
    public static ApplicationStatus fromLabel(String label) {
        if (label == null) {
            return null;
        }
        for (ApplicationStatus value : values()) {
            if (value.label.equals(label)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown ApplicationStatus: " + label);
    }

    @Override
    public String toString() {
        return label;
    }
}
