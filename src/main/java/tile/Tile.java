package tile;

import java.awt.image.BufferedImage;

public class Tile {
    // TILE
    public BufferedImage image;
    public boolean collision = false;
    private boolean moveNextMap = false;

    public Tile(BufferedImage image, boolean collision) {
        this.image = image;
        this.collision = collision;
    }

    public Tile(boolean moveNextMap) {
        this.moveNextMap = moveNextMap;
    }

    public Tile setNextMap(boolean moveNextMap) {
        this.moveNextMap = moveNextMap;
        return this;
    }

    public boolean isMoveNextMap() {
        return moveNextMap;
    }
}
