package entity.player;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import java.awt.Graphics2D;
import java.util.Arrays;
import battle.Panel;
import battle.ability.Action;
import battle.ability.Skill;
import main.GamePanel;
import main.SoundManager;

public class Detective extends Character {
    private int maxBullets = 6;
    private int index = 0;
    private boolean[] chamber = new boolean[6];
    private double damageMultiplier = 0.5;
    protected SoundManager sound = new SoundManager();

    public Detective(GamePanel gp) {
        super(70, 0.90, "John Lloyd - The Detective", gp);

        skills.add(new Skill(0, 0, "Shoot", 0));
        skills.add(new Skill(0, 0, "Judgement", 2));
        skills.add(new Skill(5, 15, "Pistol Whip", 3));

        actions.add(new Action(0, 0, "Shoot self", 0));
        actions.add(new Action(0, 0, "Peek", 2));
        actions.add(new Action(0, 0, "Spin", 3));

        resetRevolver();

        loadImages();
    }

    private void loadImages() {
        idleUp = new ImageIcon(getClass().getResource("/player/Detective/Back_Detective_Idle.png")).getImage();
        idleDown = new ImageIcon(getClass().getResource("/player/Detective/Front_Detective_Idle.png")).getImage();
        idleLeft = new ImageIcon(getClass().getResource("/player/Detective/Left_Detective_Idle.png")).getImage();
        idleRight = new ImageIcon(getClass().getResource("/player/Detective/Right_Detective_Idle.png")).getImage();

        up = new ImageIcon(getClass().getResource("/player/Detective/Back_Detective.gif")).getImage();
        down = new ImageIcon(getClass().getResource("/player/Detective/Front_Detective.gif")).getImage();
        left = new ImageIcon(getClass().getResource("/player/Detective/Left_Detective.gif")).getImage();
        right = new ImageIcon(getClass().getResource("/player/Detective/Right_Detective.gif")).getImage();
    }

    public void resetRevolver() {
        Arrays.fill(chamber, false);
        chamber[(int) (Math.random() * maxBullets)] = true;
        index = 0;
    }

    @Override
    public int useSkill(int skillIndex) {

        if (skillIndex == 0) { // Shoot
            playSkillSound(skillIndex); // 🔊

            int finalDmg = (int) (20 * damageMultiplier);
            this.damageMultiplier = 0.5;
            resetRevolver();
            return finalDmg;
        }

        if (skillIndex == 1) { // Judgement
            playSkillSound(skillIndex); // 🔊

            int remaining = maxBullets - index;
            double successChance = 1.0 / remaining;

            if (Math.random() < successChance) {
                return 200;
            }
            return 0;
        }

        return super.useSkill(skillIndex);
    }

    @Override
    public void useAction(int actionIndex, Panel panel) {

        actions.get(actionIndex).triggerCooldown();

        playActionSound(actionIndex);

        if (actionIndex == 0) {
            if (chamber[index]) {
                this.heartRate -= 40;
                this.damageMultiplier = 0.5;
                resetRevolver();
            } else {
                this.damageMultiplier += 1.0;
                index++;
                if (index >= maxBullets)
                    resetRevolver();
            }

        } else if (actionIndex == 1) {
            boolean isLive = chamber[index];
            String status = isLive ? "LIVE ROUND" : "EMPTY";
            JOptionPane.showMessageDialog(null, "The current chamber is: " + status);

        } else if (actionIndex == 2) {
            resetRevolver();
        }
    }

    @Override
    protected void playSkillSound(int index) {
        if (index == 0) {
            sound.playSE("/SoundEffects/lloyd_gunshot.wav");
        }
        if (index == 1) {
                sound.playSE("/SoundEffects/lloyd_gunshot.wav");
            }
        if (index == 2) {
            sound.playSE("/SoundEffects/metal_hit.wav");
        }
    }

    @Override
    protected void playActionSound(int actionIndex) {

        if (actionIndex == 0) {
                sound.playSE("/SoundEffects/lloyd_gunshot.wav");
            }
        if (actionIndex == 1) {
            sound.playSE("/SoundEffects/lloyd_gunshot.wav");
        }
        if (actionIndex == 2) {
            sound.playSE("/SoundEffects/revolver_spin.wav");
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        super.draw(g2);
    }

    @Override
    public String getPlayerPortraitPath() {
        return "/player/Detective/Lloyd_Transparent.png";
    }

    @Override
    public CharacterType getCharacterType() {
        return CharacterType.DETECTIVE;
    }
}