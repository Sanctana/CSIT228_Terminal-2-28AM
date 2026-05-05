package battle.ability;

public class Action extends Ability {
    private double resistFloor;
    private double resistCeil;

    public Action(double resistFloor, double resistCeil, String name, int maxCooldown) {
        super(name, maxCooldown);
        this.resistFloor = resistFloor;
        this.resistCeil = resistCeil;
    }

    public double action() {
        triggerCooldown();
        return Math.random() * (resistCeil - resistFloor) + resistFloor;
    }

    public void setStats(double floor, double ceil, String name) {
        this.resistFloor = floor;
        this.resistCeil = ceil;
        setName(name);
    }
}
