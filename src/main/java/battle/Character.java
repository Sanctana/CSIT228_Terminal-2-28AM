package battle;

import Inventory.Defibrillator;
import Inventory.IVFluids;
import Inventory.Item;
import Inventory.Scalpel;

import java.util.Random;
import entity.Entity;
import main.GamePanel;

public abstract class Character extends Entity {
    protected int maxHeartBeat = 200;
    protected Random random = new Random();

    public Character(int heartBeat, double resistance, String name, GamePanel gp) {
        super(gp);
        this.heartRate = heartBeat;
        this.resistance = resistance;
        this.name = name;
        this.initialResistance = resistance;

        this.inventory = new Item[3];

        this.inventory[0] = new Scalpel(3);
        this.inventory[1] = new Defibrillator(9);
        this.inventory[2] = new IVFluids(3);
    }

    public void setResistance(double action) {
        this.resistance = action;
    }

    public void resetResistance() {
        this.resistance = initialResistance;
    }

    public void takeDamage(int damage) {
        setHeartBeat(getHeartBeat() - (damage - (int) (damage * resistance)));
    }

    public void recover(int heal) {
        setHeartBeat(getHeartBeat() + heal);
    }

    public int getHeartBeat() {
        return heartRate;
    }

    public void setHeartBeat(int hb) {
        this.heartRate = hb;
    }

    public boolean getIsAlive() {
        return heartRate >= 40 && heartRate <= 180;
    }

    public String getName() {
        return name;
    }

    public Item[] getInventory() {
        return inventory;
    }

    public Item getItem(int index) {
        if (index >= 0 && index < inventory.length) {
            return inventory[index];
        }
        return null;
    }

    public int useSkill(int index) {
        if (index >= 0 && index < skills.size()) {
            return skills.get(index).getDamage();
        }
        return 0;
    }

    public void useItem(int index) {
        inventory[index].use(this);
    }

    public void useAction(int index, Panel panel) {
        if (index >= 0 && index < actions.size()) {
            this.setResistance(actions.get(index).action());
        }
    }
}
