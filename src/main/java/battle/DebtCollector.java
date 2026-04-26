package battle;

public class DebtCollector extends Character{
    public DebtCollector() {
        super(70, 0.20, "Yohann - The Debt Collector");
        skills.add(new Skill(20,50,"Due Date"));
        skills.add(new Skill(20,75,"Penalty"));
        skills.add(new Skill(0,0,"Property"));

        actions.add(new Action(.20,.40,"Block"));
        actions.add(new Action(.40,.60,"Stronger Block"));
        actions.add(new Action(0.99,1,"Immunity"));
    }
}
