package Maps;

import main.GamePanel;
import tile.Map;
import tile.TileType;

public class ThirdFloorMap extends Map {
    public ThirdFloorMap(GamePanel gp) {
        super(gp, "3rdFloorMap.txt");
    }

    @Override
    public Map getNextMap() {
        return new SecondFloorMap(gp);
    }

    @Override
    public Map getPreviousMap() {
        return null; // No previous map from the third floor
    }
}
