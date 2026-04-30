package entity.Player;

import javax.swing.ImageIcon;

import battle.Action;
import battle.Skill;
import main.GamePanel;

public class DebtCollector extends Character {
    public DebtCollector(GamePanel gp) {
        super(70, 0.20, "Yohann - The Debt Collector", gp);
        skills.add(new Skill(20, 50, "Due Date"));
        skills.add(new Skill(20, 75, "Penalty"));
        skills.add(new Skill(0, 0, "Property"));

        actions.add(new Action(.20, .40, "Block"));
        actions.add(new Action(.40, .60, "Stronger Block"));
        actions.add(new Action(0.99, 1, "Immunity"));

        idleUp = new ImageIcon(getClass().getResource("/player/Collector/Back_Collector_Idle.png")).getImage();
        idleDown = new ImageIcon(getClass().getResource("/player/Collector/Front_Collector_Idle.png")).getImage();
        idleLeft = new ImageIcon(getClass().getResource("/player/Collector/Left_Collector_Idle.png")).getImage();
        idleRight = new ImageIcon(getClass().getResource("/player/Collector/Right_Collector_Idle.png")).getImage();

        up = new ImageIcon(getClass().getResource("/player/Collector/Back_Collector.gif")).getImage();
        down = new ImageIcon(getClass().getResource("/player/Collector/Front_Collector.gif")).getImage();
        left = new ImageIcon(getClass().getResource("/player/Collector/Left_Collector.gif")).getImage();
        right = new ImageIcon(getClass().getResource("/player/Collector/Right_Collector.gif")).getImage();
    }
}
