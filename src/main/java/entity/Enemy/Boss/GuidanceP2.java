package entity.Enemy.Boss;

import battle.EnemySkill;
import entity.Enemy.Enemy;

public class GuidanceP2 extends Enemy {
    public GuidanceP2() {
        this.health = 1000;
        this.skills.add(new EnemySkill(20, 35, true));
        this.skills.add(new EnemySkill(20, 35, false));
    }

    @Override
    public String getIdleURL() {
        return "/Assets/EnemiesSprites/Guidance_P2_Idle.gif";
    }

    @Override
    public String getAttackURL() {
        return "/Assets/EnemiesSprites/Guidance_P2_Attack.gif";
    }

    @Override
    public String getDisplayName() {
        return "Guidance P2";
    }

    @Override
    public boolean isBoss() {
        return true;
    }

    @Override
    public String getEncounterMessage() {
        return getEncounterMessage("waits beyond the door");
    }

    @Override
    public String getDefeatMessage(){
        return getDisplayName() + " has been defeated! The way down is open.";
    }
}
