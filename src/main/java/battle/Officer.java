package battle;

public class Officer extends Character{
    public Officer() {
        super(70, .25, "Andrew - The Officer");
        skills.add(new Skill(10,30,"Warning Shot"));
        skills.add(new Skill(20,75,"Barrage of Bullets"));
        skills.add(new Skill(999,1000,"One Shot"));

        actions.add(new Action(.20,.40,"Block"));
        actions.add(new Action(.40,.60,"Stronger Block"));
        actions.add(new Action(.99,1,"Immunity"));
    }
}
