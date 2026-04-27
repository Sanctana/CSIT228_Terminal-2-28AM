package main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Stack;
import javax.swing.JPanel;

import Maps.ThirdFloorMap;
import UI.UI;
import Utilities.States.GameState;
import Utilities.States.TileType;
import battle.BattleLauncher;
import battle.Character;
import battle.Enemy;
import environment.EnvironmentManager;
import entity.CharacterType;
import entity.Player;
import entity.EntityState;
import tile.Map;

public class GamePanel extends JPanel implements Runnable {
    private final int originalTileSize = 8;
    private final int scale = 8;
    private final int FPS = 60;
    private final int maxScreenCol = 20;
    private final int maxScreenRow = 12;

    public final int tileSize = originalTileSize * scale; // 64 by 64
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    private SoundManager music = new SoundManager();

    // FULL SCREEN
    private int screenWidth2 = screenWidth;
    private int screenHeight2 = screenHeight;
    private BufferedImage tempScreen;
    private Graphics2D graphics2d;
    private final Object renderLock = new Object();

    public Map map;
    private Thread gameThread;
    public CollisionChecker cChecker;
    public UI ui;
    public Player player;
    public GameState gameState;
    public Stack<Point> previousPlayerPositions = new Stack<>(); // Stack to store previous player positions for map
                                                                 // transitions
    private boolean battleTileReady = true;
    private JPanel activeBattlePanel;
    private Enemy pendingEnemy;
    private long encounterStartTime;
    private String encounterMessage = "";
    private boolean oneShotModeEnabled = false;
    private String statusMessage = "";
    private long statusMessageUntil = 0L;
    private volatile boolean respawnRequested = false;
    private volatile boolean newGameRequested = false;
    private CharacterType pendingCharacterType;
    private int respawnTransitionPhase = 0;
    private int respawnFadeAlpha = 0;
    private int transitionAction = 0;

    private static final int RESPAWN_TRANSITION_NONE = 0;
    private static final int RESPAWN_TRANSITION_FADE_OUT = 1;
    private static final int RESPAWN_TRANSITION_FADE_IN = 2;
    private static final int RESPAWN_FADE_STEP = 18;
    private static final int TRANSITION_ACTION_NONE = 0;
    private static final int TRANSITION_ACTION_RESPAWN = 1;
    private static final int TRANSITION_ACTION_NEW_GAME = 2;
    private static final int TRANSITION_ACTION_BATTLE_RETURN = 3;
    private static final int TRANSITION_ACTION_GAME_OVER = 4;

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

        screenWidth2 = Main.window.getWidth();
        screenHeight2 = Main.window.getHeight();
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
        if (respawnRequested) {
            respawnRequested = false;
            startRespawnTransition(TRANSITION_ACTION_RESPAWN);
        }

        if (newGameRequested) {
            newGameRequested = false;
            startRespawnTransition(TRANSITION_ACTION_NEW_GAME);
        }

        if (respawnTransitionPhase != RESPAWN_TRANSITION_NONE) {
            updateRespawnTransition();
            return;
        }

