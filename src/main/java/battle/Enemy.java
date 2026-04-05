package battle;

import java.util.ArrayList;
import java.util.Random;

public abstract class Enemy {
    protected int health;
    protected boolean isIncreasingDamage;
    protected ArrayList<EnemySkill> skills = new ArrayList<>();
    protected int index;

    protected Random rand = new Random();

    public int skill() {
        if (skills.isEmpty())
            return 0; // No skills, no damage

        index = rand.nextInt(skills.size()); // pick random skill

        EnemySkill chosenSkill = skills.get(index);

        int minDmg = chosenSkill.getFloorDMG();
        int maxDmg = chosenSkill.getCeilDMG();

        return rand.nextInt(maxDmg - minDmg + 1) + minDmg; // random damage between min and max
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void takeDamage(int dmg) {
        health = Math.max(0, health - dmg); // Ensure health doesn't go below 0
    }

    public int getHealth() {
        return health;
    }

    public boolean getIsAlive() {
        return health > 0;
    }

    public abstract String getIdleURL();
    public abstract String getAttackURL();
}
