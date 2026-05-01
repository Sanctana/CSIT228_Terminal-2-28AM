package Maps;

import main.GamePanel;

public class FirstFloorMap extends Map {
    public FirstFloorMap(GamePanel gp) {
        super(gp, "1stFloorMap.txt", "1ST FLOOR");
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
