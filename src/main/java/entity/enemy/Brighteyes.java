package entity.enemy;

import battle.ability.EnemySkill;
import main.SoundManager;


public class Brighteyes extends Enemy {

    protected SoundManager sound = new SoundManager();
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


    @Override
    public String getSoundURL() {
        return "";
    }

    @Override
    public void playSkillSound(int index) {
        sound.playSE("/SoundEffects/metal_hit.wav");
    }

    @Override
    public boolean isBoss() {
        return false;
    }
}
