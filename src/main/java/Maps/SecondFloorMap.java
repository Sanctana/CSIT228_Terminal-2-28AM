package Maps;

import main.GamePanel;
import tile.Map;
import tile.TileType;

public class SecondFloorMap extends Map {
    public SecondFloorMap(GamePanel gp) {
        super(gp, "2ndFloorMap.txt");

        setup("blackTiles", TileType.TO_NEXT_MAP); // 36
        setup("blackTiles", TileType.TO_PREVIOUS_MAP); // 37
    }

    @Override
    public Map getNextMap() {
        return new FirstFloorMap(gp); // First floor but it's not yet implemented, so return null for now
    }

    @Override
    public Map getPreviousMap() {
        return new ThirdFloorMap(gp); // Third floor but it's not yet implemented, so return null for now
    }
}