        if (gameState == GameState.PLAY) {
            player.update();
            handleBattleTileTrigger();
            if (player.heartRate <= 40 || player.heartRate >= 180) {
                gameState = GameState.GAMEOVER;
            }

            if (player.state == EntityState.TO_NEXT_MAP) {
                player.storeCurrentPosition();

                map = map.transitionToMap(player.state);

                Point spawnPoint = map.loadMap();
                player.setLocation(spawnPoint.y, spawnPoint.x);

                player.state = EntityState.IDLE;
            } else if (player.state == EntityState.TO_PREVIOUS_MAP) {
                map = map.transitionToMap(player.state);
                map.loadMap();

                player.restorePreviousPosition(); // Restore the player's previous position after transitioning back

                player.state = EntityState.IDLE;
            }
        } else if (gameState == GameState.ENEMY_ENCOUNTER) {
            if (System.currentTimeMillis() - encounterStartTime >= 1200L) {
                startBattle();
            }
        } else if (gameState == GameState.FIRST_LOAD) {
            completeFirstLoad();
        }
    }

    private void handleBattleTileTrigger() {
        TileType currentTileType = cChecker.getTileTypeUnderEntity(player);

        if (currentTileType == TileType.BATTLE_TRIGGER) {
            if (battleTileReady) {
                battleTileReady = false;
                triggerBattle();
            }
            return;
        }

        battleTileReady = true;
    }

    private void triggerBattle() {
        pendingEnemy = BattleLauncher.createRandomEnemy();
        encounterMessage = pendingEnemy.getClass().getSimpleName() + " appeared";
        encounterStartTime = System.currentTimeMillis();
        gameState = GameState.ENEMY_ENCOUNTER;
    }

    private void startBattle() {
        if (pendingEnemy == null) {
            gameState = GameState.PLAY;
            return;
        }

        activeBattlePanel = BattleLauncher.createBattlePanel(this, pendingEnemy, new BattleLauncher.BattleResultListener() {
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
        encounterMessage = "";
        player.state = EntityState.IDLE;

        revalidate();
        repaint();
        requestFocusInWindow();

        if (lostBattle) {
            gameState = GameState.GAMEOVER;
            startRespawnTransition(TRANSITION_ACTION_GAME_OVER);
            respawnTransitionPhase = RESPAWN_TRANSITION_FADE_IN;
            respawnFadeAlpha = 255;
        } else {
            gameState = GameState.PLAY;
            startRespawnTransition(TRANSITION_ACTION_BATTLE_RETURN);
            respawnTransitionPhase = RESPAWN_TRANSITION_FADE_IN;
            respawnFadeAlpha = 255;
        }
    }

    public void requestRespawn() {
        if (gameState == GameState.GAMEOVER && respawnTransitionPhase == RESPAWN_TRANSITION_NONE) {
            respawnRequested = true;
        }
    }

    public void requestNewGame(CharacterType characterType) {
        if (respawnTransitionPhase != RESPAWN_TRANSITION_NONE) {
            return;
        }

        pendingCharacterType = characterType;
        newGameRequested = true;
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
        encounterMessage = "";
        battleTileReady = false;
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
        if (player == null) {
            return;
        }

        Point spawnPoint = map.loadMap();
        if (spawnPoint == null) {
            gameState = GameState.TITLE;
            return;
        }

        player.setLocation(spawnPoint.y, spawnPoint.x);
        player.state = EntityState.IDLE;
        gameState = GameState.PLAY;
    }

    private void startRespawnTransition(int action) {
        transitionAction = action;
        respawnTransitionPhase = RESPAWN_TRANSITION_FADE_OUT;
        respawnFadeAlpha = (action == TRANSITION_ACTION_RESPAWN) ? 150 : 0;
    }

    private void updateRespawnTransition() {
        if (respawnTransitionPhase == RESPAWN_TRANSITION_FADE_OUT) {
            respawnFadeAlpha = Math.min(255, respawnFadeAlpha + RESPAWN_FADE_STEP);
            if (respawnFadeAlpha >= 255) {
                if (transitionAction == TRANSITION_ACTION_RESPAWN) {
                    respawnPlayer();
                } else if (transitionAction == TRANSITION_ACTION_NEW_GAME) {
                    beginNewGameTransition();
                } else if (transitionAction == TRANSITION_ACTION_BATTLE_RETURN) {
                    gameState = GameState.PLAY;
                } else if (transitionAction == TRANSITION_ACTION_GAME_OVER) {
                    gameState = GameState.GAMEOVER;
                }
                respawnTransitionPhase = RESPAWN_TRANSITION_FADE_IN;
            }
            return;
        }

        if (respawnTransitionPhase == RESPAWN_TRANSITION_FADE_IN) {
            respawnFadeAlpha = Math.max(0, respawnFadeAlpha - RESPAWN_FADE_STEP);
            if (respawnFadeAlpha <= 0) {
                respawnTransitionPhase = RESPAWN_TRANSITION_NONE;
                transitionAction = TRANSITION_ACTION_NONE;
            }
        }
    }

    private void beginNewGameTransition() {
        if (pendingCharacterType == null) {
            respawnTransitionPhase = RESPAWN_TRANSITION_NONE;
            transitionAction = TRANSITION_ACTION_NONE;
            gameState = GameState.TITLE;
            return;
        }

        map = new ThirdFloorMap(this);
        previousPlayerPositions.clear();
        pendingEnemy = null;
        encounterMessage = "";
        battleTileReady = false;
        keyH.resetMovementInput();

        player = new Player(this, keyH, pendingCharacterType);
        pendingCharacterType = null;
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
        return Math.min(1F, elapsed / 1200F);
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
        statusMessageUntil = System.currentTimeMillis() + 1800L;
    }

    public String getStatusMessage() {
        if (System.currentTimeMillis() > statusMessageUntil) {
            return "";
        }
        return statusMessage;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        synchronized (renderLock) {
            if (tempScreen != null) {
                g.drawImage(tempScreen, 0, 0, screenWidth2, screenHeight2, null);
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
                drawRespawnTransition(graphics2d);
                return;
            }

            map.draw(graphics2d);
            player.draw(graphics2d);
            eManager.draw(graphics2d);
            ui.draw();
            drawRespawnTransition(graphics2d);
        }
    }

    private void drawRespawnTransition(Graphics2D g2) {
        if (respawnTransitionPhase == RESPAWN_TRANSITION_NONE) {
            return;
        }

        g2.setColor(new Color(0, 0, 0, respawnFadeAlpha));
        g2.fillRect(0, 0, screenWidth, screenHeight);
    }

    public void playMusic(int i) {
        music.setFile(i);
        music.play();
        music.loop();
    }

}
