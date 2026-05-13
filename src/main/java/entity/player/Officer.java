package entity.player;

import javax.swing.ImageIcon;

import battle.Panel;
import battle.ability.Action;
import battle.ability.Skill;
import main.GamePanel;

public class Officer extends Character {

    private int abstractionMeter = 50;

    public Officer(GamePanel gp) {
        super(70, .25, "Andrew - The Officer", gp);

        skills.add(new Skill(100000, 30000000, "Warning Shot", 0));
        skills.add(new Skill(20, 75, "Barrage of Bullets", 2));
        skills.add(new Skill(9, 10, "One Shot", 3));

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

            // Trigger sound manually
            playSkillSound(index);

            // Start the cooldown timer
            skill.triggerCooldown();

            // Return the damage calculation
            return skill.useSkill();
        }
        return 0;
    }

    @Override
    public void useAction(int index, Panel panel) {
        if (index >= 0 && index < actions.size()) {
            Action action = actions.get(index);

            if (!action.isReady()) return;

            // Apply standard block logic
            this.setResistance(action.action());

            playActionSound(index);
            action.triggerCooldown();
        }
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