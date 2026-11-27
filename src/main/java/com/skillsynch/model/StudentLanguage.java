package com.skillsynch.model;

/** One row of student_languages (primary key: studentId + language). */
public class StudentLanguage {

    private int studentId;
    private String language;
    private int proficiencyLevel;   // 1 to 5

    public StudentLanguage(int studentId, String language, int proficiencyLevel) {
        this.studentId = studentId;
        this.language = language;
        this.proficiencyLevel = proficiencyLevel;
    }

    public int getStudentId() { return studentId; }
    public String getLanguage() { return language; }
    public int getProficiencyLevel() { return proficiencyLevel; }
}
