package UI;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;

import Inventory.Item;
import main.GamePanel;
import Utilities.States.TitleScreenState;
import entity.Player.Character;

public class UI {
    private GamePanel gp;
    private Graphics2D g2;

    private Font arial_40;
    public int commandNum;
    public TitleScreenState titleScreenState;
    int pulseCounter;
    boolean pulseOn;

    public UI(GamePanel gp, Graphics2D g2) {
        this.gp = gp;
        this.g2 = g2;

        arial_40 = new Font("Arial", Font.PLAIN, 40);

        titleScreenState = TitleScreenState.MAIN_MENU;
        commandNum = pulseCounter = 0;
    }

    public void draw() {
        g2.setFont(arial_40);
        g2.setColor(Color.white);

        switch (gp.gameState) {
        case TITLE -> drawTitleScreen();
        case PLAY -> drawPlayerUI();
        case PAUSE -> drawPauseScreen();
        case ENEMY_ENCOUNTER -> {
            drawPlayerUI();
            drawEnemyEncounter();
        }
        case FIRST_LOAD, BATTLE -> {
        }
        case INVENTORY -> {
            drawPlayerUI();
            drawInventoryScreen(gp.player);
        }
        case GAME_OVER -> {
            drawPlayerUI();
            drawGameOverScreen();
        }
        }
    }

