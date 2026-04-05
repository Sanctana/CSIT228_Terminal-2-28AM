package battle;

import java.util.Random;
import java.util.ArrayList;
public abstract class Character {
    protected String name;
    protected int heartBeat;
    protected int maxHeartBeat = 200;
    protected double resistance;
    protected double initialResistance;
    protected Random random = new Random();
    protected ArrayList<Skill> skills = new ArrayList<>();

    public Character(int heartBeat, double resistance, String name) {
        this.heartBeat = heartBeat;
        this.resistance = resistance;
        this.name = name;
        this.initialResistance = resistance;
    }


    public void defend() { this.resistance = 0.75; }
    public void resetResistance() { this.resistance = initialResistance; }

    public void takeDamage(int damage) {
        int reduction = (int) (damage * resistance);
        int finalDamage = damage - reduction;
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

    public String getName(){
        return name;
    }
}