package main;

import java.awt.*;

enum TitleScreenState {
    MAIN_MENU,
    CHARACTER_SELECT
}

public class UI {
    GamePanel gp;
    Graphics2D g2;

    Font arial_40, arial_80B;
    public String message;
    int messageCounter;
    public int commandNum;
    TitleScreenState titleScreenState;
    int pulseCounter;
    boolean pulseOn;

    public UI(GamePanel gp) {
        this.gp = gp;

        arial_40 = new Font("Arial", Font.PLAIN, 40);
        arial_80B = new Font("Arial", Font.BOLD, 80);

        titleScreenState = TitleScreenState.MAIN_MENU;
        messageCounter = commandNum = pulseCounter = 0;
    }

    public void showMessage(String text) {
        message = text;
    }

    public void draw(Graphics2D g2) {
        this.g2 = g2;

        g2.setFont(arial_40);
        g2.setColor(Color.white);

        if (gp.gameState == GameState.TITLE) {
            drawTitleScreen();
        }
        if (gp.gameState == GameState.PLAY) {
            drawPlayerUI();
        }
        if (gp.gameState == GameState.PAUSE) {
            drawPauseScreen();
        }
    }

    public void drawTitleScreen() {
        g2.setColor(new Color(0, 0, 0));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        if (titleScreenState == TitleScreenState.MAIN_MENU) {
            // TITLE NAME
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F));
            String text = "Terminal 2:28 AM";
            int x = getXforCenteredText(text);
            int y = gp.tileSize * 3;

            // SHADOW
            g2.setColor(Color.gray);
            g2.drawString(text, x + 5, y + 5);
            // MAIN COLOR
            g2.setColor(Color.white);
            g2.drawString(text, x, y);

