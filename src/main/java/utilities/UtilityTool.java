package utilities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import entity.enemy.Brighteyes;
import entity.enemy.Enemy;
import entity.enemy.EnemyType;
import entity.enemy.Scalper;
import entity.enemy.Stillborn;
import entity.player.Artist;
import entity.player.Character;
import entity.player.CharacterType;
import entity.player.DebtCollector;
import entity.player.Detective;
import entity.player.Intruder;
import entity.player.Officer;
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

    public static CharacterType getCharacterType(Character player) {
        if (player instanceof Detective) {
            return CharacterType.DETECTIVE;
        }
        if (player instanceof Officer) {
            return CharacterType.OFFICER;
        }
        if (player instanceof Intruder) {
            return CharacterType.INTRUDER;
        }
        if (player instanceof Artist) {
            return CharacterType.ARTIST;
        }
        if (player instanceof DebtCollector) {
            return CharacterType.COLLECTOR;
        }
        return CharacterType.DETECTIVE;
    }
}
