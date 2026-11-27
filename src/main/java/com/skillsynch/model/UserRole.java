package com.skillsynch.model;

/** Values of users.role. */
public enum UserRole {
    ADMIN("admin"),
    STUDENT("student"),
    COMPANY("company");

    private final String label;

    UserRole(String label) {
        this.label = label;
    }

    /** The exact text stored in the database. */
    public String getLabel() {
        return label;
    }

    /** Converts the database text into the enum. Returns null for null. */
    public static UserRole fromLabel(String label) {
        if (label == null) {
            return null;
        }
        for (UserRole value : values()) {
            if (value.label.equals(label)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown UserRole: " + label);
    }

    @Override
    public String toString() {
        return label;
    }
}
