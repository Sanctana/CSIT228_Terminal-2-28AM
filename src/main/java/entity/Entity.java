package entity;

import java.awt.*;

public class Entity {
    public int worldX, worldY;
    public int speed;

    // IDLES
    public Image idleUp, idleDown, idleLeft, idleRight, up, down, left, right;
    public Direction direction;
    public boolean moving = false;
    public Rectangle solidArea;
    public boolean collisionOn = false;
}
