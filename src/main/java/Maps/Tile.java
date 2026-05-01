package Maps;

import java.awt.image.BufferedImage;

import Utilities.States.TileType;

public class Tile {
    // TILE
    public BufferedImage image;
    private TileType tileType;

    public Tile(BufferedImage image, TileType tileType) {
        this.image = image;
        this.tileType = tileType;
    }

    public TileType getTileType() {
        return tileType;
    }
}
