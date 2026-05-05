package battle.ability;

public class Skill extends Ability {
    private int floorDMG;
    private int ceilDMG;

    public Skill(int floorDMG, int ceilDMG, String skillName, int maxCooldown) {
        super(skillName, maxCooldown);
        this.floorDMG = floorDMG;
        this.ceilDMG = ceilDMG;
    }

    public int getDamage() {
        return (int) (Math.random() * (ceilDMG - floorDMG + 1)) + floorDMG;
    }

    public void setStats(int floor, int ceil, String name) {
        this.floorDMG = floor;
        this.ceilDMG = ceil;
        setName(name);
    }

    public int getFloorDMG() {
        return floorDMG;
    }

    public int getCeilDMG() {
        return ceilDMG;
    }
}
