package entity;

import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;

import Inventory.Item;
import battle.Action;
import battle.Skill;
import Utilities.States.Direction;
import main.GamePanel;

public abstract class Entity {
    public int worldX, worldY; // position in the world
    public int speed;
    protected GamePanel gp;

    // IDLES
    public Image idleUp, idleDown, idleLeft, idleRight, up, down, left, right;
    public Direction direction;
    public EntityState state;
    public Rectangle solidArea;

    public String name;
    public int heartRate = 70;
    public double resistance = 0.1;
    public double initialResistance = 0.1;
    public ArrayList<Skill> skills = new ArrayList<>();
    public ArrayList<Action> actions = new ArrayList<>();
    public Item[] inventory = new Item[3];
    public int[] itemAmounts = { 3, 9, 3 };

    public Entity(GamePanel gp) {
        this.gp = gp;
    }

    public int getHeartBeat() {
        return heartRate;
    }

    public void setHeartBeat(int bpm) {
        this.heartRate = bpm;
    }

    public boolean getIsAlive() {
        return heartRate >= 40 && heartRate <= 180;
    }

    public void takeDamage(int damage) {
        int finalDamage = (int) (damage - (damage * resistance));
        this.heartRate -= finalDamage;
    }

    public void setLocation(int row, int col) {
        worldX = col * gp.tileSize;
        worldY = row * gp.tileSize;
    }
}
