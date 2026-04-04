package Maps;

import main.GamePanel;
import tile.Map;
import tile.TileType;

public class FirstFloorMap extends Map {
    public FirstFloorMap(GamePanel gp) {
        super(gp, "1stFloorMap.txt");

        setup("blackTiles", TileType.WALKABLE); // 36
    }

    @Override
    public Map getNextMap() {
        return new ThirdFloorMap(gp);
    }

    @Override
    public Map getPreviousMap() {
        return new SecondFloorMap(gp);
    }
}
