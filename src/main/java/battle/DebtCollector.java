package battle;

public class DebtCollector extends Character{
    public DebtCollector() {
        super(70, 0.20, "Yohann - The Debt Collector");
        skills.add(new Skill(20,50,"Due Date"));
        skills.add(new Skill(20,75,"Penalty"));
        skills.add(new Skill(0,0,"Property"));
    }
}
