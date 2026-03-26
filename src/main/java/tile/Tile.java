package tile;

import java.awt.image.BufferedImage;

public class Tile {
    // TILE
    public BufferedImage image;
    public boolean collision = false;

    public Tile(BufferedImage image, boolean collision) {
        this.image = image;
        this.collision = collision;
    }
}
