package battle;

// not final
public class EnemySkill {
    private int floorDMG;
    private int ceilDMG;

    public EnemySkill(int floorDMG, int ceilDMG){
        this.floorDMG = floorDMG;
        this.ceilDMG = ceilDMG;
    }

    public int getFloorDMG(){
        return floorDMG;
    }

    public int getCeilDMG(){
        return ceilDMG;
    }
}
