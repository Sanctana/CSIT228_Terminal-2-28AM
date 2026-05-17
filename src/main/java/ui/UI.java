package ui;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import java.nio.file.Path;
import java.util.List;

import battle.ability.Action;
import battle.ability.Skill;
import inventory.Item;
import main.GamePanel;
import utilities.SaveManager;
import utilities.UtilityTool;
import utilities.states.TitleScreenState;
import entity.player.Character;
import entity.player.CharacterType;
import utilities.LeaderboardManager;

import java.awt.AlphaComposite;

public class UI {
    public static final long VICTORY_ENDING_COMPLETE_MS = 56_000L;
    private GamePanel gp;
    private Graphics2D g2;

    private Font arial_40;
    private Font mainMenuFont;
    private Font titleFont;
    private Font timeFont;

    public int commandNum;
    public TitleScreenState titleScreenState;
    public boolean pauseQuitConfirm;
    public PauseSavePrompt pauseSavePrompt = PauseSavePrompt.NONE;
    public boolean loadDeleteConfirm;
    public int pendingDeleteSaveIndex;
    public List<Path> saveFiles = List.of();
    int pulseCounter;
    boolean pulseOn;
    private final CharacterPreview[] characterPreviews;
    private Image titleBackground;
    private static final String TITLE_TEXT = "Terminal";
    private static final String TIME_TEXT = "2:28 AM";
    private static final int TITLE_FONT_SIZE = 200;
    private static final int TIME_FONT_SIZE = 130;
    private static final int TITLE_SHADOW_LAYERS = 8;
    private static final float TITLE_SHADOW_SPREAD = 0.8F;
    private static final int TITLE_SHADOW_OFFSET_X = 3;
    private static final int TITLE_SHADOW_OFFSET_Y = 3;
    private static final int TIME_SHADOW_OFFSET_X = -6;
    private static final int TIME_SHADOW_OFFSET_Y = 3;
    private static final Color TITLE_FLICKER_COLOR = new Color(255, 0, 0, 10);
    private static final Color TIME_COLOR = new Color(90, 10, 10);
    private static final Color TIME_GHOST_COLOR = new Color(120, 0, 0, 100);
    private int cachedTitleScreenWidth = -1;
    private int cachedTitleScreenHeight = -1;
    private TextImageCache titleTextCache;
    private TextImageCache timeTextCache;

    private Image exampleImage;

    private CharacterPreview selectedCharacterPreview;
    public String nameEntryText = "";
    public int nameEntryIndex = 0;
    public java.util.List<LeaderboardManager.Entry> leaderboardEntries = new java.util.ArrayList<>();



    public enum PauseSavePrompt {
        NONE, MAIN_MENU, QUIT
    }

    public UI(GamePanel gp, Graphics2D g2) {
        this.gp = gp;
        this.g2 = g2;

        arial_40 = new Font("Arial", Font.PLAIN, 40);
        mainMenuFont = getMenuFont(40);

        titleScreenState = TitleScreenState.MAIN_MENU;
        commandNum = pulseCounter = 0;

        titleBackground = new ImageIcon(getClass().getResource("/Assets/TitleScreenBackground/TitleScreen.gif"))
                .getImage();

        exampleImage = new ImageIcon(getClass().getResource("/Assets/Blood.png")).getImage();
        characterPreviews = new CharacterPreview[] {

                new CharacterPreview(CharacterType.DETECTIVE, "DETECTIVE", "Revolver",
                        "John, also known as John Lloyd, is a detective residing outside "
                                + "WildCats Town. He visits the town intending to find answers about his wife’s murder case"
                                + "wife’s murder case.",
                        "/player/Detective/Lloyd_Transparent.png", "/player/Detective/Loyd.png", gp),

                new CharacterPreview(CharacterType.OFFICER, "OFFICER", "Service Pistol",
                        "Andrew is a humble and honest police officer in WildCats Town. "
                                + "He upholds strong morals and always looks out for those in need, "
                                + "protecting the weak with unwavering dedication.",
                        "/player/Officer/Andrew_Transparent.png", "/player/Officer/Andrew.png", gp),

                new CharacterPreview(CharacterType.INTRUDER, "INTRUDER", "Crowbar",
                        "Trixy once lived a normal life as a waitress in WildCats Town. "
                                + "But after her husband fell gravely ill, debts consumed her life. "
                                + "Now, she resorts to desperate measures just to survive.",
                        "/player/Intruder/Trixy_Transparent.png", "/player/Intruder/Trixy.png", gp),

                new CharacterPreview(CharacterType.ARTIST, "ARTIST", "Canvas Tools",
                        "No one truly knows Tria. She shows no emotion and speaks little. "
                                + "Locals find it disturbing that she frequently buys different shades of red paint, "
                                + "often late at night.",
                        "/player/Artist/Tria_Transparent.png", "/player/Artist/Tria.png", gp),

                new CharacterPreview(CharacterType.COLLECTOR, "COLLECTOR", "Ledger",
                        "Yohan works for a loan shark organization, tasked with collecting debts. "
                                + "His presence alone is enough to intimidate the residents of WildCats Town.",
                        "/player/Collector/Yohann_Transparent.png", "/player/Collector/Yohann.png", gp) };
    }

    private Font getMenuFont(float size) {
        try {
            return Font.createFont(Font.TRUETYPE_FONT,
                    getClass().getResourceAsStream("/Assets/Fonts/DK Face Your Fears.ttf"));
        } catch (Exception e) {
            e.printStackTrace();
            return new Font("Arial", Font.BOLD, (int) size);
        }
    }

