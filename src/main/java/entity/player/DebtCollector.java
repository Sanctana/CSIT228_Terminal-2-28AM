package entity.player;

import javax.swing.ImageIcon;

import battle.ability.Action;
import battle.ability.Skill;
import main.GamePanel;
import main.SoundManager;

public class DebtCollector extends Character {
    protected SoundManager sound = new SoundManager();

    public DebtCollector(GamePanel gp) {
        super(70, 0.20, "Yohann - The Debt Collector", gp);

        skills.add(new Skill(20, 50, "Due Date", 0));

        skills.add(new Skill(20, 75, "Penalty", 2));

        skills.add(new Skill(50, 100, "Collection", 3));

        actions.add(new Action(.20, .40, "Block", 0));
        actions.add(new Action(.40, .60, "Stronger Block", 2));
        actions.add(new Action(0.75, 1, "Protection", 3));

        loadImages();
    }

    private void loadImages() {
        idleUp = new ImageIcon(getClass().getResource("/player/Collector/Back_Collector_Idle.png")).getImage();
        idleDown = new ImageIcon(getClass().getResource("/player/Collector/Front_Collector_Idle.png")).getImage();
        idleLeft = new ImageIcon(getClass().getResource("/player/Collector/Left_Collector_Idle.png")).getImage();
        idleRight = new ImageIcon(getClass().getResource("/player/Collector/Right_Collector_Idle.png")).getImage();

        up = new ImageIcon(getClass().getResource("/player/Collector/Back_Collector.gif")).getImage();
        down = new ImageIcon(getClass().getResource("/player/Collector/Front_Collector.gif")).getImage();
        left = new ImageIcon(getClass().getResource("/player/Collector/Left_Collector.gif")).getImage();
        right = new ImageIcon(getClass().getResource("/player/Collector/Right_Collector.gif")).getImage();
    }

    @Override
    protected void playSkillSound(int index) {
        sound.playSE("/SoundEffects/yohann_punch.wav");
    }

    @Override
    protected void playActionSound(int index) {
        sound.playSE("/SoundEffects/swoosh.wav");
    }

    @Override
    public String getPlayerPortraitPath() {
        return "/player/Collector/Yohann_Transparent.png";
    }

    @Override
    public CharacterType getCharacterType() {
        return CharacterType.COLLECTOR;
    }
}