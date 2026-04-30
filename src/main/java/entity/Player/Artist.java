package entity.Player;

import javax.swing.ImageIcon;

import battle.Action;
import battle.Panel;
import battle.Skill;
import main.GamePanel;

public class Artist extends Character {
    private int currentMode = 1;

    public Artist(GamePanel gp) {
        super(100, 0.0, "Tria - The Artist", gp);

        // Initial Skills (Recreation Mode)
        skills.add(new Skill(20, 40, "Forgotten Memories"));
        skills.add(new Skill(25, 50, "Taking what's not yours"));
        skills.add(new Skill(0, 0, "Empty Canvas")); // assume

        // Stance Actions
        actions.add(new Action(0, 0, "Obsession"));
        actions.add(new Action(0, 0, "Recreation"));
        actions.add(new Action(0, 0, "Preservation"));

        idleUp = new ImageIcon(getClass().getResource("/player/Artist/Back_Artist_Idle.png")).getImage();
        idleDown = new ImageIcon(getClass().getResource("/player/Artist/Front_Artist_Idle.png")).getImage();
        idleLeft = new ImageIcon(getClass().getResource("/player/Artist/Left_Artist_Idle.png")).getImage();
        idleRight = new ImageIcon(getClass().getResource("/player/Artist/Right_Artist_Idle.png")).getImage();

        up = new ImageIcon(getClass().getResource("/player/Artist/Back_Artist.gif")).getImage();
        down = new ImageIcon(getClass().getResource("/player/Artist/Front_Artist.gif")).getImage();
        left = new ImageIcon(getClass().getResource("/player/Artist/Left_Artist.gif")).getImage();
        right = new ImageIcon(getClass().getResource("/player/Artist/Right_Artist.gif")).getImage();

    }

    @Override
    public void useAction(int index, Panel panel) {
        // Logic: Swapping modes based on the action button clicked, pero feel free to
        // change based rani sa ako understanding
        currentMode = index;

        if (currentMode == 0) { // OBSESSION
            this.resistance = -0.20;
            updateSkills("Relentless Pursuit", 30, 50, "Taking what's not yours", 40, 80);
        } else if (currentMode == 1) { // RECREATION
            this.resistance = 0.0;
            updateSkills("Forgotten Memories", 20, 40, "Taking what's not yours", 25, 50);
        } else if (currentMode == 2) { // PRESERVATION
            this.resistance = 0.30;
            updateSkills("Embracing the curse", 10, 20, "Taking what's not yours", 15, 30);
        }
        panel.refreshButtonText();
    }

    private void updateSkills(String s1Name, int s1Min, int s1Max, String s2Name, int s2Min, int s2Max) {
        skills.set(0, new Skill(s1Min, s1Max, s1Name));
        skills.set(1, new Skill(s2Min, s2Max, s2Name));
    }
}