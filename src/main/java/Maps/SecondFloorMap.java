package Maps;

import entity.Enemy.Enemy;
import entity.Enemy.Boss.GuidanceP2;
import main.GamePanel;

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

    @Override
    public Enemy createBoss() {
        return new GuidanceP2();
    }
}
