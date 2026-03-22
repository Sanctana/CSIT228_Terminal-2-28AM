package environment;

import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Lighting {
    BufferedImage darknessFilter;
    boolean enabled = true; // toggle flag

    public Lighting(GamePanel gp, int circleSize) {
        updateFilter(gp, circleSize);
    }

    public void updateFilter(GamePanel gp, int circleSize) {
        darknessFilter = new BufferedImage(gp.screenWidth, gp.screenHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) darknessFilter.getGraphics();

        int centerX = gp.player.screenX + (gp.tileSize) / 2;
        int centerY = gp.player.screenY + (gp.tileSize) / 2;

        Color[] color = {
                new Color(0, 0, 0, 0f),
                new Color(0, 0, 0, 0.25f),
                new Color(0, 0, 0, 0.5f),
                new Color(0, 0, 0, 0.75f),
                new Color(0, 0, 0, 0.98f)
        };
        float[] fraction = { 0f, 0.25f, 0.5f, 0.75f, 1f };

        RadialGradientPaint gPaint = new RadialGradientPaint(centerX, centerY, (circleSize / 2), fraction, color);
        g2.setPaint(gPaint);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        g2.dispose();
    }

    public void draw(Graphics2D g2) {
        if (enabled) {
            g2.drawImage(darknessFilter, 0, 0, null);
        }
    }
}
