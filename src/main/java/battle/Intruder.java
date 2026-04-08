package battle;

public class Intruder extends Character {
    private int abstractionMeter = 50;

    public Intruder() {
        super(70, 0.0, "Trixy - The Intruder");

        skills.add(new Skill(10, 30, "Crowbar Strike"));
        skills.add(new Skill(30, 50, "Ambush"));
        skills.add(new Skill(40, 30, "Wait...")); //assume

        actions.add(new Action(0, 0, "Hold your breath"));
        actions.add(new Action(0, 0, "Silent Steps"));
        actions.add(new Action(0, 0, "Blend in the dark"));
    }

    @Override
    public void takeDamage(int damage) {
        if (random.nextInt(100) < abstractionMeter) {
            return;
        }
        super.takeDamage(damage);
    }

    @Override
    public int useSkill(int index) {
        if (index == 0) { // Crowbar Strike
            abstractionMeter = Math.max(0, abstractionMeter - 10);
        }
        else if (index == 1) { // Ambush
            abstractionMeter = Math.max(0, abstractionMeter - 25);
        }
        return super.useSkill(index);
    }

    @Override
    public void useAction(int index, Panel panel) {
        if (index == 0) { // Action 1: Hold your breath
            int gain = random.nextInt(11) + 10;
            abstractionMeter = Math.min(100, abstractionMeter + gain);
        }
        else if (index == 1) { // Action 2: Silent Steps
            int gain = random.nextInt(8) + 3;
            abstractionMeter = Math.min(100, abstractionMeter + gain);
        }
        else if (index == 2) { // Action 3: Blend in the dark
            int gain = random.nextInt(21) + 40;
            abstractionMeter = Math.min(100, abstractionMeter + gain);
        }
    }

    public int getAbstractionMeter() {
        return abstractionMeter;
    }
}