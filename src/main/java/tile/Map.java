package tile;

import javax.imageio.ImageIO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.Objects;
import java.util.Comparator;
import java.util.Arrays;
import java.awt.Point;
import java.awt.Graphics2D;

import main.GamePanel;
import Utilities.UtilityTool;
import Utilities.States.EntityState;
import Utilities.States.TileType;

public abstract class Map {
    public GamePanel gp;
    public Tile[] tile;
    public ArrayList<int[]> mapTileNum;

    public volatile int maxWorldCol;
    public volatile int maxWorldRow;

    private final String mapFile;
    private final String mapName;

    public Map(GamePanel gp, String mapFile, String mapName) {
        this.gp = gp;
        this.mapFile = mapFile;
        this.mapName = mapName;

        mapTileNum = new ArrayList<int[]>();

        try {
            getTileImages();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Common tiles for all maps
    public void getTileImages() throws IOException {
        List<Path> tilePaths;
        try (Stream<Path> paths = Files.list(Paths.get("src/main/resources/tiles"))) {
            tilePaths = paths.sorted(Comparator.comparing(Path::getFileName)).toList();
        }

        tile = new Tile[tilePaths.size()];

        for (Path path : tilePaths) {
            String[] splits = path.getFileName().toString().split("_");

            try {
                int index = Integer.parseInt(splits[0]);
                String tileTypeFromSplit = splits[2];
                int tileType = Integer.parseInt(tileTypeFromSplit.substring(0, tileTypeFromSplit.lastIndexOf('.')));

                tile[index] = new Tile(UtilityTool.scaleImage(
                        ImageIO.read(
                                Objects.requireNonNull(getClass().getResourceAsStream("/tiles/" + path.getFileName()))),
                        gp.tileSize, gp.tileSize), TileType.values()[tileType]);
            } catch (IOException | NumberFormatException e) {
                System.out.println(e.getMessage());
            }
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
                new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/maps/" + mapFile))))) {

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

                g2.drawImage(tile[tileIndex].image, screenX, screenY, null);
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
