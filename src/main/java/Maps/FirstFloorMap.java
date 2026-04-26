package Maps;

import Utilities.States.TileType;
import main.GamePanel;
import tile.Map;

public class FirstFloorMap extends Map {
    public FirstFloorMap(GamePanel gp) {
        super(gp, "1stFloorMap.txt", "1ST FLOOR");

        setup("blackTiles", TileType.TO_PREVIOUS_MAP); // 37
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
