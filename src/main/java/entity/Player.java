package entity;

import test.characterdisplay.GamePanel;
import test.characterdisplay.KeyHandler;

import java.awt.*;

public class Player extends Entity {
    GamePanel gp;
    KeyHandler keyH;

    public final int screenX;
    public final int screenY;

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;

        screenX = gp.screenWidth / 2 - (gp.tileSize/2);
        screenY = gp.screenHeight / 2 - (gp.tileSize/2);

        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        worldX = gp.tileSize * 32;
        worldY = gp.tileSize *  32;
        speed = 4;
        direction = "down"; //DEFAULT
    }

    public void getPlayerImage() {
        idleUp = new javax.swing.ImageIcon(getClass().getResource("/player/idleUp.png")).getImage();
        idleDown = new javax.swing.ImageIcon(getClass().getResource("/player/idleDown.png")).getImage();
        idleLeft = new javax.swing.ImageIcon(getClass().getResource("/player/idleLeft.png")).getImage();
        idleRight = new javax.swing.ImageIcon(getClass().getResource("/player/idleRight.png")).getImage();

        up = new javax.swing.ImageIcon(getClass().getResource("/player/upWalk.gif")).getImage();
        down = new javax.swing.ImageIcon(getClass().getResource("/player/downWalk.gif")).getImage();
        left = new javax.swing.ImageIcon(getClass().getResource("/player/leftWalk.gif")).getImage();
        right = new javax.swing.ImageIcon(getClass().getResource("/player/rightWalk.gif")).getImage();
    }

    public void update() {
        moving = false;

        if(keyH.upPressed){
            direction = "up";
            worldY -= speed;
            moving = true;
        }

        if(keyH.downPressed){
            direction = "down";
            worldY += speed;
            moving = true;
        }

        if(keyH.leftPressed){
            direction = "left";
            worldX -= speed;
            moving = true;
        }

        if(keyH.rightPressed){
            direction = "right";
            worldX += speed;
            moving = true;
        }
    }

    public void draw(Graphics2D g2){

        Image image = null;

        if(moving){
            switch(direction){
                case "up": image = up; break;
                case "down": image = down; break;
                case "left": image = left; break;
                case "right": image = right; break;
            }
        } else {
            switch(direction){
                case "up": image = idleUp; break;
                case "down": image = idleDown; break;
                case "left": image = idleLeft; break;
                case "right": image = idleRight; break;
            }
        }

        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
    }
}
