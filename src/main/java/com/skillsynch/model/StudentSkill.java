package com.skillsynch.model;

/**
 * One row of student_skills (primary key: studentId + skillId).
 * skillName and category are optional and filled by JOIN queries.
 */
public class StudentSkill {

    private int studentId;
    private int skillId;
    private int proficiencyLevel;   // 1 to 5

    // Optional joined display fields
    private String skillName;
    private String category;

    public StudentSkill(int studentId, int skillId, int proficiencyLevel) {
        this.studentId = studentId;
        this.skillId = skillId;
        this.proficiencyLevel = proficiencyLevel;
    }

    public int getStudentId() { return studentId; }
    public int getSkillId() { return skillId; }
    public int getProficiencyLevel() { return proficiencyLevel; }

    public String getSkillName() { return skillName; }
    public String getCategory() { return category; }

    public void setSkillName(String skillName) { this.skillName = skillName; }
    public void setCategory(String category) { this.category = category; }
}
