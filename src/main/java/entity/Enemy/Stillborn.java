package entity.Enemy;

import battle.Enemy;
import battle.EnemySkill;

public class Stillborn extends Enemy {
    public Stillborn() {
        this.health = 200;
        this.skills.add(new EnemySkill(20, 30, false));
        this.skills.add(new EnemySkill(25, 40, false));
    }

    @Override
    public String getIdleURL() {
        return "/Assets/EnemiesSprites/Stillborn_Idle.gif";
    }

    @Override
    public String getAttackURL() {
        return "/Assets/EnemiesSprites/Stillborn_Attack.gif";
    }
}