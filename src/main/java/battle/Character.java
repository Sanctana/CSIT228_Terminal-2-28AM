package battle;

import java.util.Random;

public class Character {
    private int heartBeat;
    private int maxHeartBeat = 200;
    private double resistance;
    private Random random = new Random();

    public Character(int heartBeat, double resistance) {
        this.heartBeat = heartBeat;
        this.resistance = resistance;
    }

    public int skill1() { return random.nextInt(11) + 10; }
    public int skill2() { return random.nextInt(11) + 20; }
    public int skill3() { return random.nextInt(16) + 30; }

    public void defend() { this.resistance = 0.75; }
    public void resetResistance() { this.resistance = 0.5; }

    public void takeDamage(int damage) {
        int finalDamage = (int) (damage * (1.0 - resistance));
        this.heartBeat -= finalDamage;
    }

    public void recover(int heal) {
        this.heartBeat += heal;
    }

    public int getHeartBeat() { return heartBeat; }
    public void setHeartBeat(int hb) { this.heartBeat = hb; }

    public boolean getIsAlive() {
        return heartBeat >= 40 && heartBeat <= 180;
    }
}