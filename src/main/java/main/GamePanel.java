package main;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.util.Stack;
import javax.swing.JPanel;

import Maps.FirstFloorMap;
import Maps.Map;
import Maps.ThirdFloorMap;
import Maps.SecondFloorMap;
import Maps.FirstFloorMap;
import UI.UI;
import Utilities.UtilityTool;
import Utilities.States.EntityState;
import Utilities.States.GameState;
import Utilities.States.TitleScreenState;
import battle.BattleLauncher;
import battle.Enemy;
import entity.Boss.GuidanceP1;
import entity.Boss.GuidanceP2;
import entity.Boss.GuidanceP3;
import entity.Player.Character;
import entity.Player.CharacterType;
import environment.EnvironmentManager;

enum Transitions {
    NONE, RESPAWN, NEW_GAME, BATTLE_RETURN, GAME_OVER, CHANGE_MAP, VICTORY_RETURN
}

public class GamePanel extends JPanel implements Runnable {
    private final int originalTileSize = 8;
    private final int scale = 8;
    private final int FPS = 60;
    private final int maxScreenCol = 20;
    private final int maxScreenRow = 12;
    private static final double WORLD_ZOOM = 1.5;

    public final int tileSize = originalTileSize * scale; // 64 by 64
    public int screenWidth = tileSize * maxScreenCol;
    public int screenHeight = tileSize * maxScreenRow;

    private final SoundManager music = new SoundManager();
    private final Thread gameThread;

    private BufferedImage tempScreen;
    private Graphics2D graphics2d;

    public Map map;
    public CollisionChecker cChecker;
    public UI ui;
    public Character player;
    public GameState gameState;
    public Stack<Point> previousPlayerPositions = new Stack<>(); // Stack to store previous player positions for map
                                                                 // transitions
    private JPanel activeBattlePanel;
    private Enemy pendingEnemy;
    private long encounterStartTime;
    private String encounterMessage = "";
    private boolean bossEncounter = false;
    private boolean pendingBossMapTransition = false;
    private boolean pendingFinalBossVictory = false;
    private boolean finalBossDefeated = false;
    private boolean oneShotModeEnabled = false;
    private String statusMessage = "";
    private long victoryEndingStartTime = 0L;

    private int respawnFadeAlpha = 0;
    private int RESPAWN_FADE_STEP = 18;

    private Transitions transitionPhase = Transitions.NONE;

    private static final long ENCOUNTER_TRANSITION_DURATION_MS = 1500L;

   // private EnvironmentManager eManager = new EnvironmentManager(this);
    private final KeyHandler keyH = new KeyHandler(this);

    public GamePanel() {
        this.setLayout(null);
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        map = new ThirdFloorMap(this); // Default map
        gameThread = new Thread(this, "GameLoop");
        cChecker = new CollisionChecker(this);
    }

    public void setupGame() {
        //eManager.setup();
        gameState = GameState.TITLE;

        // playMusic(0);

        tempScreen = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
        graphics2d = (Graphics2D) tempScreen.getGraphics();

        ui = new UI(this, graphics2d);
    }

    public void setFullScreen() {
        GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(Game.window);

        screenWidth = Game.window.getWidth();
        screenHeight = Game.window.getHeight();
    }

    public void startGameThread() {
        gameThread.start();
    }

