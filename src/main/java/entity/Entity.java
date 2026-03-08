package entity;

import java.awt.Image;

public class Entity {
    public int worldX, worldY;
    public int speed;

    //IDLES
    public Image idleUp, idleDown, idleLeft, idleRight, up, down, left, right;
    public String direction;
    public boolean moving = false;
}
