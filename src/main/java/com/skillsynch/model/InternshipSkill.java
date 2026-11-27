package com.skillsynch.model;

/**
 * One row of internship_skills (primary key: internId + skillId).
 * skillName is optional and filled by JOIN queries.
 */
public class InternshipSkill {

    private int internId;
    private int skillId;
    private int minLevel;   // 1 to 5

    // Optional joined display field
    private String skillName;

    public InternshipSkill(int internId, int skillId, int minLevel) {
        this.internId = internId;
        this.skillId = skillId;
        this.minLevel = minLevel;
    }

    public int getInternId() { return internId; }
    public int getSkillId() { return skillId; }
    public int getMinLevel() { return minLevel; }

    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }
}
