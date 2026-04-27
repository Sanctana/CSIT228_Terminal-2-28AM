package battle;

import javax.swing.*;
import java.util.Arrays;

public class Detective extends Character {
    private int maxBullets = 6;
    private int index = 0;
    private boolean[] chamber = new boolean[6];
    private double damageMultiplier = 0.5;

    public Detective(main.GamePanel gp) {
        super(100, 0.90, "John Lloyd - The Detective",gp);

        // Math overridden in useSkill
        skills.add(new Skill(0, 0, "Shoot"));
        skills.add(new Skill(0, 0, "Judgement"));
        skills.add(new Skill(5, 15, "Pistol Whip"));

        actions.add(new Action(0, 0, "Shoot self"));
        actions.add(new Action(0, 0, "Peek"));
        actions.add(new Action(0, 0, "Spin"));

        resetRevolver();
    }

    public void resetRevolver() {
        Arrays.fill(chamber, false);
        chamber[random.nextInt(maxBullets)] = true;
        index = 0;
        System.out.println("Revolver Spun!");
    }

    @Override
    public int useSkill(int skillIndex) {
        if (skillIndex == 0) { // Skill 1: Shoot

            int finalDmg = (int)(20 * damageMultiplier);

            this.damageMultiplier = 0.5;
            resetRevolver();
            return finalDmg;
        }

        if (skillIndex == 1) { // Skill 2: Judgement
            int remaining = maxBullets - index;
            double successChance = 1.0 / remaining;

            if (random.nextDouble() < successChance) {
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
                if (index >= maxBullets) resetRevolver();
            }
        }
        else if (actionIndex == 1) { // Action 2: Peek
            boolean isLive = chamber[index];
            String status = isLive ? "LIVE ROUND" : "EMPTY";
            JOptionPane.showMessageDialog(null, "The current chamber is: " + status);
        }
        else if (actionIndex == 2) { // Action 3: Spin
            resetRevolver();
        }
    }
}