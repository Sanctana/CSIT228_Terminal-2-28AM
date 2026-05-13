package entity.player;

import javax.swing.ImageIcon;

import battle.Panel;
import battle.ability.Action;
import battle.ability.Skill;
import main.GamePanel;
import main.SoundManager;

public class Artist extends Character {
    protected SoundManager sound = new SoundManager();
    private int currentMode = 1; // Default: Recreation

    public Artist(GamePanel gp) {
        super(70, 0.0, "Tria - The Artist", gp);

        skills.add(new Skill(25, 45, "Forgotten Memories", 0));
        skills.add(new Skill(35, 50, "Taking what's not yours", 2));
        skills.add(new Skill(45, 75, "Imperfect Replica", 3));

        actions.add(new Action(0, 0, "Obsession", 0));
        actions.add(new Action(0, 0, "Recreation", 2));
        actions.add(new Action(0, 0, "Preservation", 3));

        loadAssets();
    }

    private void loadAssets() {
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
        super.useAction(index, panel);

        currentMode = index;

        if (currentMode == 0) { // OBSESSION
            this.resistance = -0.20;
            updateSkills("Relentless Pursuit", 40, 75, "Cursed Brush", 50, 100,"Scarlet Rose",75,125);
        } else if (currentMode == 1) { // RECREATION
            this.resistance = 0.20;
            updateSkills("Forgotten Memories", 20, 40, "Taking what's not yours", 25, 50,"Imperfect Replica",45,75);
        } else if (currentMode == 2) { // PRESERVATION
            this.resistance = 0.50;
            updateSkills("Embracing the Curse", 10, 20, "Shielded Stroke", 15, 30,"Everlasting Torment",20,40);
        }

        panel.refreshButtonText();
    }

    private void updateSkills(String s1Name, int s1Min, int s1Max, String s2Name, int s2Min, int s2Max, String s3Name, int s3Min, int s3Max) {
        skills.get(0).setStats(s1Min, s1Max, s1Name);
        skills.get(1).setStats(s2Min, s2Max, s2Name);
        skills.get(2).setStats(s3Min, s3Max, s3Name);
    }

    @Override
    protected void playSkillSound(int index) {
        sound.playSE("/SoundEffects/sharp-sound.wav");
    }

    @Override
    protected void playActionSound(int index) {
        sound.playSE("/SoundEffects/tria_action.wav");
    }

    @Override
    public String getPlayerPortraitPath() {
        return "/player/Artist/Tria_Transparent.png";
    }

    @Override
    public CharacterType getCharacterType() {
        return CharacterType.ARTIST;
    }
}