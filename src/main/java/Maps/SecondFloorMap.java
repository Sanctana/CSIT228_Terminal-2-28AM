package Maps;

import main.GamePanel;
import tile.Map;

public class SecondFloorMap extends Map {
    public SecondFloorMap(GamePanel gp) {
        super(gp, "2ndFloorMap.txt", "2ND FLOOR");
    }

    @Override
    public Map getNextMap() {
        return new FirstFloorMap(gp);
    }

    @Override
    public Map getPreviousMap() {
        return new ThirdFloorMap(gp);
    }
}
