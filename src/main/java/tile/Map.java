package tile;

import main.GamePanel;
import main.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
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

    String mapFile;

    public Map(GamePanel gp, String mapFile) {
        this.gp = gp;
        this.mapFile = mapFile;

        tile = new ArrayList<>();
        mapTileNum = new ArrayList<>();

        getTileImages();
        loadMap();
    }

    // Common tiles for all maps
    public void getTileImages() {
        setup("blackTiles", false); // 0
        setup("upperLeftCorner", true); // 1
        setup("upperRightCorner", true); // 2
        setup("lowerLeftCorner", true); // 3
        setup("lowerRightCorner", true); // 4
        setup("blackTilesWallHor", true); // 5
        setup("blackTilesWallVert", true); // 6

        // NEW TILES
        setup("0001", true); // 7
        setup("0002", true); // 8
        setup("0003", true); // 9
        setup("0004", true); // 10
        setup("0005", true); // 11
        setup("0006", true); // 12
        setup("0007", true); // 13
        setup("0008", true); // 14
        setup("0009", true); // 15
        setup("0010", true); // 16
        setup("0011", true); // 17
        setup("0012", true); // 18
        setup("0013", true); // 19
        setup("0014", true); // 20
        setup("0015", true); // 21
        setup("0016", true); // 22
        setup("0017", true); // 23
        setup("0018", true); // 24
        setup("0019", true); // 25
        setup("0020", true); // 26
        setup("0021", true); // 27
        setup("0022", true); // 28
        setup("0023", true); // 29
        setup("0024", true); // 30

        setup("UpperBed", false); // 31
        setup("lowerBed", false); // 32

        setup("upperBedV2", false); // 33
        setup("lowerBedV2", false); // 34
    }

    public void setup(String imageName, boolean collision) {
        UtilityTool uTool = new UtilityTool();

        try {
            BufferedImage originalImage = ImageIO.read(getClass().getResourceAsStream("/tiles/" + imageName + ".png"));
            tile.add(new Tile(uTool.scaleImage(originalImage, gp.tileSize, gp.tileSize), collision));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Loads the map from a text file with dynamically determined dimensions
    public void loadMap() {
        Pattern pattern = Pattern.compile("\s+");

        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream("/maps/" + mapFile)))) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                mapTileNum.add(Arrays.stream(pattern.split(trimmed)).mapToInt(Integer::parseInt).toArray());
            }

            if (mapTileNum.isEmpty()) {
                throw new IOException("Map file is empty or not properly formatted.");
            }

            maxWorldRow = mapTileNum.size();
            maxWorldCol = mapTileNum.get(0).length; // Assuming all rows have the same number of columns
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        int leftCol = Math.max(0, (gp.player.worldX - gp.player.screenX) / gp.tileSize - 1);
        int rightCol = (gp.player.worldX + gp.player.screenX) / gp.tileSize + 1;
        int topRow = Math.max(0, (gp.player.worldY - gp.player.screenY) / gp.tileSize - 1);
        int bottomRow = Math.min(maxWorldRow - 1, (gp.player.worldY + gp.player.screenY) / gp.tileSize + 1);

        for (int worldRow = topRow; worldRow <= bottomRow; worldRow++) {
            int[] rowTiles = mapTileNum.get(worldRow);
            if (rowTiles.length == 0) {
                continue;
            }

            int rowLeftCol = leftCol;
            int rowRightCol = Math.min(rightCol, rowTiles.length - 1);

            if (rowLeftCol > rowRightCol) {
                continue;
            }

            for (int worldCol = rowLeftCol; worldCol <= rowRightCol; worldCol++) {
                int tileIndex = rowTiles[worldCol];
                if (tileIndex < 0 || tileIndex >= tile.size()) {
                    continue;
                }

                int worldX = worldCol * gp.tileSize;
                int worldY = worldRow * gp.tileSize;
                int screenX = worldX - gp.player.worldX + gp.player.screenX;
                int screenY = worldY - gp.player.worldY + gp.player.screenY;

                g2.drawImage(tile.get(tileIndex).image, screenX, screenY, null);
            }
        }
    }

    public abstract Map getNextMap();
}
