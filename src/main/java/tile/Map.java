package tile;

import main.GamePanel;
import entity.EntityState;
import Utilities.UtilityTool;
import Utilities.States.TileType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public abstract class Map {
    public GamePanel gp;
    public ArrayList<Tile> tile;
    public ArrayList<int[]> mapTileNum;

    public volatile int maxWorldCol;
    public volatile int maxWorldRow;

    private String mapFile;
    private String mapName;

    public Map(GamePanel gp, String mapFile, String mapName) {
        this.gp = gp;
        this.mapFile = mapFile;
        this.mapName = mapName;

        tile = new ArrayList<Tile>();
        mapTileNum = new ArrayList<int[]>();

        getTileImages();
    }

    // Common tiles for all maps
    public void getTileImages() {
        setup("blackTiles", TileType.WALKABLE); // 0
        setup("upperLeftCorner", TileType.COLLISION_TILE); // 1
        setup("upperRightCorner", TileType.COLLISION_TILE); // 2
        setup("lowerLeftCorner", TileType.COLLISION_TILE); // 3
        setup("lowerRightCorner", TileType.COLLISION_TILE); // 4
        setup("blackTilesWallHor", TileType.COLLISION_TILE); // 5
        setup("blackTilesWallVert", TileType.COLLISION_TILE); // 6

        // NEW TILES
        setup("0001", TileType.COLLISION_TILE); // 7
        setup("0002", TileType.COLLISION_TILE); // 8
        setup("0003", TileType.COLLISION_TILE); // 9
        setup("0004", TileType.COLLISION_TILE); // 10
        setup("0005", TileType.COLLISION_TILE); // 11
        setup("0006", TileType.COLLISION_TILE); // 12
        setup("0007", TileType.COLLISION_TILE); // 13
        setup("0008", TileType.COLLISION_TILE); // 14
        setup("0009", TileType.COLLISION_TILE); // 15
        setup("0010", TileType.COLLISION_TILE); // 16
        setup("0011", TileType.COLLISION_TILE); // 17
        setup("0012", TileType.COLLISION_TILE); // 18
        setup("0013", TileType.COLLISION_TILE); // 19
        setup("0014", TileType.COLLISION_TILE); // 20
        setup("0015", TileType.COLLISION_TILE); // 21
        setup("0016", TileType.COLLISION_TILE); // 22
        setup("0017", TileType.COLLISION_TILE); // 23
        setup("0018", TileType.COLLISION_TILE); // 24
        setup("0019", TileType.COLLISION_TILE); // 25
        setup("0020", TileType.COLLISION_TILE); // 26
        setup("0021", TileType.COLLISION_TILE); // 27
        setup("0022", TileType.COLLISION_TILE); // 28
        setup("0023", TileType.COLLISION_TILE); // 29
        setup("0024", TileType.COLLISION_TILE); // 30

        setup("UpperBed", TileType.WALKABLE); // 31
        setup("lowerBed", TileType.WALKABLE); // 32

        setup("upperBedV2", TileType.WALKABLE); // 33
        setup("lowerBedV2", TileType.WALKABLE); // 34

        setup("blackTiles", TileType.SPAWN_POINT); // 35
        setup("blackTiles", TileType.TO_NEXT_MAP); // 36
        setup("blackTilesFlower", TileType.COLLISION_TILE); // 37
        setup("LeftDoorHor", TileType.COLLISION_TILE); // 38
        setup("RightDoorHor", TileType.COLLISION_TILE); // 39
        setup("blackTiles", TileType.TO_PREVIOUS_MAP); // 40
        setup("floorTileBrighter", TileType.BATTLE_TRIGGER); // 41
    }

    public void setup(String imageName, TileType tileType) {
        try {
            tile.add(new Tile(
                    UtilityTool.scaleImage(ImageIO.read(getClass().getResourceAsStream("/tiles/" + imageName + ".png")),
                            gp.tileSize, gp.tileSize),
                    tileType));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the map from a text file with dynamically determined dimensions
     * 
     * @return Point representing the column and row of the player's spawn point
     */
    public Point loadMap() {
        Pattern pattern = Pattern.compile("\\s+");
        mapTileNum.clear();

        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream("/maps/" + mapFile)))) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                mapTileNum.add(Arrays.stream(pattern.split(line.trim())).mapToInt(Integer::parseInt).toArray());
            }

            if (mapTileNum.isEmpty()) {
                throw new IOException("Map file is empty or not properly formatted.");
            }

            maxWorldRow = mapTileNum.size();
            maxWorldCol = mapTileNum.get(0).length; // Assuming all rows have the same number of columns

            // Find the player's starting position (first occurrence of tile index 35)
            for (int row = 0; row < maxWorldRow; row++) {
                int[] cols = mapTileNum.get(row);
                for (int col = 0, len = cols.length; col < len; col++) {
                    if (cols[col] == 35) {
                        return new Point(col, row); // x = col, y = row
                    }
                }
            }

            throw new IOException("No spawn point (tile index 35) found in the map.");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void draw(Graphics2D g2) {
        int leftCol = Math.max(0, (gp.player.worldX - gp.player.screenX) / gp.tileSize - 1);
        int rightCol = (gp.player.worldX + gp.player.screenX) / gp.tileSize + 1;
        int topRow = Math.max(0, (gp.player.worldY - gp.player.screenY) / gp.tileSize - 1);
        int bottomRow = Math.min(maxWorldRow - 1, (gp.player.worldY + gp.player.screenY) / gp.tileSize + 1);

        for (int worldRow = topRow; worldRow <= bottomRow; worldRow++) {
            int[] rowTiles = mapTileNum.get(worldRow);

            int rowRightCol = Math.min(rightCol, rowTiles.length - 1);

            for (int worldCol = leftCol; worldCol <= rowRightCol; worldCol++) {
                int tileIndex = rowTiles[worldCol];

                int worldX = worldCol * gp.tileSize;
                int worldY = worldRow * gp.tileSize;
                int screenX = worldX - gp.player.worldX + gp.player.screenX;
                int screenY = worldY - gp.player.worldY + gp.player.screenY;

                g2.drawImage(tile.get(tileIndex).image, screenX, screenY, null);
            }
        }
    }

    public Map transitionToMap(EntityState state) {
        return switch (state) {
            case TO_NEXT_MAP -> getNextMap();
            case TO_PREVIOUS_MAP -> getPreviousMap();
            default -> this;
        };
    }

    public String getMapName() {
        return mapName;
    }

    public abstract Map getNextMap();

    public abstract Map getPreviousMap();
}
