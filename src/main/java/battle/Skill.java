package battle;

import java.util.Random;

public class Skill {
    private int floorDMG;
    private int ceilDMG;
    private String skillName;
    Random random = new Random();

    public Skill(int floorDMG, int ceilDMG, String skillName) {
        this.floorDMG = floorDMG;
        this.ceilDMG = ceilDMG;
        this.skillName = skillName;
    }

    public int getDamage() {
        return random.nextInt(floorDMG, ceilDMG + 1);
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
