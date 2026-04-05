package battle;

public class Officer extends Character{
    public Officer() {
        super(70, .25, "Andrew - The Officer");
        skills.add(new Skill(10,30,"Warning Shot"));
        skills.add(new Skill(20,75,"Barrage of Bullets"));
        skills.add(new Skill(5,15,"Pistol Whip"));
    }
}
