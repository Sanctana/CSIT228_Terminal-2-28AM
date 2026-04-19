package battle;

public class EnemySkill {
    private int floorDMG;
    private int ceilDMG;
    private boolean isIncreasingDamage;
    public EnemySkill(int floorDMG, int ceilDMG, boolean isIncreasingDamage) {
        this.floorDMG = floorDMG;
        this.ceilDMG = ceilDMG;
        this.isIncreasingDamage = isIncreasingDamage;
    }

    public int getFloorDMG() {
        return floorDMG;
    }

    public int getCeilDMG() {
        return ceilDMG;
    }

    public boolean getIsIncreasingDamage(){
        return isIncreasingDamage;
    }
}