    private void drawEnemyEncounter() {
        float progress = gp.getEncounterTransitionProgress();

        float darknessProgress = Math.min(1F, progress / 0.85F);
        int overlayAlpha = Math.min(255, 30 + Math.round(darknessProgress * 225));
        g2.setColor(new Color(0, 0, 0, overlayAlpha));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        if (progress < 0.2F) {
            return;
        }

        float fadeInProgress = Math.min(1F, (progress - 0.2F) / 0.18F);
        float fadeOutProgress = progress <= 0.62F ? 0F : Math.min(1F, (progress - 0.62F) / 0.23F);
        float textVisibility = fadeInProgress * (1F - fadeOutProgress);
        int titleAlpha = Math.min(255, Math.round(255 * textVisibility));
        int subtitleAlpha = Math.min(215, Math.round(215 * textVisibility));

        if (titleAlpha <= 0 && subtitleAlpha <= 0) {
            return;
        }

        boolean bossEncounter = gp.isBossEncounter();

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 54F));
        g2.setColor(bossEncounter ? new Color(210, 20, 20, titleAlpha) : new Color(255, 255, 255, titleAlpha));
        String title = bossEncounter ? "Boss Encounter" : "Enemy Found";
        g2.drawString(title, getXforCenteredText(title), gp.screenHeight / 2 - 20);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 32F));
        g2.setColor(bossEncounter ? new Color(255, 120, 120, subtitleAlpha) : new Color(220, 220, 220, subtitleAlpha));
        String enemyText = gp.getEncounterMessage();
        g2.drawString(enemyText, getXforCenteredText(enemyText), gp.screenHeight / 2 + 35);
    }

    public void drawInventoryScreen(Character player) {
        if (player == null) {
            return;
        }

        Item[] inventory = player.getInventory();
        int frameX = gp.tileSize;
        int frameY = gp.tileSize;
        int frameWidth = gp.screenWidth - (gp.tileSize * 2);
        int frameHeight = gp.screenHeight - (gp.tileSize * 2);

        g2.setColor(new Color(0, 0, 0, 210));
        g2.fillRoundRect(frameX, frameY, frameWidth, frameHeight, 18, 18);

        g2.setColor(new Color(230, 230, 230));
        g2.setStroke(new BasicStroke(4f));
        g2.drawRoundRect(frameX, frameY, frameWidth, frameHeight, 18, 18);

        int listStartX = frameX + gp.tileSize;
        int listStartY = frameY + gp.tileSize + 8;
        int rowHeight = 56;

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 52F));
        int selectedIndex = Math.max(0, Math.min(commandNum, inventory.length - 1));

        for (int i = 0; i < inventory.length; i++) {
            int rowY = listStartY + (i * rowHeight);

            if (i == selectedIndex) {
                g2.setColor(new Color(255, 70, 70));
                g2.drawString(">", listStartX - 36, rowY);
                g2.setColor(Color.white);
            } else {
                g2.setColor(new Color(170, 170, 170));
            }
            String itemText = inventory[i].getName() + " " + inventory[i].getQuantity() + "x";
            g2.drawString(itemText, listStartX, rowY);
        }
        int subtitleY = frameY + frameHeight - gp.tileSize - 54;
        g2.setColor(new Color(190, 190, 190));
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 28F));
        drawWrappedText(inventory[selectedIndex].getDescription(), listStartX, subtitleY,
                frameWidth - (gp.tileSize * 2), 34);

        int instructionY = frameY + frameHeight - (gp.tileSize / 2);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 40F));
        g2.setColor(Color.white);
        g2.drawString("Press Enter to use", listStartX, instructionY);
    }

    private void drawWrappedText(String text, int x, int y, int maxWidth, int lineHeight) {
        if (text == null || text.isBlank()) {
            return;
        }

        String[] words = text.split("\\s+");
        StringBuilder line = new StringBuilder();
        int drawY = y;

        for (String word : words) {
            String candidate = line.isEmpty() ? word : line + " " + word;
            int width = g2.getFontMetrics().stringWidth(candidate);

            if (width > maxWidth && !line.isEmpty()) {
                g2.drawString(line.toString(), x, drawY);
                line = new StringBuilder(word);
                drawY += lineHeight;
            } else {
                line = new StringBuilder(candidate);
            }
        }

        if (!line.isEmpty()) {
            g2.drawString(line.toString(), x, drawY);
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

    public void drawPlayerUI() {
        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRoundRect(15, 20, 360, 100, 20, 20);

        // ===== TOP LEFT (CHARACTER NAME) =====
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 24F));
        g2.setColor(Color.white);

        String characterText = gp.player.getName().toUpperCase();

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

        String floorText = gp.map.getMapName() + " - 2:28 AM";

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
        pulseCounter++;
        if (pulseCounter > 30) {
            pulseOn = !pulseOn;
            pulseCounter = 0;
        }

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20F));

        String hrText = "HEART RATE: " + gp.player.heartRate + " BPM";

        int hrX = margin;
        int hrY = 90;

        if (gp.player.heartRate < 60) {
            g2.setColor(Color.cyan);
        } else if (gp.player.heartRate < 100) {
            g2.setColor(new Color(0, 200, 120));
        } else if (gp.player.heartRate < 140) {
            g2.setColor(new Color(255, 140, 0));
        } else {
            g2.setColor(new Color(200, 40, 40));
        }

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

        if (gp.isOneShotModeEnabled()) {
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 18F));
            g2.setColor(new Color(255, 80, 80));
            g2.drawString("ONE-SHOT MODE", margin, 118);
        }

        String statusMessage = gp.getStatusMessage();
        if (!statusMessage.isBlank()) {
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 24F));
            int messageWidth = g2.getFontMetrics().stringWidth(statusMessage);
            int boxX = (gp.screenWidth - messageWidth) / 2 - 20;
            int boxY = gp.screenHeight - 90;
            int boxWidth = messageWidth + 40;

            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRoundRect(boxX, boxY, boxWidth, 42, 14, 14);
            g2.setColor(Color.WHITE);
            g2.drawString(statusMessage, boxX + 20, boxY + 28);
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

    public void drawGameOverScreen() {
        g2.setColor(new Color(0, 0, 0, 150)); // Dark overlay
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        String text = "YOU DIED";
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 110F));

        g2.setColor(Color.black);
        g2.drawString(text, getXforCenteredText(text) + 4, gp.screenHeight / 2 + 4);

        g2.setColor(new Color(200, 0, 0));
        g2.drawString(text, getXforCenteredText(text), gp.screenHeight / 2);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 32F));
        g2.setColor(Color.white);
        text = "Press Enter to Restart";
        g2.drawString(text, getXforCenteredText(text), gp.screenHeight / 2 + gp.tileSize);
    }
}
