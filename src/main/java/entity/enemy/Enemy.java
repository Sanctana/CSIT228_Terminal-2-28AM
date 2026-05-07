package entity.enemy;

import java.util.ArrayList;

import battle.ability.EnemySkill;
import inventory.Defibrillator;
import inventory.IVFluids;
import inventory.Item;
import inventory.Scalpel;

public abstract class Enemy {
    protected int health;
    protected ArrayList<EnemySkill> skills = new ArrayList<>();

    public int skill() {
        if (skills.isEmpty())
            return 0;

        int skillIndex = (int) (Math.random() * skills.size());

        playSkillSound(skillIndex);

        EnemySkill chosenSkill = skills.get(skillIndex);

        int minDmg = chosenSkill.getFloorDMG();
        int maxDmg = chosenSkill.getCeilDMG();

        int damageOutput = (int) (Math.random() * (maxDmg - minDmg + 1)) + minDmg;
        if (chosenSkill.getIsIncreasingDamage()) {
            damageOutput *= -1;
        }
        return damageOutput;
    }

    public Item dropItem() {
        int amount = (int)(Math.random() * 3) + 1;

        String enemyName = getClass().getSimpleName();

        switch (enemyName) {
            case "Scalper":
                return new Scalpel(amount);

            case "Stillborn":
                return new IVFluids(amount);

            case "Brighteyes":
                return new Defibrillator(amount);

            default:
                return null;
        }
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void takeDamage(int dmg) {
        health = Math.max(0, health - dmg);
    }

    public int getHealth() {
        return health;
    }

    public boolean getIsAlive() {
        return health > 0;
    }

    public abstract String getSoundURL();

    public abstract void playSkillSound(int index);

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