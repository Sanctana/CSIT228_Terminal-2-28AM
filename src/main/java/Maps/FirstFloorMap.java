package Maps;

import main.GamePanel;
import tile.Map;

public class FirstFloorMap extends Map {
    public FirstFloorMap(GamePanel gp) {
        super(gp, "1stFloorMap.txt");

        setup("blackTiles", false);
        tile.getLast().setNextMap(true);
    }

    @Override
    public Map getNextMap() {
        return new ThirdFloorMap(gp);
    } // not sure pa kung mao ba ni padulngan hehe
}
