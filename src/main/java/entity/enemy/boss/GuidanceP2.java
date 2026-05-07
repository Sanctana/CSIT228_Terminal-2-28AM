package entity.enemy.boss;

import battle.ability.EnemySkill;
import entity.enemy.Enemy;
import main.SoundManager;

public class GuidanceP2 extends Enemy {
    protected SoundManager sound = new SoundManager();

    public GuidanceP2() {
        this.health = 1000;
        this.skills.add(new EnemySkill(20, 35, true));
        this.skills.add(new EnemySkill(20, 35, false));
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
    public String getSoundURL() {
        return "/SoundEffects/guidance_p2.wav";
    }

    @Override
    public void playSkillSound(int index) {
        sound.playSE("/SoundEffects/guidance_p2.wav");
    }

    @Override
    public String getDisplayName() {
        return "Guidance P2";
    }

    @Override
    public boolean isBoss() {
        return true;
    }

    @Override
    public String getEncounterMessage() {
        return getEncounterMessage("waits beyond the door");
    }

    @Override
    public String getDefeatMessage(){
        return getDisplayName() + " has been defeated! The way down is open.";
    }
}