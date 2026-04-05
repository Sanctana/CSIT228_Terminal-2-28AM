package battle;

public class Stillborn extends Enemy {
    public Stillborn() {
        this.health = 200;
        this.skills.add(new EnemySkill(20, 30));
        this.skills.add(new EnemySkill(25, 40));
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