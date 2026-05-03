package entity.Boss;

import battle.Enemy;
import battle.EnemySkill;

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
}
