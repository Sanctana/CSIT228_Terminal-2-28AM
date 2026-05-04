package entity.enemy;

import battle.EnemySkill;

public class Scalper extends Enemy {
    public Scalper() {
        this.health = 200;
        this.skills.add(new EnemySkill(5, 15, false));
        this.skills.add(new EnemySkill(10, 20, false));
    }

    @Override
    public String getIdleURL() {
        return "/Assets/EnemiesSprites/ScalperIdle.gif";
    }

    @Override
    public String getAttackURL() {
        return "/Assets/EnemiesSprites/ScalperAttack.gif";
    }

    @Override
    public boolean isBoss() {
        return false;
    }
}