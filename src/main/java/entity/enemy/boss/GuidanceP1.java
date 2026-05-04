package entity.enemy.boss;

import battle.EnemySkill;
import entity.enemy.Enemy;

public class GuidanceP1 extends Enemy {
    public GuidanceP1() {
        this.health = 750;
        this.skills.add(new EnemySkill(15, 30, true));
        this.skills.add(new EnemySkill(15, 30, false));
    }

    @Override
    public String getIdleURL() {
        return "/Assets/EnemiesSprites/Guidance_P1_Idle.gif";
    }

    @Override
    public String getAttackURL() {
        return "/Assets/EnemiesSprites/Guidance_P1_Attack.gif";
    }

    @Override
    public String getDisplayName() {
        return "Guidance P1";
    }

    @Override
    public boolean isBoss() {
        return true;
    }

    @Override
    public String getEncounterMessage() {
        return getEncounterMessage("blocks the descent");
    }

    @Override
    public String getDefeatMessage(){
        return getDisplayName() + " has been defeated! The way down is open.";
    }
}
