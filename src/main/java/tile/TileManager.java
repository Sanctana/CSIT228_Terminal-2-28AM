package tile;

import main.GamePanel;
import main.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class TileManager {
    GamePanel gp;
    public Tile[] tile;
    public ArrayList<int[]> mapTileNum = new ArrayList<>();

    String mapFile;

    public TileManager(GamePanel gp, String mapFile) {
        this.gp = gp;
        this.mapFile = mapFile;

        tile = new Tile[50];
        mapTileNum = new ArrayList<>();

        getTileImage();
        loadMap();
    }

    public void getTileImage() {
        setup(00, "blackTiles", false);
        setup(01, "upperLeftCorner", true);
        setup(02, "upperRightCorner", true);
        setup(03, "lowerLeftCorner", true);
        setup(04, "lowerRightCorner", true);
        setup(05, "blackTilesWallHor", true);
        setup(06, "blackTilesWallVert", true);

        // NEW TILES
        setup(07, "0001", true);
        setup(8, "0002", true);
        setup(9, "0003", true);
        setup(10, "0004", true);
        setup(11, "0005", true);
        setup(12, "0006", true);
        setup(13, "0007", true);
        setup(14, "0008", true);
        setup(15, "0009", true);
        setup(16, "0010", true);
        setup(17, "0011", true);
        setup(18, "0012", true);
        setup(19, "0013", true);
        setup(20, "0014", true);
        setup(21, "0015", true);
        setup(22, "0016", true);
        setup(23, "0017", true);
        setup(24, "0018", true);
        setup(25, "0019", true);
        setup(26, "0020", true);
        setup(27, "0021", true);
        setup(28, "0022", true);
        setup(29, "0023", true);
        setup(30, "0024", true);

        setup(32, "UpperBed", false);
        setup(33, "lowerBed", false);

        setup(34, "upperBedV2", false);
        setup(35, "lowerBedV2", false);
    }

    public void setup(int index, String imageName, boolean collision) {
        UtilityTool uTool = new UtilityTool();

        try {
            BufferedImage originalImage = ImageIO.read(getClass().getResourceAsStream("/tiles/" + imageName + ".png"));
            tile[index] = new Tile(uTool.scaleImage(originalImage, gp.tileSize, gp.tileSize), collision);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Loads the map from a text file with dynamically determined dimensions
    public void loadMap() {
        Pattern pattern = Pattern.compile("\\s+");

        try (InputStream inputStream = getClass().getResourceAsStream("/maps/" + mapFile);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                mapTileNum.add(Arrays.stream(pattern.split(line.trim())).mapToInt(Integer::parseInt).toArray());
            }

            gp.maxWorldCol = mapTileNum.get(0).length;
            gp.maxWorldRow = mapTileNum.size();

        } catch (Exception e) {
            e.printStackTrace(); // or use a logger
        }
    }

    public void draw(Graphics2D g2) {
        for (int worldRow = 0; worldRow < gp.maxWorldRow; worldRow++) {
            for (int worldCol = 0; worldCol < gp.maxWorldCol; worldCol++) {
                int tileNum = mapTileNum.get(worldRow)[worldCol];

                int worldX = worldCol * gp.tileSize;
                int worldY = worldRow * gp.tileSize;

                int screenX = worldX - gp.player.worldX + gp.player.screenX;
                int screenY = worldY - gp.player.worldY + gp.player.screenY;

                if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
                        worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
                        worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
                        worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {

                    g2.drawImage(tile[tileNum].image, screenX, screenY, null);
                }
            }
        }
    }
}
