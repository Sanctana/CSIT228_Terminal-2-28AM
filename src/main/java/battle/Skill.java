package battle;

public class Skill {
    private int floorDMG;
    private int ceilDMG;
    private String skillName;

    public Skill(int floorDMG, int ceilDMG, String skillName) {
        this.floorDMG = floorDMG;
        this.ceilDMG = ceilDMG;
        this.skillName = skillName;
    }

    public int getDamage() {
        return (int) (Math.random() * (ceilDMG - floorDMG + 1)) + floorDMG;
    }

    public String getSkillName() {
        return skillName;
    }

    public int getFloorDMG() {
        return floorDMG;
    }

    public int getCeilDMG() {
        return ceilDMG;
    }
}
