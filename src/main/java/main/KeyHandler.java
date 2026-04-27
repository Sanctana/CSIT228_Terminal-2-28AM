package main;

import entity.CharacterType;
import entity.Player;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import Utilities.States.TitleScreenState;
import Utilities.States.GameState;

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

        if (gp.gameState == GameState.INVENTORY) {
            handleInventoryInput(code);
            return;
        }

        if (gp.gameState == GameState.GAMEOVER) {
            handleGameOverInput(code);
            return;
        }

        if (gp.gameState != GameState.PLAY && gp.gameState != GameState.PAUSE) {
            return;
        }

        // PLAY STATE
        switch (code) {
        case KeyEvent.VK_W -> upPressed = true;
        case KeyEvent.VK_S -> downPressed = true;
        case KeyEvent.VK_A -> leftPressed = true;
        case KeyEvent.VK_D -> rightPressed = true;
        case KeyEvent.VK_O -> gp.toggleOneShotMode();
        case KeyEvent.VK_ESCAPE -> {
            if (gp.gameState == GameState.PLAY) {
                gp.gameState = GameState.PAUSE;
            } else if (gp.gameState == GameState.PAUSE) {
                gp.gameState = GameState.PLAY;
            }
        }
        case KeyEvent.VK_I -> {
            if (gp.gameState == GameState.PLAY) {
                gp.gameState = GameState.INVENTORY;
                gp.ui.commandNum = 0;
            } else if (gp.gameState == GameState.INVENTORY) {
                gp.gameState = GameState.PLAY;
            }
        }
        }
    }

    private void handleGameOverInput(int code) {
        if (code == KeyEvent.VK_ENTER) {
            gp.requestRespawn();
        }
    }

    private void handleInventoryInput(int code) {
        int options = gp.player == null ? 0 : gp.player.getInventory().length;

        if (code == KeyEvent.VK_I || code == KeyEvent.VK_ESCAPE) {
            gp.gameState = GameState.PLAY;
            return;
        }

        if (options <= 0) {
            return;
        }

        if (code == KeyEvent.VK_W) {
            gp.ui.commandNum = (gp.ui.commandNum - 1 + options) % options;
            return;
        }

        if (code == KeyEvent.VK_S) {
            gp.ui.commandNum = (gp.ui.commandNum + 1) % options;
            return;
        }

        if (code == KeyEvent.VK_ENTER) {
            gp.player.useItem(gp.ui.commandNum);
            gp.ui.commandNum = 0; // Reset selection after using an item
        }
    }

    private void handleTitleInput(int code) {
        int options = (gp.ui.titleScreenState == TitleScreenState.MAIN_MENU) ? 3 : 6;
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

        if (gp.ui.titleScreenState == TitleScreenState.MAIN_MENU) {
            switch (gp.ui.commandNum) {
            case 0 -> gp.ui.titleScreenState = TitleScreenState.CHARACTER_SELECT;
            case 1 -> {
                /* add later "LOAD GAME" */ }
            case 2 -> System.exit(0);
            }
        } else { // titleScreenState == TitleScreenState.CHARACTER_SELECT
            CharacterType selectedCharacter = null;

            switch (gp.ui.commandNum) {
            case 0 -> selectedCharacter = CharacterType.DETECTIVE;
            case 1 -> selectedCharacter = CharacterType.OFFICER;
            case 2 -> selectedCharacter = CharacterType.INTRUDER;
            case 3 -> selectedCharacter = CharacterType.ARTIST;
            case 4 -> selectedCharacter = CharacterType.COLLECTOR;
            case 5 -> gp.ui.titleScreenState = TitleScreenState.MAIN_MENU;
            }

            if (gp.ui.commandNum >= 0 && gp.ui.commandNum <= 4) {
                gp.requestNewGame(selectedCharacter);
            }
        }

    }

    public void resetMovementInput() {
        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
        case KeyEvent.VK_W -> upPressed = false;
        case KeyEvent.VK_S -> downPressed = false;
        case KeyEvent.VK_A -> leftPressed = false;
        case KeyEvent.VK_D -> rightPressed = false;
        }
    }
}
