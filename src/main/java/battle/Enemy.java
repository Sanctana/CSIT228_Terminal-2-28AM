package battle;

import java.util.ArrayList;
import java.util.Random;

public class Enemy {
    private int damageOutput;
    private int health;
    boolean isIncreasingDamage;
    boolean isAlive;
    private ArrayList<EnemySkill> skills = new ArrayList<>();
    private int index;

    private Random rand = new Random();

    public Enemy(int health, boolean isAlive) {
        this.health = health;
        this.isAlive = isAlive;
    }

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
        if (this.health <= 0)
            isAlive = false;
    }

    public void setIsAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public void takeDamage(int dmg) {
        health -= dmg;
        if (health <= 0) {
            health = 0;
            isAlive = false;
        }
    }

    public int getHealth() {
        return health;
    }

    public boolean getIsAlive() {
        return isAlive;
    }

}
