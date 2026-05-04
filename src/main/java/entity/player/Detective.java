package entity.player;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import java.util.Arrays;

import battle.Action;
import battle.Panel;
import battle.Skill;
import main.GamePanel;

public class Detective extends Character {
    private int maxBullets = 6;
    private int index = 0;
    private boolean[] chamber = new boolean[6];
    private double damageMultiplier = 0.5;

    public Detective(GamePanel gp) {
        super(100, 0.90, "John Lloyd - The Detective", gp);

        // Math overridden in useSkill
        skills.add(new Skill(0, 0, "Shoot"));
        skills.add(new Skill(0, 0, "Judgement"));
        skills.add(new Skill(5, 15, "Pistol Whip"));

        actions.add(new Action(0, 0, "Shoot self"));
        actions.add(new Action(0, 0, "Peek"));
        actions.add(new Action(0, 0, "Spin"));

        resetRevolver();
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
        if (skillIndex == 0) { // Skill 1: Shoot

            int finalDmg = (int) (20 * damageMultiplier);

            this.damageMultiplier = 0.5;
            resetRevolver();
            return finalDmg;
        }

        if (skillIndex == 1) { // Skill 2: Judgement
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
        if (actionIndex == 0) { // Action 1: Shoot self
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
        } else if (actionIndex == 1) { // Action 2: Peek
            boolean isLive = chamber[index];
            String status = isLive ? "LIVE ROUND" : "EMPTY";
            JOptionPane.showMessageDialog(null, "The current chamber is: " + status);
        } else if (actionIndex == 2) { // Action 3: Spin
            resetRevolver();
        }
    }

    @Override
    public String getPlayerPortraitPath() {
        return "/player/Detective/Detective_Icon.jpg";
    }

    @Override
    public CharacterType getCharacterType() {
        return CharacterType.DETECTIVE;
    }
}