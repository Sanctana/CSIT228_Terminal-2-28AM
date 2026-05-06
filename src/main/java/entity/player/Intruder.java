package entity.player;

import javax.swing.ImageIcon;

import battle.Panel;
import battle.ability.Action;
import battle.ability.Skill;
import main.GamePanel;

public class Intruder extends Character {

    private int abstractionMeter = 50;

    public Intruder(GamePanel gp) {
        super(70, 0.0, "Trixy - The Intruder", gp);

        skills.add(new Skill(10, 30, "Crowbar Strike", 0));
        skills.add(new Skill(30, 50, "Ambush", 2));
        skills.add(new Skill(40, 30, "Wait...", 3));

        actions.add(new Action(0, 0, "Hold your breath", 0));
        actions.add(new Action(0, 0, "Silent Steps", 2));
        actions.add(new Action(0, 0, "Blend in the dark", 3));

        loadAssets();
    }

    private void loadAssets() {
        idleUp = new ImageIcon(getClass().getResource("/player/Intruder/Back_Intruder_Idle.png")).getImage();
        idleDown = new ImageIcon(getClass().getResource("/player/Intruder/Front_Intruder_Idle.png")).getImage();
        idleLeft = new ImageIcon(getClass().getResource("/player/Intruder/Left_Intruder_Idle.png")).getImage();
        idleRight = new ImageIcon(getClass().getResource("/player/Intruder/Right_Intruder_Idle.png")).getImage();

        up = new ImageIcon(getClass().getResource("/player/Intruder/Back_Intruder.gif")).getImage();
        down = new ImageIcon(getClass().getResource("/player/Intruder/Front_Intruder.gif")).getImage();
        left = new ImageIcon(getClass().getResource("/player/Intruder/Left_Intruder.gif")).getImage();
        right = new ImageIcon(getClass().getResource("/player/Intruder/Right_Intruder.gif")).getImage();
    }

    @Override
    public void takeDamage(int damage) {
        if (Math.random() * 100 < abstractionMeter) {
            return;
        }
        super.takeDamage(damage);
    }

    @Override
    public int useSkill(int index) {
        if (index >= 0 && index < skills.size()) {
            Skill skill = skills.get(index);
            if (!skill.isReady()) return 0;

            if (index == 0) {
                abstractionMeter = Math.max(0, abstractionMeter - 10);
            } else if (index == 1) {
                abstractionMeter = Math.max(0, abstractionMeter - 25);
            }
        }
        return super.useSkill(index);
    }

    @Override
    public void useAction(int index, Panel panel) {
        if (index >= 0 && index < actions.size()) {
            Action action = actions.get(index);
            if (!action.isReady()) return;

            int gain = 0;
            if (index == 0) {
                gain = (int) (Math.random() * 11 + 10);
            } else if (index == 1) {
                gain = (int) (Math.random() * 8 + 3);
            } else if (index == 2) {
                gain = (int) (Math.random() * 21 + 40);
            }

            abstractionMeter = Math.min(100, abstractionMeter + gain);

            playActionSound(index);
            action.triggerCooldown();
        }
    }

    @Override
    protected void playSkillSound(int index) {
        sound.playSE("/SoundEffects/lloyd_gunshot.wav"); //temporary
    }

    @Override
    protected void playActionSound(int index) {
        sound.playSE("/SoundEffects/trixy_action.wav");
    }

    @Override
    public String getPlayerPortraitPath() {
        return "/player/Intruder/Trixy_Transparent.png";
    }

    @Override
    public CharacterType getCharacterType() {
        return CharacterType.INTRUDER;
    }
}