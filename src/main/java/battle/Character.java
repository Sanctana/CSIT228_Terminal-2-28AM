package battle;

import Inventory.Item;
import Inventory.Scalpel;
import Inventory.Defibrillator;
import Inventory.IVFluids;
import java.util.Random;
import java.util.ArrayList;
import entity.Entity;
import main.GamePanel;

public abstract class Character extends Entity{
    protected int maxHeartBeat = 200;
    protected Random random = new Random();

    public Character(int heartBeat, double resistance, String name, GamePanel gp) {
        super(gp);
        this.heartRate = heartBeat;
        this.resistance = resistance;
        this.name = name;
        this.initialResistance = resistance;

        this.inventory[0] = new Inventory.Scalpel();
        this.inventory[1] = new Inventory.Defibrillator();
        this.inventory[2] = new Inventory.IVFluids();
    }


    public void setResistance(double action) {
        this.resistance = action;
    }
    public void resetResistance() {
        this.resistance = initialResistance;
    }

    public void takeDamage(int damage) {
        int reduction = (int) (damage * resistance);
        int finalDamage = damage - reduction;
        setHeartBeat(getHeartBeat() - finalDamage);
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
    public int[] getItemAmounts() {
        return itemAmounts;
    }
    public int useSkill(int index) {
        if (index >= 0 && index < skills.size()) {
            return skills.get(index).getDamage();
        }
        return 0;
    }

    public void useItem(int index) {
        if (index >= 0 && index < inventory.length && itemAmounts[index] > 0) {
            Item item = inventory[index];
            if (item != null) {
                item.use(this);
                itemAmounts[index]--;
            }
        }
    }

    public void useAction(int index, Panel panel) {
        if (index >= 0 && index < actions.size()) {
            this.setResistance(actions.get(index).action());
        }
    }


}
