package entity.enemy;

import battle.ability.EnemySkill;
import main.SoundManager;

public class Scalper extends Enemy {
    protected SoundManager sound = new SoundManager();
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
    public String getSoundURL() {
        return "";
    }

    @Override
    public void playSkillSound(int index) {
        sound.playSE("/SoundEffects/scalper_slash.wav");
    }

    @Override
    public boolean isBoss() {
        return false;
    }
}