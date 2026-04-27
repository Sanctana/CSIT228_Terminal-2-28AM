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

        setup("UpperBedVerticalWall", TileType.COLLISION_TILE); // 33
        setup("UpperBedBottom", TileType.WALKABLE); // 34

        setup("SpawnPoint", TileType.SPAWN_POINT); // 35
        setup("blackTiles", TileType.TO_NEXT_MAP); // 36
        setup("blackTilesFlower", TileType.COLLISION_TILE); // 37
        setup("LeftDoorHor", TileType.COLLISION_TILE); // 38
        setup("RightDoorHor", TileType.COLLISION_TILE); // 39
        setup("blackTiles", TileType.TO_PREVIOUS_MAP); // 40
        setup("floorTileBrighter", TileType.BATTLE_TRIGGER); // 41

        setup("CabinetTopVerticalWall", TileType.COLLISION_TILE); // 42
        setup("CabinetBottom", TileType.COLLISION_TILE); // 43
        setup("chartAnatomy1", TileType.COLLISION_TILE); // 44
        setup("chartAnatomy2", TileType.COLLISION_TILE); // 45
        setup("tableMedicine1", TileType.WALKABLE); // 46
        setup("tableMedicine2", TileType.COLLISION_TILE); // 47
        setup("tableMonitor1", TileType.WALKABLE); // 48
        setup("tableMonitor2", TileType.COLLISION_TILE); // 49
        setup("flowerV1a", TileType.WALKABLE); //50
        setup("flowerV1b", TileType.COLLISION_TILE); //51

        setup("LeftArrow", TileType.COLLISION_TILE); //52
        setup("RightArrow", TileType.COLLISION_TILE); //53

        setup("tileBlood1", TileType.WALKABLE); //54
        setup("tileBlood2", TileType.WALKABLE); // 55
        setup("tileBlood3", TileType.WALKABLE); // 56

        setup("UpperBenchTopLeft", TileType.COLLISION_TILE); //57
        setup("UpperBenchBottomLeft", TileType.WALKABLE); // 58
        setup("UpperBenchTopRight", TileType.COLLISION_TILE); //59
        setup("UpperBenchBottomRight", TileType.WALKABLE); //60

        setup("SpawnPointDoor", TileType.WALKABLE); // 61

        setup("LeftBenchTopLeft", TileType.COLLISION_TILE); // 62
        setup("LeftBenchBottomLeft", TileType.WALKABLE); // 63
        setup("LeftBenchTopRight", TileType.COLLISION_TILE); //64
        setup("LeftBenchBottomRight", TileType.WALKABLE); //65

        setup("0021BlackV1", TileType.COLLISION_TILE); //66
        setup("0021BlackV2", TileType.COLLISION_TILE); //67
        setup("0021BlackV3", TileType.COLLISION_TILE); //68
        setup("0021BlackV4", TileType.COLLISION_TILE); //69

        setup("blackTilesWallHorBlackV1", TileType.COLLISION_TILE); // 70
        setup("blackTilesWallHorBlackV2", TileType.COLLISION_TILE); // 71
        setup("blackTilesWallVertBlackV1", TileType.COLLISION_TILE); // 72
        setup("blackTilesWallVertBlackV2", TileType.COLLISION_TILE); // 73

        setup("upperCornerV1", TileType.COLLISION_TILE); //74
        setup("upperCornerV2", TileType.COLLISION_TILE); //75

        setup("bottomCornerV1", TileType.COLLISION_TILE); //76
        setup("bottomCornerV2", TileType.COLLISION_TILE); //77

        setup("blackTile", TileType.COLLISION_TILE); // 78

        setup("0021BlackV5", TileType.COLLISION_TILE); //79
        setup("upperCornerV3", TileType.COLLISION_TILE); //80
        setup("upperCornerV4", TileType.COLLISION_TILE); //81

        setup("BottomBenchTopLeft", TileType.COLLISION_TILE); // 82
        setup("BottomBenchBottomLeft", TileType.WALKABLE); // 83
        setup("BottomBenchTopRight", TileType.COLLISION_TILE); //84
        setup("BottomBenchBottomRight", TileType.WALKABLE); //85

        setup("DoorHorV1", TileType.COLLISION_TILE); //86
        setup("DoorHorV2", TileType.COLLISION_TILE); //87
        setup("DoorHorV3", TileType.COLLISION_TILE); //88
        setup("DoorHorV4", TileType.COLLISION_TILE); //89

        setup("DoorSingleV1", TileType.WALKABLE); //90
        setup("DoorSingleV2", TileType.WALKABLE); //91
        setup("DoorSingleV3", TileType.WALKABLE); //92
        setup("DoorSingleV4", TileType.WALKABLE); //93

        setup("0021BlackV7", TileType.COLLISION_TILE); //94
        setup("0021BlackV8", TileType.COLLISION_TILE); //95
        setup("0021BlackV9", TileType.COLLISION_TILE); //96
        setup("upperCornerV5", TileType.COLLISION_TILE); //97
        setup("upperCornerV6", TileType.COLLISION_TILE); //98
        setup("0021BlackV6", TileType.COLLISION_TILE); //99
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
