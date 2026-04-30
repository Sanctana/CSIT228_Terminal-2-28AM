package battle;

public class Action {
    String name;
    double resistFloor;
    double resistCeil;

    public Action(double resistFloor, double resistCeil, String name) {
        this.resistFloor = resistFloor;
        this.resistCeil = resistCeil;
        this.name = name;
    }

    public double action() {
        return Math.random() * (resistCeil - resistFloor) + resistFloor;
    }

    public String getName() {
        return name;
    }
}
