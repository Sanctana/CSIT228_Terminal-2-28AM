package entity;

import java.awt.*;

public class Entity {
    public int worldX, worldY; // position in the world
    public int speed;

    // IDLES
    public Image idleUp, idleDown, idleLeft, idleRight, up, down, left, right;
    public Direction direction;
    public EntityState state;
    public Rectangle solidArea;
}
