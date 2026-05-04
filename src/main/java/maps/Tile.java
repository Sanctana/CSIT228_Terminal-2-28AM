package maps;

import java.awt.image.BufferedImage;

import utilities.states.TileType;

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
