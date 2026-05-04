package maps;

import entity.enemy.Enemy;
import entity.enemy.boss.GuidanceP1;
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

    @Override
    public Enemy createBoss() {
        return new GuidanceP1();
    }
}
