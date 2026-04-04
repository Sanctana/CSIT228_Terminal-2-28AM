package tile;

import java.awt.image.BufferedImage;

public class Tile {
    // TILE
    public BufferedImage image;
    private TileType tileType;

    public Tile(BufferedImage image, TileType tileType) {
        this.image = image;
        this.tileType = tileType;
    }

    public Tile setTileType(TileType tileType) {
        this.tileType = tileType;
        return this;
    }

    public TileType getTileType() {
        return tileType;
    }

    @Override
    public String toString() {
        return "Tile{" +
                "tileType=" + tileType +
                '}';
    }
}
