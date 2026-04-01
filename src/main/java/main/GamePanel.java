package main;

import environment.EnvironmentManager;
import entity.EntityState;
import entity.Player;
import tile.Map;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import Maps.ThirdFloorMap;

public class GamePanel extends JPanel implements Runnable {
    private final int originalTileSize = 16;
    private final int scale = 4;

    public final int tileSize = originalTileSize * scale; // 64 by 64
    private final int maxScreenCol = 20;
    private final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    SoundManager music = new SoundManager();

    // FULL SCREEN
    private int screenWidth2 = screenWidth;
    private int screenHeight2 = screenHeight;
    private BufferedImage tempScreen;
    private Graphics2D graphics2d;
    private final Object renderLock = new Object();

    int FPS = 60;

    public Map tileM;
    KeyHandler keyH = new KeyHandler(this);
    private Thread gameThread;
    public CollisionChecker cChecker;
    public AssetSetter aSetter;
    public UI ui;
    public Player player;

    // GAME STATE
    public GameState gameState;

    EnvironmentManager eManager = new EnvironmentManager(this);

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        tileM = new ThirdFloorMap(this); // Default map
        gameThread = new Thread(this, "GameLoop");
        cChecker = new CollisionChecker(this);
        aSetter = new AssetSetter(this);
        ui = new UI(this);
    }

    public void setupGame() {
        eManager.setup();
        gameState = GameState.TITLE;

        playMusic(0);

        tempScreen = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
        graphics2d = (Graphics2D) tempScreen.getGraphics();
    }

    public void setFullScreen() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        gd.setFullScreenWindow(Main.window);

        screenWidth2 = Main.window.getWidth();
        screenHeight2 = Main.window.getHeight();
    }

    public void startGameThread() {
        gameThread.start();
    }

    public void run() {
        double drawInterval = 1_000_000_000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        while (gameThread != null) {
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                drawToTempScreen();
                repaint();
                delta--;
                drawCount++;
            }

            if (timer >= 1_000_000_000) {
                System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }
        }
    }

    public void update() {
        if (gameState == GameState.PLAY) {
            player.update();
            if (player.state == EntityState.MOVING_NEXT_MAP) {
                tileM = tileM.getNextMap();
                player.setLocation(1, 1);
                player.state = EntityState.IDLE;
            }
        }
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

            // TITLE SCREEN
            if (gameState == GameState.TITLE) {
                ui.draw(graphics2d);
            }
            // OTHERS
            else if (gameState == GameState.PLAY) {
                tileM.draw(graphics2d);
                player.draw(graphics2d);

                eManager.draw(graphics2d);
                ui.draw(graphics2d);
            }
        }
    }

    public void playMusic(int i) {
        music.setFile(i);
        music.play();
        music.loop();
    }

}
