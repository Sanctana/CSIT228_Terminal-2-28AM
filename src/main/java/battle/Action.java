package battle;

import java.util.Random;

public class Action {
    Random random = new Random();
    String name;
    double resistFloor;
    double resistCeil;

    public Action(double resistFloor, double resistCeil, String name) {
        this.resistFloor = resistFloor;
        this.resistCeil = resistCeil;
        this.name = name;
    }

    public double action() {
        return random.nextDouble(resistFloor, resistCeil);
    }

    public String getName() {
        return name;
    }
}
