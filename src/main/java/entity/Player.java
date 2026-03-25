package entity;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

import main.GamePanel;
import main.KeyHandler;

public class Player extends Entity {
    GamePanel gp;
    KeyHandler keyH;

    public int heartRate = 70; // current BPM
    public int minHeartRate = 40;
    public int maxHeartRate = 180;
    public final int screenX;
    public final int screenY;
    public CharacterType characterType;

    public Player(GamePanel gp, KeyHandler keyH, CharacterType characterType) {
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
        speed = 4;
        direction = Direction.DOWN; // DEFAULT
    }

    public void getPlayerImage() {
        if (characterType == CharacterType.DETECTIVE) {
            idleUp = new ImageIcon(getClass().getResource("/player/Detective/Back_Detective_Idle.png")).getImage();
            idleDown = new ImageIcon(getClass().getResource("/player/Detective/Front_Detective_Idle.png")).getImage();
            idleLeft = new ImageIcon(getClass().getResource("/player/Detective/Left_Detective_Idle.png")).getImage();
            idleRight = new ImageIcon(getClass().getResource("/player/Detective/Right_Detective_Idle.png")).getImage();

            up = new ImageIcon(getClass().getResource("/player/Detective/Back_Detective.gif")).getImage();
            down = new ImageIcon(getClass().getResource("/player/Detective/Front_Detective.gif")).getImage();
            left = new ImageIcon(getClass().getResource("/player/Detective/Left_Detective.gif")).getImage();
            right = new ImageIcon(getClass().getResource("/player/Detective/Right_Detective.gif")).getImage();
        } else if (characterType == CharacterType.OFFICER) {
            idleUp = new ImageIcon(getClass().getResource("/player/Officer/Back_Officer_Idle.png")).getImage();
            idleDown = new ImageIcon(getClass().getResource("/player/Officer/Front_Officer_Idle.png")).getImage();
            idleLeft = new ImageIcon(getClass().getResource("/player/Officer/Left_Officer_Idle.png")).getImage();
            idleRight = new ImageIcon(getClass().getResource("/player/Officer/Right_Officer_Idle.png")).getImage();

            up = new ImageIcon(getClass().getResource("/player/Officer/Back_Officer.gif")).getImage();
            down = new ImageIcon(getClass().getResource("/player/Officer/Front_Officer.gif")).getImage();
            left = new ImageIcon(getClass().getResource("/player/Officer/Left_Officer.gif")).getImage();
            right = new ImageIcon(getClass().getResource("/player/Officer/Right_Officer.gif")).getImage();
        } else if (characterType == CharacterType.INTRUDER) {
            idleUp = new ImageIcon(getClass().getResource("/player/Intruder/Back_Intruder_Idle.png")).getImage();
            idleDown = new ImageIcon(getClass().getResource("/player/Intruder/Front_Intruder_Idle.png")).getImage();
            idleLeft = new ImageIcon(getClass().getResource("/player/Intruder/Left_Intruder_Idle.png")).getImage();
            idleRight = new ImageIcon(getClass().getResource("/player/Intruder/Right_Intruder_Idle.png")).getImage();

            up = new ImageIcon(getClass().getResource("/player/Intruder/Back_Intruder.gif")).getImage();
            down = new ImageIcon(getClass().getResource("/player/Intruder/Front_Intruder.gif")).getImage();
            left = new ImageIcon(getClass().getResource("/player/Intruder/Left_Intruder.gif")).getImage();
            right = new ImageIcon(getClass().getResource("/player/Intruder/Right_Intruder.gif")).getImage();
        } else if (characterType == CharacterType.ARTIST) {
            idleUp = new ImageIcon(getClass().getResource("/player/Artist/Back_Artist_Idle.png")).getImage();
            idleDown = new ImageIcon(getClass().getResource("/player/Artist/Front_Artist_Idle.png")).getImage();
            idleLeft = new ImageIcon(getClass().getResource("/player/Artist/Left_Artist_Idle.png")).getImage();
            idleRight = new ImageIcon(getClass().getResource("/player/Artist/Right_Artist_Idle.png")).getImage();

            up = new ImageIcon(getClass().getResource("/player/Artist/Back_Artist.gif")).getImage();
            down = new ImageIcon(getClass().getResource("/player/Artist/Front_Artist.gif")).getImage();
            left = new ImageIcon(getClass().getResource("/player/Artist/Left_Artist.gif")).getImage();
            right = new ImageIcon(getClass().getResource("/player/Artist/Right_Artist.gif")).getImage();
        } else if (characterType == CharacterType.COLLECTOR) {
            idleUp = new ImageIcon(getClass().getResource("/player/Collector/Back_Collector_Idle.png")).getImage();
            idleDown = new ImageIcon(getClass().getResource("/player/Collector/Front_Collector_Idle.png")).getImage();
            idleLeft = new ImageIcon(getClass().getResource("/player/Collector/Left_Collector_Idle.png")).getImage();
            idleRight = new ImageIcon(getClass().getResource("/player/Collector/Right_Collector_Idle.png")).getImage();

            up = new ImageIcon(getClass().getResource("/player/Collector/Back_Collector.gif")).getImage();
            down = new ImageIcon(getClass().getResource("/player/Collector/Front_Collector.gif")).getImage();
            left = new ImageIcon(getClass().getResource("/player/Collector/Left_Collector.gif")).getImage();
            right = new ImageIcon(getClass().getResource("/player/Collector/Right_Collector.gif")).getImage();
        }
    }

    public void update() {

        if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
            if (keyH.upPressed) {
                direction = Direction.UP;
            } else if (keyH.downPressed) {
                direction = Direction.DOWN;
            } else if (keyH.leftPressed) {
                direction = Direction.LEFT;
            } else if (keyH.rightPressed) {
                direction = Direction.RIGHT;
            }

            moving = true;

            collisionOn = false;
            gp.cChecker.checkTile(this);

            if (collisionOn == false) {
                switch (direction) {
                    case UP:
                        worldY -= speed;
                        break;
                    case DOWN:
                        worldY += speed;
                        break;
                    case LEFT:
                        worldX -= speed;
                        break;
                    case RIGHT:
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
                case UP:
                    image = up;
                    break;
                case DOWN:
                    image = down;
                    break;
                case LEFT:
                    image = left;
                    break;
                case RIGHT:
                    image = right;
                    break;
            }
        } else {
            switch (direction) {
                case UP:
                    image = idleUp;
                    break;
                case DOWN:
                    image = idleDown;
                    break;
                case LEFT:
                    image = idleLeft;
                    break;
                case RIGHT:
                    image = idleRight;
                    break;
            }
        }

        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
    }
}
