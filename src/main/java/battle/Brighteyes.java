package battle;

public class Brighteyes extends Enemy {
    public Brighteyes() {
        this.health = 200;
        this.skills.add(new EnemySkill(5, 15, true));
        this.skills.add(new EnemySkill(10, 20, true));
    }

    @Override
    public String getIdleURL() {
        return "/Assets/EnemiesSprites/Brighteyes_Idle.gif";
    }

    @Override
    public String getAttackURL() {
        return "/Assets/EnemiesSprites/Brighteyes_Attack.gif";
    }
}