    public void run() {
        final long drawInterval = 1_000_000_000L / FPS; // 16.67 ms per frame
        long nextFrameTime = System.nanoTime() + drawInterval;

        while (gameThread != null && !Thread.currentThread().isInterrupted()) {
            update();
            drawToTempScreen();
            repaint();

            long sleepNs = nextFrameTime - System.nanoTime();
            if (sleepNs > 0) {
                try {
                    Thread.sleep(sleepNs / 1_000_000L, (int) (sleepNs % 1_000_000L));
                } catch (InterruptedException e) {// In case the sleep is interrupted, we should exit the loop to avoid
                                                  // running in an inconsistent state
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            nextFrameTime += drawInterval;
        }
    }

    public void update() {
        if (transitionPhase != Transitions.NONE) {
            doTransition();
            return;
        }

        if (gameState == GameState.PLAY) {
            player.update();
            if (player.heartRate <= 40 || player.heartRate >= 180) {
                gameState = GameState.GAME_OVER;
            /*} else if (player.state == EntityState.TO_NEXT_MAP && triggerBossBeforeNextFloor()) {
                return;*/
            } else if (player.state == EntityState.TO_NEXT_MAP || player.state == EntityState.TO_PREVIOUS_MAP) {
                startRespawnTransition(Transitions.CHANGE_MAP);
            }
        } else if (gameState == GameState.ENEMY_ENCOUNTER) {
            if (System.currentTimeMillis() - encounterStartTime >= ENCOUNTER_TRANSITION_DURATION_MS) {
                startBattle();
            }
        } else if (gameState == GameState.FIRST_LOAD) {
            startRespawnTransition(Transitions.NEW_GAME);
            completeFirstLoad();
        }
    }

    public void triggerBattle() {
        if (System.currentTimeMillis() - encounterStartTime < 5_000) { // 5 seconds of invulnerability after triggering
                                                                       // a battle
            return;
        }

        pendingEnemy = BattleLauncher.createRandomEnemy();
        startEncounter(pendingEnemy, pendingEnemy.getDisplayName() + " appeared", false, false);
    }

    private boolean triggerBossBeforeNextFloor() {
        if ("3RD FLOOR".equals(map.getMapName())) {
            startEncounter(new GuidanceP1(), "Guidance P1 blocks the descent", true, true);
            return true;
        }

        if ("2ND FLOOR".equals(map.getMapName())) {
            startEncounter(new GuidanceP2(), "Guidance P2 waits beyond the door", true, true);
            return true;
        }

        if ("1ST FLOOR".equals(map.getMapName())) {
            if (finalBossDefeated) {
                player.state = EntityState.IDLE;
                keyH.resetMovementInput();
                showStatusMessage("Guidance P3 defeated");
                return true;
            }

            startEncounter(new GuidanceP3(), "Guidance P3 waits at the final exit", true, false);
            pendingFinalBossVictory = true;
            return true;
        }

        return false;
    }

    private void startEncounter(Enemy enemy, String message, boolean isBossEncounter, boolean changeMapAfterVictory) {
        pendingEnemy = enemy;
        encounterMessage = message;
        bossEncounter = isBossEncounter;
        pendingBossMapTransition = changeMapAfterVictory;
        pendingFinalBossVictory = false;
        encounterStartTime = System.currentTimeMillis();
        gameState = GameState.ENEMY_ENCOUNTER;
    }

    private void startBattle() {
        activeBattlePanel = BattleLauncher.createBattlePanel(this, pendingEnemy,
                new BattleLauncher.BattleResultListener() {
                    @Override
                    public void onBattleWon(Character battlePlayer) {
                        endBattle(false);
                    }

                    @Override
                    public void onBattleLost(Character battlePlayer) {
                        endBattle(true);
                    }
                });
        activeBattlePanel.setBounds(0, 0, screenWidth, screenHeight);
        add(activeBattlePanel);
        setComponentZOrder(activeBattlePanel, 0);
        revalidate();
        repaint();
        gameState = GameState.BATTLE;
    }

    private void endBattle(boolean lostBattle) {
        if (activeBattlePanel != null) {
            remove(activeBattlePanel);
            activeBattlePanel = null;
        }

        boolean shouldChangeMap = pendingBossMapTransition && !lostBattle;
        boolean defeatedFinalBoss = pendingFinalBossVictory && !lostBattle;

        pendingEnemy = null;
        bossEncounter = false;
        pendingBossMapTransition = false;
        pendingFinalBossVictory = false;

        if (defeatedFinalBoss) {
            finalBossDefeated = true;
            player.state = EntityState.IDLE;
            showStatusMessage("Guidance P3 defeated");
        }

        revalidate();
        repaint();
        requestFocusInWindow();

        if (defeatedFinalBoss) {
            startVictoryEnding();
        } else {
            transitionPhase = lostBattle ? Transitions.GAME_OVER
                    : shouldChangeMap ? Transitions.CHANGE_MAP : Transitions.BATTLE_RETURN;
            respawnFadeAlpha = 255;
        }
    }

    private void startVictoryEnding() {
        transitionPhase = Transitions.NONE;
        respawnFadeAlpha = 0;
        keyH.resetMovementInput();
        victoryEndingStartTime = System.currentTimeMillis();
        gameState = GameState.VICTORY_ENDING;
    }

    public long getVictoryEndingElapsedMillis() {
        if (gameState != GameState.VICTORY_ENDING) {
            return 0L;
        }

        return System.currentTimeMillis() - victoryEndingStartTime;
    }

    public boolean isVictoryEndingComplete() {
        return getVictoryEndingElapsedMillis() >= UI.VICTORY_ENDING_COMPLETE_MS;
    }

    public void requestReturnToMainMenu() {
        if (gameState != GameState.VICTORY_ENDING || !isVictoryEndingComplete()) {
            return;
        }

        transitionPhase = Transitions.VICTORY_RETURN;
        respawnFadeAlpha = 0;
        keyH.resetMovementInput();
    }

    private void completeReturnToMainMenu() {
        player = null;
        map = new ThirdFloorMap(this);
        previousPlayerPositions.clear();
        pendingEnemy = null;
        bossEncounter = false;
        pendingBossMapTransition = false;
        pendingFinalBossVictory = false;
        finalBossDefeated = false;
        statusMessage = "";
        victoryEndingStartTime = 0L;
        keyH.resetMovementInput();
        ui.titleScreenState = TitleScreenState.MAIN_MENU;
        ui.commandNum = 0;
        gameState = GameState.TITLE;
        revalidate();
        repaint();
        requestFocusInWindow();
    }

    public void requestRespawn() {
        if (gameState == GameState.GAME_OVER && transitionPhase == Transitions.NONE) {
            startRespawnTransition(Transitions.RESPAWN);
        }
    }

    public void requestNewGame(CharacterType characterType) {
        if (transitionPhase != Transitions.NONE) {
            return;
        }

        player = UtilityTool.characterFactory(characterType, this);
        startRespawnTransition(Transitions.NEW_GAME);
    }

    private void respawnPlayer() {
        if (player == null) {
            return;
        }

        if (activeBattlePanel != null) {
            remove(activeBattlePanel);
            activeBattlePanel = null;
        }

        pendingEnemy = null;
        bossEncounter = false;
        pendingBossMapTransition = false;
        pendingFinalBossVictory = false;
        finalBossDefeated = false;
        previousPlayerPositions.clear();

        // Please check if this is really 70 since some Character sets it to 100 upon
        // creation
        player.heartRate = 70;
        player.setDefaultValues();
        keyH.resetMovementInput();

        Point spawnPoint = map.loadMap();
        if (spawnPoint == null) {
            gameState = GameState.TITLE;
            revalidate();
            repaint();
            requestFocusInWindow();
            return;
        }

        player.setLocation(spawnPoint.y, spawnPoint.x);

        revalidate();
        repaint();
        requestFocusInWindow();

        gameState = GameState.PLAY;
    }

    private void completeFirstLoad() {
        Point spawnPoint = map.loadMap();
        if (spawnPoint == null) {
            gameState = GameState.TITLE;
            return;
        }

        player.setLocation(spawnPoint.y, spawnPoint.x);
        player.state = EntityState.IDLE;
        gameState = GameState.PLAY;
    }

    private void startRespawnTransition(Transitions action) {
        transitionPhase = action;
        respawnFadeAlpha = (action == Transitions.RESPAWN) ? 150 : 0;
    }

    private void doTransition() {
        respawnFadeAlpha = Math.max(0, Math.min(255, respawnFadeAlpha + RESPAWN_FADE_STEP));
        if (respawnFadeAlpha < 255 && respawnFadeAlpha > 0) {
            return;
        }

        if (RESPAWN_FADE_STEP > 0) {
            switch (transitionPhase) {
            case RESPAWN -> respawnPlayer();
            case NEW_GAME -> beginNewGameTransition();
            case BATTLE_RETURN -> {
                encounterStartTime = System.currentTimeMillis(); // Reset encounter timer to prevent immediate
                                                                 // retriggering
                gameState = GameState.PLAY;
            }
            case GAME_OVER -> gameState = GameState.GAME_OVER;
            case VICTORY_RETURN -> completeReturnToMainMenu();
            case CHANGE_MAP -> {
                if (player.state == EntityState.TO_NEXT_MAP) {
                    player.storeCurrentPosition();

                    map = map.transitionToMap(player.state);

                    Point spawnPoint = map.loadMap();
                    player.setLocation(spawnPoint.y, spawnPoint.x);
                } else if (player.state == EntityState.TO_PREVIOUS_MAP) {
                    map = map.transitionToMap(player.state);
                    map.loadMap();

                    player.restorePreviousPosition(); // Restore the player's previous position after transitioning
                                                      // back

                }
                player.state = EntityState.IDLE;
                gameState = GameState.PLAY;
            }
            case NONE -> {
                // No action needed
            }
            }
        } else {
            transitionPhase = Transitions.NONE;
        }
        RESPAWN_FADE_STEP = -RESPAWN_FADE_STEP; // Reset for next time
    }

    private void beginNewGameTransition() {
        map = new ThirdFloorMap(this);
        previousPlayerPositions.clear();
        pendingEnemy = null;
        bossEncounter = false;
        pendingBossMapTransition = false;
        pendingFinalBossVictory = false;
        finalBossDefeated = false;
        keyH.resetMovementInput();
        completeFirstLoad();
    }

    public String getEncounterMessage() {
        return encounterMessage;
    }

    public boolean isBossEncounter() {
        return bossEncounter;
    }

    public float getEncounterTransitionProgress() {
        if (gameState != GameState.ENEMY_ENCOUNTER) {
            return 0F;
        }

        long elapsed = System.currentTimeMillis() - encounterStartTime;
        return Math.min(1F, elapsed / (float) ENCOUNTER_TRANSITION_DURATION_MS);
    }

    public boolean isOneShotModeEnabled() {
        return oneShotModeEnabled;
    }

    public void toggleOneShotMode() {
        oneShotModeEnabled = !oneShotModeEnabled;
        showStatusMessage("One-shot mode: " + (oneShotModeEnabled ? "ON" : "OFF"));
    }

    public void showStatusMessage(String message) {
        statusMessage = message;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        synchronized (this) {
            if (tempScreen != null) {
                g.drawImage(tempScreen, 0, 0, screenWidth, screenHeight, null);
            }
        }
    }

    public void drawToTempScreen() {
        synchronized (this) {
            // clear / background
            graphics2d.setColor(getBackground());
            graphics2d.fillRect(0, 0, screenWidth, screenHeight);

            if (gameState == GameState.FIRST_LOAD) {
                return;
            }
            // TITLE SCREEN
            if (gameState == GameState.TITLE) {
                ui.draw();
                drawRespawnTransition();
                return;
            } else if (gameState == GameState.VICTORY_ENDING) {
                ui.draw();
                drawRespawnTransition();
                return;
            } else if (gameState != GameState.BATTLE) {
                AffineTransform originalTransform = graphics2d.getTransform();
                graphics2d.scale(WORLD_ZOOM, WORLD_ZOOM);
                graphics2d.translate((screenWidth / 2.0) * (1.0 / WORLD_ZOOM - 1.0),
                        (screenHeight / 2.0) * (1.0 / WORLD_ZOOM - 1.0));

                map.draw(graphics2d);
                player.draw(graphics2d);
                //eManager.draw(graphics2d);

                graphics2d.setTransform(originalTransform);
                ui.draw();
                drawRespawnTransition();
            }
        }
    }

    private void drawRespawnTransition() {
        if (transitionPhase == Transitions.NONE) {
            return;
        }

        graphics2d.setColor(new Color(0, 0, 0, respawnFadeAlpha));
        graphics2d.fillRect(0, 0, screenWidth, screenHeight);

        if (transitionPhase == Transitions.CHANGE_MAP && RESPAWN_FADE_STEP < 0) {
            int textAlpha = Math.min(255, respawnFadeAlpha + 40);
            graphics2d.setColor(new Color(255, 255, 255, textAlpha));
            graphics2d.setFont(graphics2d.getFont().deriveFont(java.awt.Font.BOLD, 64F));
            String floorText = map.getMapName();
            int textWidth = graphics2d.getFontMetrics().stringWidth(floorText);
            graphics2d.drawString(floorText, (screenWidth - textWidth) / 2, screenHeight / 2);
        }
    }

    public void playMusic(int i) {
        music.setFile(i);
        music.play();
        music.loop();
    }

    public KeyHandler getKeyHandler() {
        return keyH;
    }

    public double getWorldZoom() {
        return WORLD_ZOOM;
    }
}
