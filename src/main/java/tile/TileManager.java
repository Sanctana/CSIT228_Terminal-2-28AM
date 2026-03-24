package tile;

import main.GamePanel;
import main.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.regex.Pattern;

public class TileManager {
    GamePanel gp;
    public Tile[] tile;
    public int mapTileNum[][];

    public TileManager(GamePanel gp, String tileMap) {
        this.gp = gp;
        tile = new Tile[50];

        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
        getTileImage();
        loadMap(tileMap);
    }

    public void getTileImage() {
        setup(0, "blackTiles", false);
        setup(1, "upperLeftCorner", true);
        setup(2, "upperRightCorner", true);
        setup(3, "lowerLeftCorner", true);
        setup(4, "lowerRightCorner", true);
        setup(5, "blackTilesWallHor", true);
        setup(6, "blackTilesWallVert", true);

        // NEW TILES
        setup(7, "0001", true);
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
            tile[index] = new Tile(uTool.scaleImage(
                    ImageIO.read(getClass().getResourceAsStream("/tiles/" + imageName + ".png")), gp.tileSize,
                    gp.tileSize), collision);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMap(String tileMap) {
        Pattern pattern = Pattern.compile("\\s+");

        try (InputStream is = getClass().getResourceAsStream("/maps/" + tileMap);
                BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            for (int row = 0; row < gp.maxWorldRow; row++) {
                String line = br.readLine();

                if (line == null)
                    throw new IOException("Unexpected end of map file at row " + row);

                String[] numbers = pattern.split(line.trim());

                if (numbers.length < gp.maxWorldCol)
                    throw new IOException("Not enough columns on row " + row);

                int[] parsed = Arrays.stream(numbers).mapToInt(Integer::parseInt).toArray();
                for (int col = 0; col < gp.maxWorldCol; col++) {
                    mapTileNum[col][row] = parsed[col];
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // or use a logger
        }
    }

    public void draw(Graphics2D g2) {

        int worldCol = 0;
        int worldRow = 0;

        while (worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {

            int tileNum = mapTileNum[worldCol][worldRow];

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

            worldCol++;

            if (worldCol == gp.maxWorldCol) {
                worldCol = 0;
                worldRow++;
            }
        }
    }
}
