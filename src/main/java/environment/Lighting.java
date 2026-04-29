package environment;

import main.GamePanel;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.RadialGradientPaint;
import java.awt.AlphaComposite;
import java.awt.image.BufferedImage;

public class Lighting {
    private final GamePanel gp;
    private final int circleSize;
    private final Color[] color = {
            new Color(0, 0, 0, 0f),
            new Color(0, 0, 0, 0.25f),
            new Color(0, 0, 0, 0.5f),
            new Color(0, 0, 0, 0.75f),
            new Color(0, 0, 0, 0.98f)
    };
    private final float[] fraction = { 0f, 0.25f, 0.5f, 0.75f, 1f };

    BufferedImage darknessFilter;
    boolean enabled = true; // toggle flag

    public Lighting(GamePanel gp, int circleSize) {
        this.gp = gp;
        this.circleSize = circleSize;

        darknessFilter = new BufferedImage(gp.screenWidth, gp.screenHeight, BufferedImage.TYPE_INT_ARGB);
    }

    public void updateFilter() {
        if (gp.player == null)
            return;

        int centerX = gp.player.screenX + gp.tileSize / 2;
        int centerY = gp.player.screenY + gp.tileSize / 2;

        Graphics2D g2 = darknessFilter.createGraphics();
        try {
            g2.setComposite(AlphaComposite.Src);
            g2.setPaint(new RadialGradientPaint(centerX, centerY, circleSize / 2f, fraction, color));
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        } finally {
            g2.dispose();
        }
    }

    public void draw(Graphics2D g2) {
        if (enabled) {
            g2.drawImage(darknessFilter, 0, 0, null);
        }
    }
}
