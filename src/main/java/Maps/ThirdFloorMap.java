package Maps;

import main.GamePanel;

public class ThirdFloorMap extends Map {
    public ThirdFloorMap(GamePanel gp) {
        super(gp, "3rdFloorMap.txt", "3RD FLOOR");
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
