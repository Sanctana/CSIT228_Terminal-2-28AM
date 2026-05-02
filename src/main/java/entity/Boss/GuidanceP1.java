package entity.Boss;

import battle.Enemy;
import battle.EnemySkill;

public class GuidanceP1 extends Enemy {
    public GuidanceP1() {
        this.health = 750;
        this.skills.add(new EnemySkill(15, 35, true));
        this.skills.add(new EnemySkill(15, 35, false));
    }

    @Override
    public String getIdleURL() {
        return "/Assets/EnemiesSprites/Guidance_P1_Idle.gif";
    }

    @Override
    public String getAttackURL() {
        return "/Assets/EnemiesSprites/Guidance_P1_Attack.gif";
    }
}
