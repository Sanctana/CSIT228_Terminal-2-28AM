package battle;

import java.util.Random;

public class Character {

    private int damageOutput;
    private int heartBeat;
    private int maxHeartBeat;
    private double resistance;
    private boolean isAlive;

    private Random random = new Random();


    public Character(int maxHeartBeat) {
        this.maxHeartBeat = maxHeartBeat;
        this.heartBeat = maxHeartBeat;
        this.isAlive = true;
        this.resistance = 0.0;
        this.damageOutput = 0;
    }



    public int skill1() {
        this.damageOutput = random.nextInt(11) + 10;
        return this.damageOutput;
    }

    public int skill2() {
        this.damageOutput = random.nextInt(11) + 20;
        return this.damageOutput;
    }

    public void defend() {

        this.resistance = random.nextInt(31) + 30;
    }


    public int getHeartBeat() {
        return heartBeat;
    }

    public void setHeartBeat(int heartBeat) {
        this.heartBeat = heartBeat;

        if (this.heartBeat <= 0) {
            this.isAlive = false;
        }
    }

    public int getMaxHeartBeat() {
        return maxHeartBeat;
    }

    public void setMaxHeartBeat(int maxHeartBeat) {
        this.maxHeartBeat = maxHeartBeat;
    }

    public double getResistance() {
        return resistance;
    }

    public int getDamageOutput() {
        return damageOutput;
    }

    public boolean isAlive() {
        return isAlive;
    }
}
