package Maps;

import main.GamePanel;
import tile.Map;

public class ThirdFloorMap extends Map {
    public ThirdFloorMap(GamePanel gp) {
        super(gp, "3rdFloorMap.txt");

        setup("blackTiles", false); // Dummy tile for transitioning to next map
        tile.getLast().setNextMap(true);
    }

    @Override
    public Map getNextMap() {
        return new SecondFloorMap(gp);
    }
}
