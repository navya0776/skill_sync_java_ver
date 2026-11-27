package com.skillsynch.model;

/** Values of internships.status. */
public enum InternshipStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    PUBLISHED("Published"),
    CLOSED("Closed");

    private final String label;

    InternshipStatus(String label) {
        this.label = label;
    }

    /** The exact text stored in the database. */
    public String getLabel() {
        return label;
    }

    /** Converts the database text into the enum. Returns null for null. */
    public static InternshipStatus fromLabel(String label) {
        if (label == null) {
            return null;
        }
        for (InternshipStatus value : values()) {
            if (value.label.equals(label)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown InternshipStatus: " + label);
    }

    @Override
    public String toString() {
        return label;
    }
}
