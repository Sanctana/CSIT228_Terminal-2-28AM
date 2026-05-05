package battle;

public class Skill {
    private int floorDMG;
    private int ceilDMG;
    private String skillName;
    private int maxCooldown;
    private int currentCooldown = 0;

    public Skill(int floorDMG, int ceilDMG, String skillName, int maxCooldown) {
        this.floorDMG = floorDMG;
        this.ceilDMG = ceilDMG;
        this.skillName = skillName;
        this.maxCooldown = maxCooldown;
    }

    public boolean isReady() {
        return currentCooldown <= 0;
    }
    public void triggerCooldown() {
        this.currentCooldown = maxCooldown;
    }
    public void tick() {
        if (currentCooldown > 0) currentCooldown--;
    }

    public int getDamage() {
        return (int) (Math.random() * (ceilDMG - floorDMG + 1)) + floorDMG;
    }

    public String getSkillName() {
        return skillName;
    }

    public int getCurrentCooldown() {
        return currentCooldown;
    }

    public int getFloorDMG() {
        return floorDMG;
    }

    public int getCeilDMG() {
        return ceilDMG;
    }

    public void setStats(int floor, int ceil, String name) {
        this.floorDMG = floor;
        this.ceilDMG = ceil;
        this.skillName = name;
    }

}
