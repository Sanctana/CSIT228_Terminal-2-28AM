package entity;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

import Utilities.States.Direction;
import main.GamePanel;
import main.KeyHandler;
import battle.Character;

public class Player extends Character {
    public final int screenX;
    public final int screenY;
    public CharacterType characterType;

    KeyHandler keyH;

    public Player(GamePanel gp, KeyHandler keyH, CharacterType characterType) {
        super(70, 0.1, "TestChar", gp);
        this.keyH = keyH;
        this.characterType = characterType;

        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        this.solidArea = new Rectangle(15, 40, 20, 1);

        this.inventory[0] = new Inventory.Scalpel();
        this.inventory[1] = new Inventory.Defibrillator();
        this.inventory[2] = new Inventory.IVFluids();

        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        speed = 4;
        direction = Direction.DOWN; // DEFAULT
    }

    public void setLocation(int row, int col) {
        worldY = gp.tileSize * row;
        worldX = gp.tileSize * col;
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

            gp.cChecker.checkTile(this);

            if (state == EntityState.MOVING) {
                switch (direction) {
                case UP -> worldY -= speed;
                case DOWN -> worldY += speed;
                case LEFT -> worldX -= speed;
                case RIGHT -> worldX += speed;
                }
            }
        } else {
            state = EntityState.IDLE;
        }
    }

    public void draw(Graphics2D g2) {
        Image image = null;

        if (state == EntityState.MOVING) {
            switch (direction) {
            case UP -> image = up;
            case DOWN -> image = down;
            case LEFT -> image = left;
            case RIGHT -> image = right;
            }
        } else if (state == EntityState.IDLE) {
            switch (direction) {
            case UP -> image = idleUp;
            case DOWN -> image = idleDown;
            case LEFT -> image = idleLeft;
            case RIGHT -> image = idleRight;
            }
        }

        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
    }

    public void storeCurrentPosition() {
        // Align to tile grid
        int x = worldX / gp.tileSize;
        int y = worldY / gp.tileSize;

        // Store the current position before transitioning
        switch (direction) {
        case UP -> y++;
        case DOWN -> y--;
        case LEFT -> x++;
        case RIGHT -> x--;
        }

        gp.previousPlayerPositions.push(new Point(x, y));
    }

    public void restorePreviousPosition() {
        if (!gp.previousPlayerPositions.isEmpty()) {
            Point previousPosition = gp.previousPlayerPositions.pop();
            setLocation(previousPosition.y, previousPosition.x);
        }
    }
}
