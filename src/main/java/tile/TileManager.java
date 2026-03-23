package tile;

import main.GamePanel;
import main.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TileManager {
    GamePanel gp;
    public Tile[] tile;
    public int mapTileNum[][];

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tile = new Tile[50];

        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
        getTileImage();
        //loadMap("/maps/2ndFloorMap.txt");

        loadMap();
    }

    private void loadMap() {
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

    public void loadMap(String s) {
        try {
            InputStream is = getClass().getResourceAsStream("/maps/3rdFloorMap.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int col = 0;
            int row = 0;

            while (col < gp.maxWorldCol && row < gp.maxWorldRow) {
                String line = br.readLine();

                while (col < gp.maxWorldCol) {
                    String numbers[] = line.split("\\s+");
                    int num = Integer.parseInt(numbers[col]);

                    mapTileNum[col][row] = num;
                    col++;
                }
                if (col == gp.maxWorldCol) {
                    col = 0;
                    row++;
                }
            }
            br.close();
        } catch (Exception e) {
            // e.printStackTrace();
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