            // MENU
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));

            text = "NEW GAME";
            x = getXforCenteredText(text);
            y += gp.tileSize * 3.5;
            g2.drawString(text, x, y);
            if (commandNum == 0) {
                g2.drawString(">", x - gp.tileSize, y);
            }

            text = "LOAD GAME";
            x = getXforCenteredText(text);
            y += gp.tileSize;
            g2.drawString(text, x, y);
            if (commandNum == 1) {
                g2.drawString(">", x - gp.tileSize, y);
            }

            text = "QUIT";
            x = getXforCenteredText(text);
            y += gp.tileSize;
            g2.drawString(text, x, y);
            if (commandNum == 2) {
                g2.drawString(">", x - gp.tileSize, y);
            }
        } else if (titleScreenState == TitleScreenState.CHARACTER_SELECT) {
            g2.setColor(Color.white);
            g2.setFont(g2.getFont().deriveFont(42F));

            String text = "Select your character";
            int x = getXforCenteredText(text);
            int y = gp.tileSize * 2;
            g2.drawString(text, x, y);

            text = "Detective";
            x = getXforCenteredText(text);
            y += gp.tileSize * 2;
            g2.drawString(text, x, y);
            if (commandNum == 0) {
                g2.drawString(">", x - gp.tileSize, y);
            }

            text = "Officer";
            x = getXforCenteredText(text);
            y += gp.tileSize;
            g2.drawString(text, x, y);
            if (commandNum == 1) {
                g2.drawString(">", x - gp.tileSize, y);
            }

            text = "Intruder";
            x = getXforCenteredText(text);
            y += gp.tileSize;
            g2.drawString(text, x, y);
            if (commandNum == 2) {
                g2.drawString(">", x - gp.tileSize, y);
            }

            text = "Artist";
            x = getXforCenteredText(text);
            y += gp.tileSize;
            g2.drawString(text, x, y);
            if (commandNum == 3) {
                g2.drawString(">", x - gp.tileSize, y);
            }

            text = "Collector";
            x = getXforCenteredText(text);
            y += gp.tileSize;
            g2.drawString(text, x, y);
            if (commandNum == 4) {
                g2.drawString(">", x - gp.tileSize, y);
            }

            text = "Back";
            x = getXforCenteredText(text);
            y += gp.tileSize * 2;
            g2.drawString(text, x, y);
            if (commandNum == 5) {
                g2.drawString(">", x - gp.tileSize, y);
            }
        }
    }

    public String getCharacterTitle() {
        if (gp.player == null)
            return "";

        return switch (gp.player.characterType) {
            case DETECTIVE -> "JOHN LOYD: THE DETECTIVE";
            case OFFICER -> "ANDREW: THE OFFICER";
            case INTRUDER -> "TRIXY: THE INTRUDER";
            case ARTIST -> "TRIA: THE ARTIST";
            case COLLECTOR -> "YOHANN: THE COLLECTOR";
        };
    }

    public void drawPlayerUI() {
        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRoundRect(15, 20, 360, 100, 20, 20);

        // ===== TOP LEFT (CHARACTER NAME) =====
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 24F));
        g2.setColor(Color.white);

        String characterText = getCharacterTitle();

        int margin = 30;
        int x = margin;
        int y = 50;

        // Flicker effect
        int flicker = 0;

        if (Math.random() < 0.08) {
            flicker = (int) (Math.random() * 2);
        }

        // Shadow
        g2.setColor(Color.black);
        g2.drawString(characterText, x + 2 + flicker, y + 2 + flicker);

        // Main text
        g2.setColor(Color.white);
        g2.drawString(characterText, x + flicker, y + flicker);

        // ===== BOTTOM RIGHT (FLOOR + TIME) =====
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20F));

        String floorText = "3RD FLOOR - 2:28 AM";

        int textWidth = (int) g2.getFontMetrics().getStringBounds(floorText, g2).getWidth();

        int x2 = gp.screenWidth - textWidth - 20;
        int y2 = gp.screenHeight - 20;

        // Shadow
        g2.setColor(Color.black);
        g2.drawString(floorText, x2, y2);

        // Main
        g2.setColor(Color.white);
        g2.drawString(floorText, x2 + flicker, y2);

        // ===== HEART RATE DISPLAY =====
        float pulseScale = 1f;

        pulseCounter++;
        if (pulseCounter > 30) {
            pulseOn = !pulseOn;
            pulseCounter = 0;
        }

        if (pulseOn) {
            pulseScale = 1.1f; // expand
        }

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20F * pulseScale));

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20F));

        String hrText = "HEART RATE: " + gp.player.heartRate + " BPM";

        int hrX = margin;
        int hrY = 90;

        // Color logic
        if (gp.player.heartRate < 60) {
            g2.setColor(Color.cyan); // calm / low
        } else if (gp.player.heartRate < 100) {
            g2.setColor(new Color(0, 200, 120)); // normal
        } else if (gp.player.heartRate < 140) {
            g2.setColor(new Color(255, 140, 0)); // stressed
        } else {
            g2.setColor(new Color(200, 40, 40)); // danger
        }

        // Draw Text
        g2.drawString(hrText, hrX + flicker, hrY + flicker);

        // ===== DANGER OVERLAY (ENHANCED) =====
        if (gp.player.heartRate >= 140 || gp.player.heartRate <= 45) {

            int intensity;

            if (gp.player.heartRate >= 140) {
                intensity = gp.player.heartRate - 140;
            } else {
                intensity = 45 - gp.player.heartRate;
            }

            intensity = Math.min(intensity, 40);
            int alpha = 30 + intensity + (pulseOn ? 20 : 0); // Base + intensity + pulse

            g2.setColor(new Color(180, 0, 0, alpha));
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        }
    }

    public void drawPauseScreen() {
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 80F));
        String text = "PAUSED";

        int x = getXforCenteredText(text);
        int y = gp.screenHeight / 2;

        g2.drawString(text, x, y);
    }

    public int getXforCenteredText(String text) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = gp.screenWidth / 2 - length / 2;
        return x;
    }
}
