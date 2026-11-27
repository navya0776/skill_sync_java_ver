package com.skillsynch.model;

/** Values of offers.status. */
public enum OfferStatus {
    PENDING("Pending"),
    ACCEPTED("Accepted"),
    REJECTED("Rejected");

    private final String label;

    OfferStatus(String label) {
        this.label = label;
    }

    /** The exact text stored in the database. */
    public String getLabel() {
        return label;
    }

    /** Converts the database text into the enum. Returns null for null. */
    public static OfferStatus fromLabel(String label) {
        if (label == null) {
            return null;
        }
        for (OfferStatus value : values()) {
            if (value.label.equals(label)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown OfferStatus: " + label);
    }

    @Override
    public String toString() {
        return label;
    }
}
