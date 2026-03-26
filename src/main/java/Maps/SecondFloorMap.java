package Maps;

import main.GamePanel;
import tile.Map;

public class SecondFloorMap extends Map {
    public SecondFloorMap(GamePanel gp) {
        super(gp, "2ndFloorMap.txt");

        setup("blackTiles", false); // Dummy tile for transitioning to next map
        tile.getLast().setNextMap(true);
    }

    @Override
    public Map getNextMap() {
        return null; // First floor but it's not yet implemented, so return null for now
    }
}
