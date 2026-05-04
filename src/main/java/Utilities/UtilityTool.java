package Utilities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import entity.Enemy.Enemy;
import entity.Enemy.Brighteyes;
import entity.Enemy.EnemyType;
import entity.Enemy.Scalper;
import entity.Enemy.Stillborn;
import entity.Player.Artist;
import entity.Player.Character;
import entity.Player.CharacterType;
import entity.Player.DebtCollector;
import entity.Player.Detective;
import entity.Player.Intruder;
import entity.Player.Officer;
import main.GamePanel;

public class UtilityTool {

    public static BufferedImage scaleImage(BufferedImage original, int width, int height) {
        BufferedImage scaledImage = new BufferedImage(width, height, original.getType());
        Graphics2D g2 = scaledImage.createGraphics();
        g2.drawImage(original, 0, 0, width, height, null);
        g2.dispose();

        return scaledImage;
    }

    public static Character characterFactory(CharacterType type, GamePanel gp) {
        return switch (type) {
        case DETECTIVE -> new Detective(gp);
        case COLLECTOR -> new DebtCollector(gp);
        case OFFICER -> new Officer(gp);
        case INTRUDER -> new Intruder(gp);
        case ARTIST -> new Artist(gp);
        };
    }

    public static Enemy enemyFactory(EnemyType type) {
        return switch (type) {
        case STILLBORN -> new Stillborn();
        case BRIGHTEYES -> new Brighteyes();
        case SCALPER -> new Scalper();
        };
    }

    public static Enemy getRandomEnemy() {
        EnemyType[] enemyTypes = EnemyType.values();
        return enemyFactory(enemyTypes[(int) (Math.random() * enemyTypes.length)]);
    }
}
