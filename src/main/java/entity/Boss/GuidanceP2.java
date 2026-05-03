package entity.Boss;

import battle.Enemy;
import battle.EnemySkill;

public class GuidanceP2 extends Enemy {
    public GuidanceP2() {
        this.health = 1000;
        this.skills.add(new EnemySkill(20, 40, true));
        this.skills.add(new EnemySkill(20, 40, false));
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
}
