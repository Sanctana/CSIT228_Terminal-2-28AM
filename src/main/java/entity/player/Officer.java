package entity.player;

import javax.swing.ImageIcon;

import battle.ability.Action;
import battle.ability.Skill;
import main.GamePanel;
import main.SoundManager;

public class Officer extends Character {
    protected SoundManager sound = new SoundManager();

    public Officer(GamePanel gp) {
        super(70, .25, "Andrew - The Officer", gp);

        skills.add(new Skill(10, 30, "Warning Shot", 0));
        skills.add(new Skill(20, 75, "Barrage of Bullets", 2));
        skills.add(new Skill(999, 1000, "One Shot", 3));

        actions.add(new Action(.20, .40, "Block", 0));
        actions.add(new Action(.40, .60, "Stronger Block", 2));
        actions.add(new Action(.99, 1, "Immunity", 3));

        loadImages();

    }

    private void loadImages() {
        idleUp = new ImageIcon(getClass().getResource("/player/Officer/Back_Officer_Idle.png")).getImage();
        idleDown = new ImageIcon(getClass().getResource("/player/Officer/Front_Officer_Idle.png")).getImage();
        idleLeft = new ImageIcon(getClass().getResource("/player/Officer/Left_Officer_Idle.png")).getImage();
        idleRight = new ImageIcon(getClass().getResource("/player/Officer/Right_Officer_Idle.png")).getImage();

        up = new ImageIcon(getClass().getResource("/player/Officer/Back_Officer.gif")).getImage();
        down = new ImageIcon(getClass().getResource("/player/Officer/Front_Officer.gif")).getImage();
        left = new ImageIcon(getClass().getResource("/player/Officer/Left_Officer.gif")).getImage();
        right = new ImageIcon(getClass().getResource("/player/Officer/Right_Officer.gif")).getImage();
    }

    @Override
    protected void playSkillSound(int index) {
        sound.playSE("/SoundEffects/andrew_gunshot.wav");
    }

    @Override
    protected void playActionSound(int index) {
        sound.playSE("/SoundEffects/swoosh.wav");
    }

    @Override
    public String getPlayerPortraitPath() {
        return "/player/Officer/Andrew_Transparent.png";
    }

    @Override
    public CharacterType getCharacterType() {
        return CharacterType.OFFICER;
    }

}