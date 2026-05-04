package entity.player;

import main.KeyHandler;
import utilities.states.Direction;
import utilities.states.EntityState;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;

import main.GamePanel;
import battle.Panel;
import inventory.Defibrillator;
import inventory.IVFluids;
import inventory.Item;
import inventory.Scalpel;

public abstract class Character extends Entity {
    public final int screenX;
    public final int screenY;

    protected int maxHeartBeat = 200;
    protected KeyHandler keyH;

    public Character(int heartBeat, double resistance, String name, GamePanel gp) {
        super(gp);
        this.heartRate = heartBeat;
        this.resistance = resistance;
        this.name = name;
        this.initialResistance = resistance;
        this.keyH = gp.getKeyHandler();

        this.inventory = new Item[3];

        this.inventory[0] = new Scalpel(3);
        this.inventory[1] = new Defibrillator(9);
        this.inventory[2] = new IVFluids(3);

        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        this.solidArea = new Rectangle(15, 40, 20, 1);

        setDefaultValues();
    }

    public void setResistance(double action) {
        this.resistance = action;
    }

    public void resetResistance() {
        this.resistance = initialResistance;
    }

    public void recover(int heal) {
        setHeartBeat(getHeartBeat() + heal);
    }

    public String getName() {
        return name;
    }

    public Item[] getInventory() {
        return inventory;
    }

    public Item getItem(int index) {
        if (index >= 0 && index < inventory.length) {
            return inventory[index];
        }
        return null;
    }

    public int useSkill(int index) {
        if (index >= 0 && index < skills.size()) {
            return skills.get(index).getDamage();
        }
        return 0;
    }

    public void useItem(int index) {
        inventory[index].use(this);
    }

    public void useAction(int index, Panel panel) {
        if (index >= 0 && index < actions.size()) {
            this.setResistance(actions.get(index).action());
        }
    }

    public void setDefaultValues() {
        speed = 8;
        direction = Direction.DOWN; // DEFAULT
        state = EntityState.IDLE;
    }

    public void update() {
        if (keyH.upPressed) {
            direction = Direction.UP;
        } else if (keyH.downPressed) {
            direction = Direction.DOWN;
        } else if (keyH.leftPressed) {
            direction = Direction.LEFT;
        } else if (keyH.rightPressed) {
            direction = Direction.RIGHT;
        } else {
            state = EntityState.IDLE;
            return; // No movement, so exit early
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

    public void addItem(Item droppedItem) {
        if (droppedItem == null) return;

        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] != null && inventory[i].getClass().equals(droppedItem.getClass())) {
                inventory[i].setQuantity(inventory[i].getQuantity() + droppedItem.getQuantity());
                return;
            }
        }
    }


    public abstract String getPlayerPortraitPath();
}
