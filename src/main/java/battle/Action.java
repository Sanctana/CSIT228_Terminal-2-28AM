package battle;

public class Action {
    String name;
    double resistFloor;
    double resistCeil;
    private int maxCooldown;
    private int currentCooldown = 0;

    public Action(double resistFloor, double resistCeil, String name, int maxCooldown) {
        this.resistFloor = resistFloor;
        this.resistCeil = resistCeil;
        this.name = name;
        this.maxCooldown = maxCooldown;
    }

    public boolean isReady() {
        return currentCooldown <= 0;
    }
    public void triggerCooldown() {
        this.currentCooldown = maxCooldown;
    }
    public void tick() {
        if (currentCooldown > 0) currentCooldown--;
    }

    public double action() {
        return Math.random() * (resistCeil - resistFloor) + resistFloor;
    }

    public String getName() {
        return name;
    }

    public int getCurrentCooldown() {
        return currentCooldown;
    }

    public void setStats(double floor, double ceil, String name) {
        this.resistFloor = floor;
        this.resistCeil = ceil;
        this.name = name;
    }
}
