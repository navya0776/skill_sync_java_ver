package com.skillsynch.model;

/** One row of the skills table. */
public class Skill {

    private int skillId;
    private String skillName;
    private String category;   // may be null

    public Skill(int skillId, String skillName, String category) {
        this.skillId = skillId;
        this.skillName = skillName;
        this.category = category;
    }

    public int getSkillId() { return skillId; }
    public String getSkillName() { return skillName; }
    public String getCategory() { return category; }

    @Override
    public String toString() {
        return skillName;
    }
}
