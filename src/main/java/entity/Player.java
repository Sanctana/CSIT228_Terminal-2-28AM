package entity;

import test.characterdisplay.GamePanel;
import test.characterdisplay.KeyHandler;

import java.awt.*;

public class Player extends Entity {
    GamePanel gp;
    KeyHandler keyH;

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;

        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        x = 100;
        y = 100;
        speed = 4;
        direction = "down";
    }

    public void getPlayerImage() {
        idle = new javax.swing.ImageIcon(getClass().getResource("/player/idle.gif")).getImage();
        up = new javax.swing.ImageIcon(getClass().getResource("/player/upWalk.gif")).getImage();
        down = new javax.swing.ImageIcon(getClass().getResource("/player/downWalk.gif")).getImage();
        left = new javax.swing.ImageIcon(getClass().getResource("/player/leftWalk.gif")).getImage();
        right = new javax.swing.ImageIcon(getClass().getResource("/player/rightWalk.gif")).getImage();
    }

    public void update() {
        if(keyH.upPressed) {
            direction = "up";
            y -= speed;
        }
        else if(keyH.downPressed) {
            direction = "down";
            y += speed;
        }
        else if(keyH.leftPressed) {
            direction = "left";
            x -= speed;
        }
        else if(keyH.rightPressed) {
            direction = "right";
            x += speed;
        }
        else{
            direction = "idle";
        }
    }
    public void draw(Graphics2D g2 ) {
        Image image = null;

        switch(direction) {
            case "up":
                image = up;
                break;
            case "down":
                image = down;
                break;
            case "left":
                image = left;
                break;
            case "right":
                image = right;
                break;
            default:
                image = idle;
                break;
        }
        g2.drawImage(image, x, y, gp.tileSize,  gp.tileSize, null);
    }
}
