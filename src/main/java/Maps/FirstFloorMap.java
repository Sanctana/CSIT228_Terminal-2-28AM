package Maps;

import entity.Enemy.Enemy;
import entity.Enemy.Boss.GuidanceP1;
import main.GamePanel;

public class FirstFloorMap extends Map {
    public FirstFloorMap(GamePanel gp) {
        super(gp, "1stFloorMap.txt", "1ST FLOOR");
    }

    @Override
    public Map getNextMap() {
        return null;
    }

    @Override
    public Map getPreviousMap() {
        return new SecondFloorMap(gp);
    }

    @Override
    public Enemy createBoss() {
        return new GuidanceP1();
    }
}
