package battle;

public class Artist extends Character {
    private int currentMode = 1;

    public Artist() {
        super(100, 0.0, "Tria - The Artist");

        // Initial Skills (Recreation Mode)
        skills.add(new Skill(20, 40, "Forgotten Memories"));
        skills.add(new Skill(25, 50, "Taking what's not yours"));
        skills.add(new Skill(0, 0, "Empty Canvas")); //assume

        // Stance Actions
        actions.add(new Action(0, 0, "Obsession"));
        actions.add(new Action(0, 0, "Recreation"));
        actions.add(new Action(0, 0, "Preservation"));
    }

    @Override
    public void useAction(int index, Panel panel) {
        // Logic: Swapping modes based on the action button clicked, pero feel free to change based rani sa ako understanding
        currentMode = index;

        if (currentMode == 0) { // OBSESSION
            this.resistance = -0.20;
            updateSkills("Relentless Pursuit", 30, 50, "Taking what's not yours", 40, 80);
        }
        else if (currentMode == 1) { // RECREATION
            this.resistance = 0.0;
            updateSkills("Forgotten Memories", 20, 40, "Taking what's not yours", 25, 50);
        }
        else if (currentMode == 2) { // PRESERVATION
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