    public void draw() {
        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setFont(mainMenuFont);
        g2.setColor(Color.white);

        switch (gp.gameState) {
        case TITLE -> drawTitleScreen();
        case PLAY -> drawPlayerUI();
        case PAUSE -> drawPauseScreen();
        case LEADERBOARD_MENU -> drawLeaderboardMenuScreen();
        case NAME_ENTRY -> drawNameEntryScreen();
        case ENEMY_ENCOUNTER -> {
            drawPlayerUI();
            drawEnemyEncounter();
        }
        case LEADERBOARD -> {
                drawPlayerUI();
                drawLeaderboardOverlay();
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
        case VICTORY_ENDING -> drawVictoryEnding();
        }
    }

    private void drawVictoryEnding() {
        long elapsed = gp.getVictoryEndingElapsedMillis();

        g2.setColor(Color.black);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        drawVictoryCredits(elapsed);

        if (elapsed >= VICTORY_ENDING_COMPLETE_MS) {

            drawLeaderboardOverlay();


            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 30F));
            int alpha = Math.min(255, (int) ((elapsed - VICTORY_ENDING_COMPLETE_MS) / 4));
            g2.setColor(new Color(230, 230, 230, alpha));
            String text = "Press Enter to Return to Main Menu";
            g2.drawString(text, getXforCenteredText(text), gp.screenHeight / 2 + 230);
        }
    }

    private void drawVictoryCredits(long elapsed) {
        long scrollElapsed = elapsed - 800L;
        if (scrollElapsed < 0 || elapsed >= VICTORY_ENDING_COMPLETE_MS) {
            return;
        }

        String[] lines = { "Terminal 2:28 AM", "The hospital grows quiet", "", "", "", "", "", "", "", "", """
                Thank you for playing this game!
                we truly appreciate the support
                you've given us throughout this
                project. Your encouragement means
                a lot, and we hope you enjoyed the
                experience as much as we enjoyed
                creating it.
                """, "", "", "", "", "", "", "Created by:", "Team Terminal 2:28 AM", "", "",
                "Game design, programming, and graphics:", "Yohann Abarquez", "Trixy Flores", "Loyd Hernaez",
                "Andrew Sangasina", "Trea Tangpos", "", "", "Story and atmosphere:", "Team Terminal 2:28 AM", "", "",
                "Battle system:", "Team Terminal 2:28 AM", "", "", "Maps and level layout:", "Team Terminal 2:28 AM",
                "", "", "Special thanks:", "Our instructor", "Our classmates", """
                        Everyone who played and
                        tested Terminal 2:28 AM
                        """, "", "", "", "", "", "", "", "", "", "", "", "Thank you for reaching the end",
                "The night is over." };

        float fadeIn = Math.min(1F, scrollElapsed / 2000F);
        int baseAlpha = Math.min(255, Math.round(255 * fadeIn));
        int y = gp.screenHeight + 90 - Math.round(scrollElapsed * 0.06F);

        for (String line : lines) {
            boolean title = "Terminal 2:28 AM".equals(line);
            boolean subtitle = "The hospital grows quiet".equals(line);
            boolean heading = line.endsWith(":");
            boolean message = line.contains("Thank you for playing this");
            boolean closing = line.startsWith("Thank you");

            if (title) {
                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 58F));
                g2.setColor(new Color(190, 30, 30, baseAlpha));
            } else if (subtitle || message) {
                g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 28F));
                g2.setColor(new Color(225, 225, 225, baseAlpha));
            } else if (heading) {
                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 30F));
                g2.setColor(new Color(205, 55, 55, baseAlpha));
            } else if (closing) {
                g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 36F));
                g2.setColor(new Color(255, 255, 255, baseAlpha));
            } else {
                g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 28F));
                g2.setColor(new Color(210, 210, 210, baseAlpha));
            }

            String[] displayLines = line.split("\\R");
            for (String rawDisplayLine : displayLines) {
                String displayLine = rawDisplayLine.strip();

                if (displayLine.isBlank()) {
                    y += 34;
                    continue;
                }

                if (y > -60 && y < gp.screenHeight + 80) {
                    g2.drawString(displayLine, getXforCenteredText(displayLine), y);
                }

                y += title ? 58 : heading ? 48 : 40;
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

    private int drawWrappedText(String text, int x, int y, int maxWidth, int lineHeight) {
        if (text == null || text.isBlank()) {
            return y;
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
            drawY += lineHeight;
        }

        return drawY;
    }

    public void drawTitleScreen() {
        ensureTitleScreenCache();

        if (titleBackground != null) {
            g2.drawImage(titleBackground, 0, 0, gp.screenWidth, gp.screenHeight, null);
        }

        if (Math.random() < 0.03) {
            g2.setColor(TITLE_FLICKER_COLOR);
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        }

        float breathe = (float) Math.sin(System.currentTimeMillis() * 0.020) * 2;

        if (titleScreenState == TitleScreenState.MAIN_MENU) {
            int titleX = getXforCenteredWidth(titleTextCache.textWidth);
            int titleY = gp.screenHeight / 2 - 120 + (int) breathe;

            g2.drawImage(titleTextCache.image, titleX - titleTextCache.anchorX, titleY - titleTextCache.anchorY, null);

            int glitchTimer = (int) (System.currentTimeMillis() / 100);

            boolean glitch = false;
            if (glitchTimer % 15 == 0 && Math.random() < 0.6) {
                glitch = true;
            }

            int glitchX = 0;
            int glitchY = 0;

            if (glitch) {
                glitchX = (int) (Math.random() * 6 - 3);
                glitchY = (int) (Math.random() * 4 - 2);
            }

            int timeX = getXforCenteredWidth(timeTextCache.textWidth) - 8 + glitchX;
            int timeY = titleY + 120 + glitchY;

            g2.drawImage(timeTextCache.image, timeX - timeTextCache.anchorX, timeY - timeTextCache.anchorY, null);

            if (glitch) {
                int ghostOffset = (int) (Math.random() * 4 - 2);
                g2.setFont(timeFont);
                g2.setColor(TIME_GHOST_COLOR);
                g2.drawString(TIME_TEXT, timeX + ghostOffset + 2, timeY + ghostOffset + 2);
            }

            String[] options = { "NEW GAME", "LOAD GAME", "LEADERBOARD", "QUIT" };
            drawCenteredMenuOptions(options, gp.screenHeight / 2 + 120, 62, 48F);
        } else if (titleScreenState == TitleScreenState.CHARACTER_SELECT) {
            drawCharacterSelectScreen();
        } else if (titleScreenState == TitleScreenState.LOAD_GAME) {
            drawLoadGameScreen();
        }
    }


    private void ensureTitleScreenCache() {
        if (cachedTitleScreenWidth == gp.screenWidth && cachedTitleScreenHeight == gp.screenHeight
                && titleTextCache != null && timeTextCache != null) {
            return;
        }

        cachedTitleScreenWidth = gp.screenWidth;
        cachedTitleScreenHeight = gp.screenHeight;

        titleFont = mainMenuFont.deriveFont(Font.BOLD, (float) TITLE_FONT_SIZE);
        timeFont = mainMenuFont.deriveFont(Font.BOLD, (float) TIME_FONT_SIZE);

        Color[] shadowColors = createShadowColors(TITLE_SHADOW_LAYERS);
        titleTextCache = createShadowTextCache(TITLE_TEXT, titleFont, TITLE_SHADOW_LAYERS, TITLE_SHADOW_SPREAD,
                TITLE_SHADOW_OFFSET_X, TITLE_SHADOW_OFFSET_Y, shadowColors, Color.white);
        timeTextCache = createShadowTextCache(TIME_TEXT, timeFont, TITLE_SHADOW_LAYERS, TITLE_SHADOW_SPREAD,
                TIME_SHADOW_OFFSET_X, TIME_SHADOW_OFFSET_Y, shadowColors, TIME_COLOR);
    }

    private Color[] createShadowColors(int layers) {
        Color[] colors = new Color[layers];
        for (int i = 0; i < layers; i++) {
            int alpha = 140 - (i * 15);
            if (alpha < 0) {
                alpha = 0;
            }
            colors[i] = new Color(0, 0, 0, alpha);
        }
        return colors;
    }

    private TextImageCache createShadowTextCache(String text, Font font, int layers, float spreadStep,
            int baseShadowOffsetX, int baseShadowOffsetY, Color[] shadowColors, Color textColor) {
        BufferedImage measureImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D measureG2 = measureImage.createGraphics();
        measureG2.setFont(font);
        FontMetrics fontMetrics = measureG2.getFontMetrics();
        int textWidth = fontMetrics.stringWidth(text);
        int textHeight = fontMetrics.getHeight();
        int ascent = fontMetrics.getAscent();
        measureG2.dispose();

        int maxSpread = Math.round((layers - 1) * spreadStep);
        int minOffsetX = Math.min(0, Math.min(baseShadowOffsetX, baseShadowOffsetX + maxSpread));
        int maxOffsetX = Math.max(0, Math.max(baseShadowOffsetX, baseShadowOffsetX + maxSpread));
        int minOffsetY = Math.min(0, Math.min(baseShadowOffsetY, baseShadowOffsetY + maxSpread));
        int maxOffsetY = Math.max(0, Math.max(baseShadowOffsetY, baseShadowOffsetY + maxSpread));

        int padding = 2;
        int imageWidth = textWidth + (maxOffsetX - minOffsetX) + (padding * 2);
        int imageHeight = textHeight + (maxOffsetY - minOffsetY) + (padding * 2);

        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D textG2 = image.createGraphics();
        textG2.setFont(font);
        textG2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        textG2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int anchorX = padding - minOffsetX;
        int anchorY = padding - minOffsetY + ascent;

        for (int i = 0; i < layers; i++) {
            float spread = i * spreadStep;
            int offsetX = baseShadowOffsetX + Math.round(spread);
            int offsetY = baseShadowOffsetY + Math.round(spread);
            textG2.setColor(shadowColors[i]);
            textG2.drawString(text, anchorX + offsetX, anchorY + offsetY);
        }

        textG2.setColor(textColor);
        textG2.drawString(text, anchorX, anchorY);
        textG2.dispose();

        return new TextImageCache(image, anchorX, anchorY, textWidth);
    }

    private void drawLoadGameScreen() {
        g2.setColor(Color.white);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 56F));

        String text = "LOAD GAME";
        int y = gp.screenHeight / 2 - 230;
        g2.drawString(text, getXforCenteredText(text), y);

        if (!saveFiles.isEmpty() && !loadDeleteConfirm) {
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 22F));
            g2.setColor(new Color(165, 165, 165));
            text = "Press Enter to load     Press D to delete";
            g2.drawString(text, getXforCenteredText(text), y + 42);
        }

        if (loadDeleteConfirm) {
            drawLoadDeleteConfirmation();
            return;
        }

        if (saveFiles.isEmpty()) {
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 42F));
            text = "NO SAVE DATA";
            g2.setColor(new Color(180, 180, 180));
            g2.drawString(text, getXforCenteredText(text), gp.screenHeight / 2 + 10);
            drawCenteredMenuOption("BACK", 0, gp.screenHeight / 2 + 255, 42F);
            return;
        }

        int startY = gp.screenHeight / 2 - 92;
        int rowHeight = 48;
        int visibleSaves = Math.min(saveFiles.size(), 6);
        int selectedIndex = Math.max(0, Math.min(commandNum, saveFiles.size()));
        int firstIndex = Math.max(0, selectedIndex - visibleSaves + 1);
        firstIndex = Math.min(firstIndex, Math.max(0, saveFiles.size() - visibleSaves));

        for (int i = 0; i < visibleSaves; i++) {
            int saveIndex = firstIndex + i;
            drawCenteredMenuOption(SaveManager.getDisplayName(saveFiles.get(saveIndex)), saveIndex,
                    startY + i * rowHeight, 30F);
        }

        drawCenteredMenuOption("BACK", saveFiles.size(), gp.screenHeight / 2 + 255, 42F);
    }

    private void drawLoadDeleteConfirmation() {
        int frameWidth = 680;
        int frameHeight = 230;
        int frameX = (gp.screenWidth - frameWidth) / 2;
        int frameY = gp.screenHeight / 2 - 70;

        g2.setColor(new Color(0, 0, 0, 225));
        g2.fillRoundRect(frameX, frameY, frameWidth, frameHeight, 18, 18);

        g2.setColor(new Color(230, 230, 230));
        g2.setStroke(new BasicStroke(4f));
        g2.drawRoundRect(frameX, frameY, frameWidth, frameHeight, 18, 18);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 30F));
        g2.setColor(Color.white);
        String text = "Delete save data?";
        g2.drawString(text, getXforCenteredText(text), frameY + 76);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 22F));
        g2.setColor(new Color(205, 205, 205));
        text = "This save file will be removed.";
        g2.drawString(text, getXforCenteredText(text), frameY + 114);

        drawTwoChoiceOptions("YES", "NO", frameY + 178);
    }

    private void drawCharacterSelectScreen() {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        int overlayX = 0;
        int overlayY = 150;
        int overlayW = gp.screenWidth;
        int overlayH = 520;

        g2.setColor(new Color(255, 255, 255, 8));
        g2.fillRect(overlayX, overlayY, overlayW, overlayH);

        g2.setColor(new Color(69, 15, 15));
        g2.setStroke(new BasicStroke(3f));
        g2.drawLine(overlayX, overlayY, overlayX + overlayW, overlayY);

        g2.setFont(mainMenuFont.deriveFont(Font.BOLD, 52F));
        g2.setColor(Color.white);
        String title = "SELECT YOUR CHARACTER";
        g2.drawString(title, getXforCenteredText(title), 120);

        String[] options = { "DETECTIVE", "OFFICER", "INTRUDER", "ARTIST", "COLLECTOR", "BACK" };

        int panelX = 60;
        int panelY = 200;
        int panelW = 260;
        int textY = panelY + 60;

        for (int i = 0; i < options.length; i++) {
            boolean selected = commandNum == i;
            boolean isBackOption = "BACK".equals(options[i]);

            if (selected) {
                if (isBackOption) {
                    g2.setColor(new Color(0, 0, 0, 180));
                    g2.fillRoundRect(panelX + 10, textY - 27, panelW - 20, 40, 5, 10);

                    g2.setFont(mainMenuFont.deriveFont(Font.BOLD, 28F));
                    g2.setColor(new Color(108, 2, 2));
                    g2.drawString(options[i], panelX + 25, textY);
                } else {
                    g2.setColor(new Color(255, 255, 255, 60));
                    g2.fillRoundRect(panelX + 10, textY - 27, panelW - 20, 40, 5, 10);

                    g2.setFont(mainMenuFont.deriveFont(Font.BOLD, 28F));
                    g2.setColor(new Color(0, 0, 0, 123));
                    g2.drawString(options[i], panelX + 27, textY + 2);

                    g2.setColor(new Color(96, 1, 1));
                    g2.drawString(options[i], panelX + 25, textY);
                }
            } else {
                g2.setFont(mainMenuFont.deriveFont(Font.PLAIN, 26F));
                g2.setColor(new Color(163, 159, 159, 169));
                g2.drawString(options[i], panelX + 25, textY);
            }

            textY += 55;
        }

        drawCharacterInfoPanel();
    }

    public void setSelectedCharacterPreview() {
        selectedCharacterPreview = characterPreviews[Math.min(commandNum, characterPreviews.length - 1)];
    }

    private void drawCharacterInfoPanel() {
        int selectedIndex = Math.min(commandNum, characterPreviews.length - 1);
        CharacterPreview preview = characterPreviews[selectedIndex];
        Character character = preview.character;

        int panelX = 380;
        int panelY = 200;
        int panelW = gp.screenWidth - panelX - 60;
        int panelH = 420;

        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRoundRect(panelX, panelY, panelW, panelH, 20, 20);

        g2.setColor(new Color(69, 15, 15));
        g2.setStroke(new BasicStroke(3f));
        g2.drawRoundRect(panelX, panelY, panelW, panelH, 20, 20);

        int imgSize = 160;
        int imgX = panelX + 30;
        int imgY = panelY + 30;

        g2.setColor(new Color(60, 60, 60));
        g2.fillRoundRect(imgX, imgY, imgSize, imgSize, 18, 18);

        if (preview.portrait != null) {
            g2.drawImage(preview.portrait, imgX, imgY, imgSize, imgSize, null);
        }

        int textX = imgX + imgSize + 25;
        int textY = imgY + 30;

        g2.setFont(mainMenuFont.deriveFont(Font.BOLD, 45));
        g2.setColor(new Color(108, 2, 2));
        g2.drawString(preview.menuName, textX, textY);

        g2.setFont(arial_40.deriveFont(Font.PLAIN, 18F));
        g2.setColor(new Color(200, 200, 200, 171));
        int descMaxWidth = panelX + panelW - textX - 40;
        int afterDescY = drawWrappedText(preview.description, textX, textY + 35, descMaxWidth, 22);

        int infoY = afterDescY + 75;
        g2.setFont(arial_40.deriveFont(Font.BOLD, 20F));
        g2.setColor(Color.white);
        g2.drawString("WEAPON:", panelX + 30, infoY);

        g2.setFont(arial_40.deriveFont(Font.PLAIN, 20F));
        g2.setColor(new Color(200, 200, 200));
        g2.drawString(preview.weapon, panelX + 30, infoY + 30);

        g2.setFont(arial_40.deriveFont(Font.BOLD, 20F));
        g2.setColor(Color.white);
        g2.drawString("DEFENSE:", panelX + 30, infoY + 80);

        g2.setFont(arial_40.deriveFont(Font.PLAIN, 20F));
        g2.setColor(new Color(200, 200, 200));
        g2.drawString(Math.round(character.initialResistance * 100) + "% Resistance", panelX + 30, infoY + 110);

        int skillX = panelX + 250;
        int skillY = infoY;

        g2.setFont(arial_40.deriveFont(Font.BOLD, 20F));
        g2.setColor(Color.white);
        g2.drawString("SKILLS:", skillX, skillY);

        g2.setFont(arial_40.deriveFont(Font.PLAIN, 18F));
        g2.setColor(new Color(200, 200, 200));
        int y = skillY + 30;
        for (Skill skill : character.skills) {
            g2.drawString("- " + skill.getName(), skillX, y);
            y += 24;
        }

        int actionX = skillX + 250;
        int actionY = skillY;

        g2.setFont(arial_40.deriveFont(Font.BOLD, 20F));
        g2.setColor(Color.white);
        g2.drawString("ACTIONS:", actionX, actionY);

        g2.setFont(arial_40.deriveFont(Font.PLAIN, 18F));
        g2.setColor(new Color(200, 200, 200));
        int ay = actionY + 30;
        for (Action action : character.actions) {
            g2.drawString("- " + action.getName(), actionX, ay);
            ay += 24;
        }
    }

    private void drawCenteredMenuOptions(String[] options, int startY, int rowHeight, float fontSize) {
        int y = startY;

        for (int i = 0; i < options.length; i++) {
            drawCenteredMenuOption(options[i], i, y, fontSize);
            y += rowHeight;
        }
    }

    private void drawCenteredMenuOption(String text, int optionIndex, int y, float fontSize) {
        boolean selected = commandNum == optionIndex;

        g2.setFont(mainMenuFont.deriveFont(selected ? Font.BOLD : Font.PLAIN, fontSize));

        int x = getXforCenteredText(text);

        int shadowOffset = selected ? 3 : 2;
        int shadowAlpha = selected ? 120 : 80;
        Color shadowColor = new Color(0, 0, 0, shadowAlpha);

        int jitter = selected ? (int) (Math.random() * 2) : 0;

        g2.setColor(shadowColor);
        g2.drawString(text, x + shadowOffset + jitter, y + shadowOffset + jitter);

        if (selected) {
            g2.drawString(text, x + 2 + jitter, y + 2 + jitter);
            g2.setColor(new Color(108, 2, 2));
        } else {
            g2.setColor(new Color(120, 120, 120, 140));
        }

        g2.drawString(text, x + jitter, y + jitter);

        if (selected) {
            // Choose font for the arrow symbol based on the option
            if ("QUIT".equals(text)) {
                g2.setFont(mainMenuFont.deriveFont(Font.BOLD, fontSize));
            } else {
                g2.setFont(arial_40.deriveFont(Font.BOLD, 35f));
            }

            g2.setColor(Color.white);
            g2.drawString(">", x - gp.tileSize + jitter, y + jitter);

            // Reset back to mainMenuFont for consistency
            g2.setFont(mainMenuFont.deriveFont(selected ? Font.BOLD : Font.PLAIN, fontSize));
        }
    }

    private static class CharacterPreview {
        private final String menuName;
        private final String weapon;
        private final String description;

        private final Image portrait;
        private final Image ingamePortrait;

        private final Character character;

        private CharacterPreview(CharacterType type, String menuName, String weapon, String description,
                String portraitPath, String ingamePath, GamePanel gp) {

            this.menuName = menuName;
            this.weapon = weapon;
            this.description = description;

            this.portrait = new ImageIcon(CharacterPreview.class.getResource(portraitPath)).getImage();
            this.ingamePortrait = new ImageIcon(CharacterPreview.class.getResource(ingamePath)).getImage();

            this.character = UtilityTool.characterFactory(type, gp);
        }

        public Image getIngamePortrait() {
            return ingamePortrait;
        }
    }

    private static class TextImageCache {
        private final BufferedImage image;
        private final int anchorX;
        private final int anchorY;
        private final int textWidth;

        private TextImageCache(BufferedImage image, int anchorX, int anchorY, int textWidth) {
            this.image = image;
            this.anchorX = anchorX;
            this.anchorY = anchorY;
            this.textWidth = textWidth;
        }
    }

    private void drawPlayerUI() {
        int marginX = 80;
        int marginY = 40;

        if (exampleImage != null) {
            float opacity = 0.05f;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            g2.drawImage(exampleImage, 0, 0, gp.screenWidth, gp.screenHeight, null);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }

        // int selectedIndex = Math.min(commandNum, characterPreviews.length - 1);
        // CharacterPreview preview = characterPreviews[selectedIndex];

        if (selectedCharacterPreview != null) {
            g2.drawImage(selectedCharacterPreview.getIngamePortrait(), marginX, marginY, 150, 150, null);
        }

        int textBaseX = marginX + 170;
        int textBaseY = marginY + 35;

        g2.setFont(mainMenuFont.deriveFont(Font.BOLD, 28F));
        g2.setColor(Color.RED);
        String characterText = gp.player.getName().toUpperCase();
        g2.drawString(characterText, textBaseX, textBaseY);

        int lineSpacing = 20;
        int lineY = textBaseY + lineSpacing;
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(textBaseX, lineY, textBaseX + 450, lineY);

        int statusY = lineY + 30;
        g2.setFont(new Font("Arial", Font.BOLD, 18));

        String statusLabel = "STATUS:";
        g2.setColor(new Color(255, 255, 255, 60));
        g2.drawString(statusLabel, textBaseX, statusY);

        int statusLabelWidth = g2.getFontMetrics().stringWidth(statusLabel);
        int statusX = textBaseX + statusLabelWidth + 8; // small gap

        if (gp.player.heartRate <= 55 || gp.player.heartRate >= 165) {
            g2.setColor(Color.YELLOW);
            g2.drawString("WARNING", statusX, statusY);
        } else {
            g2.setColor(Color.GREEN);
            g2.drawString("STABLE", statusX, statusY);
        }

        int hrY = statusY + 35;
        g2.setFont(new Font("Arial", Font.BOLD, 18));

        String hrLabel = "HEART RATE:";
        g2.setColor(new Color(255, 255, 255, 60));
        g2.drawString(hrLabel, textBaseX, hrY);

        int hrLabelWidth = g2.getFontMetrics().stringWidth(hrLabel);
        int hrX = textBaseX + hrLabelWidth + 8;

        g2.setColor(Color.WHITE);
        String hrValue = gp.player.heartRate + " BPM";
        g2.drawString(hrValue, hrX, hrY);

        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.setColor(Color.WHITE);
        String beforeI = "PRESS '";
        String afterI = "' TO USE INVENTORY";
        int invX = gp.screenWidth - 300;
        int invY = 80;
        g2.drawString(beforeI, invX, invY);
        g2.setColor(Color.RED);
        g2.drawString("I", invX + g2.getFontMetrics().stringWidth(beforeI), invY);
        g2.setColor(Color.WHITE);
        g2.drawString(afterI, invX + g2.getFontMetrics().stringWidth(beforeI + "I"), invY);

        String beforeTab = "PRESS '";
        String afterTab = "' LEADERBOARD";
        int tabY = invY + 26;  // just below the inventory hint
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.setColor(Color.WHITE);
        g2.drawString(beforeTab, invX, tabY);
        g2.setColor(Color.RED);
        g2.drawString("TAB", invX + g2.getFontMetrics().stringWidth(beforeTab), tabY);
        g2.setColor(Color.WHITE);
        g2.drawString(afterTab, invX + g2.getFontMetrics().stringWidth(beforeTab + "TAB"), tabY);


        g2.setFont(mainMenuFont.deriveFont(Font.BOLD, 34F));
        g2.setColor(Color.RED);
        String floorText = gp.map.getMapName() + " - 2:28 AM";
        int textWidth = g2.getFontMetrics().stringWidth(floorText);
        g2.drawString(floorText, gp.screenWidth - textWidth - 20, gp.screenHeight - 30);

        String statusMessage = gp.getStatusMessage();
        if (!statusMessage.isBlank()) {
            int statusAlpha = gp.getStatusMessageAlpha();
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 24F));
            int messageWidth = g2.getFontMetrics().stringWidth(statusMessage);
            int boxX = (gp.screenWidth - messageWidth) / 2 - 20;
            int boxY = gp.screenHeight - 90;
            int boxWidth = messageWidth + 40;

            g2.setColor(new Color(0, 0, 0, Math.min(180, statusAlpha)));
            g2.fillRoundRect(boxX, boxY, boxWidth, 42, 14, 14);
            g2.setColor(new Color(255, 255, 255, statusAlpha));
            g2.drawString(statusMessage, boxX + 20, boxY + 28);
        }

        int borderX = 0;
        int borderY = 0;
        int borderW = gp.screenWidth;
        int borderH = gp.screenHeight;
        int cornerRadius = (int) (Math.min(borderW, borderH) * 0.10);

        g2.setColor(new Color(37, 14, 14));
        g2.setStroke(new BasicStroke(21f));
        g2.drawRoundRect(borderX, borderY, borderW, borderH, cornerRadius, cornerRadius);
    }

    public void drawPauseScreen() {
        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 80F));
        g2.setColor(Color.white);
        String text = "PAUSED";
        int x = getXforCenteredText(text);
        int y = gp.screenHeight / 2 - 120;

        g2.drawString(text, x, y);

        if (pauseQuitConfirm) {
            drawPauseQuitConfirmation();
            return;
        }

        if (pauseSavePrompt != PauseSavePrompt.NONE) {
            drawPauseSavePrompt();
            return;
        }

        String[] options = { "RESUME", "SAVE GAME", "MAIN MENU", "QUIT" };
        drawCenteredMenuOptions(options, gp.screenHeight / 2, 62, 46F);
    }

    private void drawPauseSavePrompt() {
        int frameWidth = 700;
        int frameHeight = 240;
        int frameX = (gp.screenWidth - frameWidth) / 2;
        int frameY = gp.screenHeight / 2 - 80;

        g2.setColor(new Color(0, 0, 0, 225));
        g2.fillRoundRect(frameX, frameY, frameWidth, frameHeight, 18, 18);

        g2.setColor(new Color(230, 230, 230));
        g2.setStroke(new BasicStroke(4f));
        g2.drawRoundRect(frameX, frameY, frameWidth, frameHeight, 18, 18);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 30F));
        g2.setColor(Color.white);
        String text = "Would you like to save your game?";
        g2.drawString(text, getXforCenteredText(text), frameY + 78);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 22F));
        g2.setColor(new Color(205, 205, 205));
        text = pauseSavePrompt == PauseSavePrompt.MAIN_MENU ? "Save before returning to main menu?"
                : "Save before exiting to desktop?";
        g2.drawString(text, getXforCenteredText(text), frameY + 116);

        drawThreeChoiceOptions("YES", "NO", "CANCEL", frameY + 185);
    }

    private void drawPauseQuitConfirmation() {
        int frameWidth = 620;
        int frameHeight = 230;
        int frameX = (gp.screenWidth - frameWidth) / 2;
        int frameY = gp.screenHeight / 2 - 70;

        g2.setColor(new Color(0, 0, 0, 225));
        g2.fillRoundRect(frameX, frameY, frameWidth, frameHeight, 18, 18);

        g2.setColor(new Color(230, 230, 230));
        g2.setStroke(new BasicStroke(4f));
        g2.drawRoundRect(frameX, frameY, frameWidth, frameHeight, 18, 18);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 30F));
        g2.setColor(Color.white);
        String text = "Exit to desktop?";
        g2.drawString(text, getXforCenteredText(text), frameY + 70);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 22F));
        g2.setColor(new Color(205, 205, 205));
        text = "Are you sure you want to quit?";
        g2.drawString(text, getXforCenteredText(text), frameY + 108);

        drawTwoChoiceOptions("YES", "NO", frameY + 175);
    }

    private void drawTwoChoiceOptions(String leftText, String rightText, int optionY) {
        String[] options = { leftText, rightText };
        int optionGap = 120;
        int centerX = gp.screenWidth / 2;

        for (int i = 0; i < options.length; i++) {
            boolean selected = commandNum == i;
            g2.setFont(g2.getFont().deriveFont(selected ? Font.BOLD : Font.PLAIN, 36F));
            g2.setColor(selected ? Color.white : new Color(185, 185, 185));

            int optionX = centerX + ((i == 0) ? -optionGap : optionGap)
                    - g2.getFontMetrics().stringWidth(options[i]) / 2;
            g2.drawString(options[i], optionX, optionY);

            if (selected) {
                g2.drawString(">", optionX - gp.tileSize, optionY);
            }
        }
    }

    private void drawThreeChoiceOptions(String leftText, String centerText, String rightText, int optionY) {
        String[] options = { leftText, centerText, rightText };
        int[] offsets = { -180, 0, 180 };
        int centerX = gp.screenWidth / 2;

        for (int i = 0; i < options.length; i++) {
            boolean selected = commandNum == i;
            g2.setFont(g2.getFont().deriveFont(selected ? Font.BOLD : Font.PLAIN, 34F));
            g2.setColor(selected ? Color.white : new Color(185, 185, 185));

            int optionX = centerX + offsets[i] - g2.getFontMetrics().stringWidth(options[i]) / 2;
            g2.drawString(options[i], optionX, optionY);

            if (selected) {
                g2.drawString(">", optionX - gp.tileSize, optionY);
            }
        }
    }

    public int getXforCenteredText(String text) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = gp.screenWidth / 2 - length / 2;
        return x;
    }

    private int getXforCenteredWidth(int width) {
        return gp.screenWidth / 2 - width / 2;
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
    private void drawLeaderboardOverlay() {
        int panelW = 540;
        int panelH = 400;
        int panelX = (gp.screenWidth - panelW) / 2;
        int panelY = (gp.screenHeight - panelH) / 2;


        g2.setColor(new Color(10, 10, 10, 220));
        g2.fillRoundRect(panelX, panelY, panelW, panelH, 20, 20);

        g2.setColor(new Color(150, 20, 20));
        g2.setStroke(new BasicStroke(3f));
        g2.drawRoundRect(panelX, panelY, panelW, panelH, 20, 20);


        g2.setFont(mainMenuFont.deriveFont(Font.BOLD, 42F));
        g2.setColor(new Color(220, 50, 50));
        String title = "LEADERBOARD";
        g2.drawString(title, getXforCenteredText(title), panelY + 55);


        g2.setColor(new Color(150, 20, 20, 180));
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(panelX + 30, panelY + 68, panelX + panelW - 30, panelY + 68);


        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.setColor(new Color(200, 200, 200));
        String charName = gp.player != null ? gp.player.name.toUpperCase() : "UNKNOWN";
        g2.drawString(charName, getXforCenteredText(charName), panelY + 100);


        long ms = gp.getTotalPlayedMs();
        long hours = ms / 3_600_000;
        long minutes = (ms % 3_600_000) / 60_000;
        long seconds = (ms % 60_000) / 1_000;
        String timeStr = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.setColor(new Color(150, 150, 150));
        String timeLabel = "TIME PLAYED";
        g2.drawString(timeLabel, getXforCenteredText(timeLabel), panelY + 130);

        g2.setFont(new Font("Arial", Font.BOLD, 28));
        g2.setColor(new Color(220, 220, 220));
        g2.drawString(timeStr, getXforCenteredText(timeStr), panelY + 162);


        g2.setColor(new Color(80, 80, 80, 150));
        g2.setStroke(new BasicStroke(1f));
        g2.drawLine(panelX + 30, panelY + 178, panelX + panelW - 30, panelY + 178);


        int kills = gp.player != null ? gp.player.killCount : 0;
        int bosses = gp.player != null ? gp.player.bossKillCount : 0;
        int score = gp.player != null ? gp.player.score : 0;

        int col1X = panelX + 60;
        int col2X = panelX + panelW / 2 - 30;
        int col3X = panelX + panelW - 140;
        int labelY = panelY + 220;
        int valueY = panelY + 260;


        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.setColor(new Color(150, 150, 150));
        g2.drawString("ENEMIES", col1X, labelY);
        g2.drawString("BOSSES", col2X, labelY);
        g2.drawString("SCORE", col3X, labelY);

        g2.setFont(mainMenuFont.deriveFont(Font.BOLD, 48F));

        g2.setColor(kills > 0 ? new Color(220, 50, 50) : new Color(100, 100, 100));
        g2.drawString(String.valueOf(kills), col1X, valueY);

        g2.setColor(bosses > 0 ? new Color(255, 180, 0) : new Color(100, 100, 100));
        g2.drawString(String.valueOf(bosses), col2X, valueY);

        g2.setFont(new Font("Arial", Font.BOLD, 32));
        g2.setColor(score > 0 ? new Color(100, 220, 100) : new Color(100, 100, 100));
        g2.drawString(String.valueOf(score), col3X, valueY - 8);

        g2.setFont(new Font("Arial", Font.PLAIN, 13));
        g2.setColor(new Color(110, 110, 110));
        String legend = "Enemy: +200pts     Boss: +1000pts";
        g2.drawString(legend, getXforCenteredText(legend), panelY + 310);

        g2.setFont(new Font("Arial", Font.PLAIN, 13));
        g2.setColor(new Color(90, 90, 90));
        String hint = "Release TAB to close";
        g2.drawString(hint, getXforCenteredText(hint), panelY + panelH - 15);

    }
    private void drawNameEntryScreen() {
        g2.setColor(new Color(0, 0, 0, 220));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        int panelW = 560;
        int panelH = 260;
        int panelX = (gp.screenWidth - panelW) / 2;
        int panelY = (gp.screenHeight - panelH) / 2;

        g2.setColor(new Color(10, 10, 10, 230));
        g2.fillRoundRect(panelX, panelY, panelW, panelH, 20, 20);
        g2.setColor(new Color(150, 20, 20));
        g2.setStroke(new java.awt.BasicStroke(3f));
        g2.drawRoundRect(panelX, panelY, panelW, panelH, 20, 20);

        g2.setFont(mainMenuFont.deriveFont(java.awt.Font.BOLD, 38F));
        g2.setColor(new Color(220, 50, 50));
        String title = "ENTER YOUR NAME";
        g2.drawString(title, getXforCenteredText(title), panelY + 60);

        g2.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 18));
        g2.setColor(new Color(160, 160, 160));
        String sub = "Your run has been recorded. Name yourself.";
        g2.drawString(sub, getXforCenteredText(sub), panelY + 92);


        int boxW = 400;
        int boxH = 54;
        int boxX = (gp.screenWidth - boxW) / 2;
        int boxY = panelY + 115;

        g2.setColor(new Color(30, 30, 30));
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 10, 10);
        g2.setColor(new Color(180, 20, 20));
        g2.setStroke(new java.awt.BasicStroke(2f));
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 10, 10);

        String display = nameEntryText.isEmpty() ? "Enter Name" : nameEntryText;
        boolean isCursor = (System.currentTimeMillis() / 500) % 2 == 0;
        String displayWithCursor = nameEntryText + (isCursor ? "|" : " ");

        g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
        g2.setColor(nameEntryText.isEmpty() ? new Color(100, 100, 100) : Color.WHITE);
        String toShow = nameEntryText.isEmpty() ? display : displayWithCursor;
        g2.drawString(toShow, getXforCenteredText(toShow), boxY + 35);

        g2.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 15));
        g2.setColor(new Color(100, 100, 100));
        String hint = "Press Enter to confirm";
        g2.drawString(hint, getXforCenteredText(hint), panelY + panelH - 15);
    }

    private void drawLeaderboardMenuScreen() {

        g2.setColor(new Color(8, 8, 8));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);


        g2.setColor(new Color(37, 14, 14));
        g2.setStroke(new java.awt.BasicStroke(21f));
        int cr = (int)(Math.min(gp.screenWidth, gp.screenHeight) * 0.10);
        g2.drawRoundRect(0, 0, gp.screenWidth, gp.screenHeight, cr, cr);


        g2.setFont(mainMenuFont.deriveFont(java.awt.Font.BOLD, 56F));
        g2.setColor(new Color(220, 50, 50));
        String title = "LEADERBOARD";
        g2.drawString(title, getXforCenteredText(title), 70);


        g2.setColor(new Color(150, 20, 20));
        g2.setStroke(new java.awt.BasicStroke(2f));
        g2.drawLine(60, 82, gp.screenWidth - 60, 82);


        int tableX = 50;
        int headerY = 118;
        int rankW = 60;
        int nameW = 280;
        int colW = 120;

        int col1 = tableX + rankW;
        int col2 = col1 + nameW;
        int col3 = col2 + colW;
        int col4 = col3 + colW;
        int col5 = col4 + colW;

        g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 13));
        g2.setColor(new Color(150, 150, 150));
        g2.drawString("#", tableX + 18, headerY);
        g2.drawString("PLAYER NAME", col1, headerY);
        g2.drawString("BOSSES", col2, headerY);
        g2.drawString("ENEMIES", col3, headerY);
        g2.drawString("SCORE", col4, headerY);
        g2.drawString("TIME", col5, headerY);


        g2.setColor(new Color(80, 80, 80));
        g2.setStroke(new java.awt.BasicStroke(1f));
        g2.drawLine(tableX, headerY + 8, gp.screenWidth - tableX, headerY + 8);


        int rowH = 52;
        int startY = headerY + 28;

        java.util.List<LeaderboardManager.Entry> entries = leaderboardEntries;

        for (int i = 0; i < 10; i++) {
            int rowY = startY + i * rowH;
            boolean hasEntry = i < entries.size();


            if (i % 2 == 0) {
                g2.setColor(new Color(20, 20, 20, 180));
            } else {
                g2.setColor(new Color(12, 12, 12, 180));
            }
            g2.fillRoundRect(tableX - 8, rowY - 28, gp.screenWidth - (tableX * 2) + 16, rowH - 4, 8, 8);

            // Rank number with medal colors for top 3
            g2.setFont(mainMenuFont.deriveFont(java.awt.Font.BOLD, 26F));
            if (i == 0) g2.setColor(new Color(255, 215, 0));
            else if (i == 1) g2.setColor(new Color(192, 192, 192));
            else if (i == 2) g2.setColor(new Color(205, 127, 50));
            else g2.setColor(new Color(120, 120, 120));
            g2.drawString(String.format("%02d", i + 1), tableX + 4, rowY);

            if (hasEntry) {
                LeaderboardManager.Entry e = entries.get(i);


                g2.setFont(mainMenuFont.deriveFont(java.awt.Font.BOLD, 26F));
                g2.setColor(i == 0 ? new Color(255, 215, 0) : Color.WHITE);
                String nameDisplay = e.name.isEmpty() ? "Enter Name" : e.name;
                g2.drawString(nameDisplay, col1, rowY);

                g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 20));


                g2.setColor(new Color(255, 180, 0));
                g2.drawString(String.valueOf(e.bossKillCount), col2 + 20, rowY);


                g2.setColor(new Color(220, 80, 80));
                g2.drawString(String.valueOf(e.killCount), col3 + 20, rowY);


                g2.setColor(new Color(100, 220, 100));
                g2.drawString(String.valueOf(e.score), col4 + 10, rowY);


                long ms = e.totalPlayedMs;
                long h = ms / 3_600_000;
                long m = (ms % 3_600_000) / 60_000;
                long s = (ms % 60_000) / 1_000;
                g2.setColor(new Color(180, 180, 255));
                g2.drawString(String.format("%02d:%02d:%02d", h, m, s), col5, rowY);

            } else {

                g2.setFont(new java.awt.Font("Arial", java.awt.Font.ITALIC, 18));
                g2.setColor(new Color(60, 60, 60));
                g2.drawString("---", col1, rowY);
                g2.drawString("-", col2 + 20, rowY);
                g2.drawString("-", col3 + 20, rowY);
                g2.drawString("-", col4 + 10, rowY);
                g2.drawString("--:--:--", col5, rowY);
            }


            g2.setColor(new Color(40, 40, 40));
            g2.setStroke(new java.awt.BasicStroke(1f));
            g2.drawLine(tableX, rowY + 18, gp.screenWidth - tableX, rowY + 18);
        }


        int backY = gp.screenHeight - 38;
        g2.setFont(mainMenuFont.deriveFont(java.awt.Font.BOLD, 34F));
        g2.setColor(new Color(108, 2, 2));
        String back = "< BACK";
        g2.drawString(back, getXforCenteredText(back), backY);

        g2.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        g2.setColor(new Color(80, 80, 80));
        String hint = "Press Enter or Escape to return";
        g2.drawString(hint, getXforCenteredText(hint), backY + 22);
    }

}