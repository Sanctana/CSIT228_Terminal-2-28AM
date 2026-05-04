package entity.enemy.boss;

import battle.EnemySkill;
import entity.enemy.Enemy;

public class GuidanceP3 extends Enemy {
    public GuidanceP3() {
        this.health = 1500;
        this.skills.add(new EnemySkill(25, 40, true));
        this.skills.add(new EnemySkill(25, 40, false));
    }

    @Override
    public String getIdleURL() {
        return "/Assets/EnemiesSprites/Guidance_P3_Idle.gif";
    }

    @Override
    public String getAttackURL() {
        return "/Assets/EnemiesSprites/Guidance_P3_Attack.gif";
    }

    @Override
    public String getDisplayName() {
        return "Guidance P3";
    }

    @Override
    public boolean isBoss() {
        return true;
    }

    @Override
    public String getDefeatMessage(){
        return getDisplayName() + " has been defeated! The way down is open.";
    }
}
