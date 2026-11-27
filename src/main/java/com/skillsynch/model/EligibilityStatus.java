package com.skillsynch.model;

/** Values of eligibility.eligibility_status. */
public enum EligibilityStatus {
    ELIGIBLE("Eligible"),
    PARTIALLY_ELIGIBLE("Partially Eligible"),
    NOT_ELIGIBLE("Not Eligible");

    private final String label;

    EligibilityStatus(String label) {
        this.label = label;
    }

    /** The exact text stored in the database. */
    public String getLabel() {
        return label;
    }

    /** Converts the database text into the enum. Returns null for null. */
    public static EligibilityStatus fromLabel(String label) {
        if (label == null) {
            return null;
        }
        for (EligibilityStatus value : values()) {
            if (value.label.equals(label)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown EligibilityStatus: " + label);
    }

    @Override
    public String toString() {
        return label;
    }
}
