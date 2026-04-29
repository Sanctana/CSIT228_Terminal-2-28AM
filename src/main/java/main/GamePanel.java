package main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Stack;
import javax.swing.JPanel;

import Maps.ThirdFloorMap;
import UI.UI;
import Utilities.States.GameState;
import battle.BattleLauncher;
import battle.Character;
import battle.Enemy;
import environment.EnvironmentManager;
import entity.CharacterType;
import entity.Player;
import entity.EntityState;
import tile.Map;

enum Transitions {
    NONE, RESPAWN, NEW_GAME, BATTLE_RETURN, GAME_OVER, CHANGE_MAP
}

public class GamePanel extends JPanel implements Runnable {
    private final int originalTileSize = 8;
    private final int scale = 8;
    private final int FPS = 60;
    private final int maxScreenCol = 20;
    private final int maxScreenRow = 12;

    public final int tileSize = originalTileSize * scale; // 64 by 64
    public int screenWidth = tileSize * maxScreenCol;
    public int screenHeight = tileSize * maxScreenRow;

    private final SoundManager music = new SoundManager();

    // FULL SCREEN
    private BufferedImage tempScreen;
    private Graphics2D graphics2d;
    private final Object renderLock = new Object();

    public Map map;
    private final Thread gameThread;
    public CollisionChecker cChecker;
    public UI ui;
    public Player player;
    public GameState gameState;
    public Stack<Point> previousPlayerPositions = new Stack<>(); // Stack to store previous player positions for map
                                                                 // transitions
    private JPanel activeBattlePanel;
    private Enemy pendingEnemy;
    private long encounterStartTime;
    private String encounterMessage = "";
    private boolean oneShotModeEnabled = false;
    private String statusMessage = "";

    private int respawnFadeAlpha = 0;
    private int RESPAWN_FADE_STEP = 18;

    private Transitions transitionPhase = Transitions.NONE;

    private static final long ENCOUNTER_TRANSITION_DURATION_MS = 1500L;

    EnvironmentManager eManager = new EnvironmentManager(this);
    KeyHandler keyH = new KeyHandler(this);

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
        eManager.setup();
        gameState = GameState.TITLE;

        // playMusic(0);

        tempScreen = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
        graphics2d = (Graphics2D) tempScreen.getGraphics();

        ui = new UI(this, graphics2d);
    }

    public void setFullScreen() {
        GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(Main.window);

        screenWidth = Main.window.getWidth();
        screenHeight = Main.window.getHeight();
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
        encounterMessage = pendingEnemy.getClass().getSimpleName() + " appeared";
        encounterStartTime = System.currentTimeMillis();
        gameState = GameState.ENEMY_ENCOUNTER;
    }

    private void startBattle() {
        activeBattlePanel = BattleLauncher.createBattlePanel(this, pendingEnemy,
                new BattleLauncher.BattleResultListener() {
                    @Override
                    public void onBattleWon(Character battlePlayer) {
                        endBattle(battlePlayer, false);
                    }

                    @Override
                    public void onBattleLost(Character battlePlayer) {
                        endBattle(battlePlayer, true);
                    }
                });
        activeBattlePanel.setBounds(0, 0, screenWidth, screenHeight);
        add(activeBattlePanel);
        setComponentZOrder(activeBattlePanel, 0);
        revalidate();
        repaint();
        gameState = GameState.BATTLE;
    }

    private void endBattle(Character battlePlayer, boolean lostBattle) {
        player.heartRate = battlePlayer.getHeartBeat();
        player.itemAmounts = battlePlayer.getItemAmounts().clone();

        if (activeBattlePanel != null) {
            remove(activeBattlePanel);
            activeBattlePanel = null;
        }

        pendingEnemy = null;
        player.state = EntityState.IDLE;

        revalidate();
        repaint();
        requestFocusInWindow();

        if (lostBattle) {
            startRespawnTransition(Transitions.GAME_OVER);
        } else {
            startRespawnTransition(Transitions.BATTLE_RETURN);
        }
        respawnFadeAlpha = 255;
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

        player = new Player(this, keyH, characterType);
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
        previousPlayerPositions.clear();

        player.heartRate = 70;
        player.state = EntityState.IDLE;
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
        keyH.resetMovementInput();
        completeFirstLoad();
    }

    public String getEncounterMessage() {
        return encounterMessage;
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
        synchronized (renderLock) {
            if (tempScreen != null) {
                g.drawImage(tempScreen, 0, 0, screenWidth, screenHeight, null);
            }
        }
    }

    public void drawToTempScreen() {
        synchronized (renderLock) {
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
            }

            map.draw(graphics2d);
            player.draw(graphics2d);
            eManager.draw(graphics2d);
            ui.draw();
            drawRespawnTransition();
        }
    }

    private void drawRespawnTransition() {
        if (transitionPhase == Transitions.NONE) {
            return;
        }

        graphics2d.setColor(new Color(0, 0, 0, respawnFadeAlpha));
        graphics2d.fillRect(0, 0, screenWidth, screenHeight);
    }

    public void playMusic(int i) {
        music.setFile(i);
        music.play();
        music.loop();
    }

}
