package entity;

import main.GamePanel;
import main.KeyHandler;

import java.awt.*;

public class Player extends Entity {
    GamePanel gp;
    KeyHandler keyH;

    public final int screenX;
    public final int screenY;
    public String characterType;

    public Player(GamePanel gp, KeyHandler keyH, String characterType) {
        this.gp = gp;
        this.keyH = keyH;
        this.characterType = characterType;

        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        solidArea = new Rectangle();
        solidArea.x = 15;
        solidArea.y = 40;
        solidArea.width = 20;
        solidArea.height = 1;

        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        worldX = gp.tileSize * 7;
        worldY = gp.tileSize * 5;
        speed = 3;
        direction = "down"; // DEFAULT
    }

    public void getPlayerImage() {
        if(characterType.equals("detective")) {
            idleUp = new javax.swing.ImageIcon(getClass().getResource("/player/Detective/Back_Detective_Idle.png")).getImage();
            idleDown = new javax.swing.ImageIcon(getClass().getResource("/player/Detective/Front_Detective_Idle.png")).getImage();
            idleLeft = new javax.swing.ImageIcon(getClass().getResource("/player/Detective/Left_Detective_Idle.png")).getImage();
            idleRight = new javax.swing.ImageIcon(getClass().getResource("/player/Detective/Right_Detective_Idle.png")).getImage();

            up = new javax.swing.ImageIcon(getClass().getResource("/player/Detective/Back_Detective.gif")).getImage();
            down = new javax.swing.ImageIcon(getClass().getResource("/player/Detective/Front_Detective.gif")).getImage();
            left = new javax.swing.ImageIcon(getClass().getResource("/player/Detective/Left_Detective.gif")).getImage();
            right = new javax.swing.ImageIcon(getClass().getResource("/player/Detective/Right_Detective.gif")).getImage();
        }
        else if(characterType.equals("officer")) {
            idleUp = new javax.swing.ImageIcon(getClass().getResource("/player/Officer/Back_Officer_Idle.png")).getImage();
            idleDown = new javax.swing.ImageIcon(getClass().getResource("/player/Officer/Front_Officer_Idle.png")).getImage();
            idleLeft = new javax.swing.ImageIcon(getClass().getResource("/player/Officer/Left_Officer_Idle.png")).getImage();
            idleRight = new javax.swing.ImageIcon(getClass().getResource("/player/Officer/Right_Officer_Idle.png")).getImage();

            up = new javax.swing.ImageIcon(getClass().getResource("/player/Officer/Back_Officer.gif")).getImage();
            down = new javax.swing.ImageIcon(getClass().getResource("/player/Officer/Front_Officer.gif")).getImage();
            left = new javax.swing.ImageIcon(getClass().getResource("/player/Officer/Left_Officer.gif")).getImage();
            right = new javax.swing.ImageIcon(getClass().getResource("/player/Officer/Right_Officer.gif")).getImage();
        }
    }

    public void update() {

        if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
            if (keyH.upPressed) {
                direction = "up";
            } else if (keyH.downPressed) {
                direction = "down";
            } else if (keyH.leftPressed) {
                direction = "left";
            } else if (keyH.rightPressed) {
                direction = "right";
            }

            moving = true;

            collisionOn = false;
            gp.cChecker.checkTile(this);

            if (collisionOn == false) {
                switch (direction) {
                    case "up":
                        worldY -= speed;
                        break;
                    case "down":
                        worldY += speed;
                        break;
                    case "left":
                        worldX -= speed;
                        break;
                    case "right":
                        worldX += speed;
                        break;
                }
            }

        } else {
            moving = false;
        }
    }

    public void draw(Graphics2D g2) {

        Image image = null;

        if (moving) {
            switch (direction) {
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
            }
        } else {
            switch (direction) {
                case "up":
                    image = idleUp;
                    break;
                case "down":
                    image = idleDown;
                    break;
                case "left":
                    image = idleLeft;
                    break;
                case "right":
                    image = idleRight;
                    break;
            }
        }

        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
    }
}
