package main;

import entity.CharacterType;
import entity.Player;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    GamePanel gp;
    public boolean upPressed, downPressed, leftPressed, rightPressed;

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // TITLE STATE
        if (gp.gameState == GameState.TITLE) {
            handleTitleInput(code);
            return;

        }

        // PLAY STATE
        if (code == KeyEvent.VK_W) {
            upPressed = true; // D
        }
        if (code == KeyEvent.VK_S) {
            downPressed = true;
        }
        if (code == KeyEvent.VK_A) {
            leftPressed = true;
        }
        if (code == KeyEvent.VK_D) {
            rightPressed = true;
        }
        if (code == KeyEvent.VK_ESCAPE) {
            if (gp.gameState == GameState.PLAY) {
                gp.gameState = GameState.PAUSE;
            } else if (gp.gameState == GameState.PAUSE) {
                gp.gameState = GameState.PLAY;
            }
        }
    }

    private void handleTitleInput(int code) {
        int options = (gp.ui.titleScreenState == 0) ? 3 : 6;
        if (code == KeyEvent.VK_W) {
            gp.ui.commandNum = (gp.ui.commandNum - 1 + options) % options;
            return;
        }
        if (code == KeyEvent.VK_S) {
            gp.ui.commandNum = (gp.ui.commandNum + 1) % options;
            return;
        }
        if (code != KeyEvent.VK_ENTER)
            return;

        if (gp.ui.titleScreenState == 0) {
            switch (gp.ui.commandNum) {
                case 0 -> gp.ui.titleScreenState = 1;
                case 1 -> {
                    /* add later "LOAD GAME" */ }
                case 2 -> System.exit(0);
            }
        } else { // titleScreenState == 1
            switch (gp.ui.commandNum) {
                case 0 -> {
                    gp.player = new Player(gp, this, CharacterType.DETECTIVE);
                    gp.gameState = GameState.PLAY;
                }
                case 1 -> {
                    gp.player = new Player(gp, this, CharacterType.OFFICER);
                    gp.gameState = GameState.PLAY;
                }
                case 2 -> {
                    gp.player = new Player(gp, this, CharacterType.INTRUDER);
                    gp.gameState = GameState.PLAY;
                }
                case 3 -> {
                    gp.player = new Player(gp, this, CharacterType.ARTIST);
                    gp.gameState = GameState.PLAY;
                }
                case 4 -> {
                    gp.player = new Player(gp, this, CharacterType.COLLECTOR);
                    gp.gameState = GameState.PLAY;
                }
                case 5 -> gp.ui.titleScreenState = 0;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W) {
            upPressed = false;
        }
        if (code == KeyEvent.VK_S) {
            downPressed = false;
        }
        if (code == KeyEvent.VK_A) {
            leftPressed = false;
        }
        if (code == KeyEvent.VK_D) {
            rightPressed = false;
        }
    }
}
