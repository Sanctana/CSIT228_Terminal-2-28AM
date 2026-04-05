package battle;

public class Scalper extends Enemy {
    public Scalper() {
        this.health = 200;
        this.skills.add(new EnemySkill(5, 15));
        this.skills.add(new EnemySkill(10, 20));
    }

    @Override
    public String getIdleURL() {
        return "/Assets/EnemiesSprites/ScalperIdle.gif";
    }

    @Override
    public String getAttackURL() {
        return "/Assets/EnemiesSprites/ScalperAttack.gif";
    }
}