package Utilities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import entity.CharacterType;
import entity.DebtCollector;
import entity.Detective;
import entity.Intruder;
import entity.Officer;
import main.GamePanel;
import entity.Artist;
import entity.Character;

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
}
