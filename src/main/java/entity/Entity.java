package entity;

import java.awt.*;

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

    public Entity(GamePanel gp) {
        this.gp = gp;
    }

    public void setLocation(int row, int col) {
        worldX = col * gp.tileSize;
        worldY = row * gp.tileSize;
    }
}
