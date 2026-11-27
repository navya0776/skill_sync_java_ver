package com.skillsynch.model;

/** One row of internship_languages (primary key: internId + language). */
public class InternshipLanguage {

    private int internId;
    private String language;
    private int minLevel;   // 1 to 5

    public InternshipLanguage(int internId, String language, int minLevel) {
        this.internId = internId;
        this.language = language;
        this.minLevel = minLevel;
    }

    public int getInternId() { return internId; }
    public String getLanguage() { return language; }
    public int getMinLevel() { return minLevel; }
}
