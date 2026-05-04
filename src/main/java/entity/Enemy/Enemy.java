package entity.Enemy;

import java.util.ArrayList;

import battle.EnemySkill;

public abstract class Enemy {
    protected int health;
    protected ArrayList<EnemySkill> skills = new ArrayList<>();

    public int skill() {
        if (skills.isEmpty())
            return 0; // No skills, no damage

        EnemySkill chosenSkill = skills.get((int) (Math.random() * skills.size()));

        int minDmg = chosenSkill.getFloorDMG();
        int maxDmg = chosenSkill.getCeilDMG();

        int damageOutput = (int) (Math.random() * (maxDmg - minDmg + 1)) + minDmg;
        if (chosenSkill.getIsIncreasingDamage()) {
            damageOutput *= -1;
        }
        return damageOutput;
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

    public String getDisplayName() {
        return getClass().getSimpleName();
    }

    public abstract boolean isBoss();

    public abstract String getIdleURL();

    public abstract String getAttackURL();

    protected String getEncounterMessage(String verb) {
        return getDisplayName() + " " + verb + "!";
    }

    public String getEncounterMessage() {
        return getEncounterMessage("appears");
    }

    public String getDefeatMessage() {
        return "The enemy has been suppressed.";
    }
}
