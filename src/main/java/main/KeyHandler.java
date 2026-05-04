package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.nio.file.Path;

import ui.UI;
import utilities.SaveManager;
import entity.player.CharacterType;
import utilities.states.GameState;
import utilities.states.TitleScreenState;

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

        if (gp.gameState == GameState.GAME_OVER) {
            handleGameOverInput(code);
            return;
        }

        if (gp.gameState == GameState.VICTORY_ENDING) {
            handleVictoryEndingInput(code);
            return;
        }

        if (gp.gameState == GameState.PAUSE) {
            handlePauseInput(code);
            return;
        }

        if (gp.gameState != GameState.PLAY) {
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
            gp.gameState = GameState.PAUSE;
            gp.ui.commandNum = 0;
            gp.ui.pauseQuitConfirm = false;
            gp.ui.pauseSavePrompt = UI.PauseSavePrompt.NONE;
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

    private void handleVictoryEndingInput(int code) {
        if (code == KeyEvent.VK_ENTER && gp.isVictoryEndingComplete()) {
            gp.requestReturnToMainMenu();
        }
    }

    private void handlePauseInput(int code) {
        if (gp.ui.pauseQuitConfirm) {
            handlePauseQuitConfirmationInput(code);
            return;
        }

        if (gp.ui.pauseSavePrompt != UI.PauseSavePrompt.NONE) {
            handlePauseSavePromptInput(code);
            return;
        }

        int options = 4;

        if (code == KeyEvent.VK_ESCAPE) {
            gp.gameState = GameState.PLAY;
            gp.ui.commandNum = 0;
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

        if (code != KeyEvent.VK_ENTER) {
            return;
        }

        switch (gp.ui.commandNum) {
        case 0 -> {
            gp.gameState = GameState.PLAY;
            gp.ui.commandNum = 0;
        }
        case 1 -> gp.saveCurrentGame();
        case 2 -> {
            gp.ui.pauseSavePrompt = UI.PauseSavePrompt.MAIN_MENU;
            gp.ui.commandNum = 0;
        }
        case 3 -> {
            gp.ui.pauseSavePrompt = UI.PauseSavePrompt.QUIT;
            gp.ui.commandNum = 0;
        }
        }
    }

    private void handlePauseSavePromptInput(int code) {
        if (code == KeyEvent.VK_ESCAPE) {
            cancelPauseSavePrompt();
            return;
        }

        if (code == KeyEvent.VK_A || code == KeyEvent.VK_D || code == KeyEvent.VK_W || code == KeyEvent.VK_S) {
            gp.ui.commandNum = (gp.ui.commandNum + 1) % 3;
            return;
        }

        if (code != KeyEvent.VK_ENTER) {
            return;
        }

        UI.PauseSavePrompt prompt = gp.ui.pauseSavePrompt;

        switch (gp.ui.commandNum) {
        case 0 -> {
            gp.saveCurrentGame();
            finishPauseSavePrompt(prompt);
        }
        case 1 -> finishPauseSavePrompt(prompt);
        case 2 -> {
            gp.ui.pauseSavePrompt = UI.PauseSavePrompt.NONE;
            gp.ui.commandNum = prompt == UI.PauseSavePrompt.MAIN_MENU ? 2 : 3;
        }
        }
    }

    private void finishPauseSavePrompt(UI.PauseSavePrompt prompt) {
        gp.ui.pauseSavePrompt = UI.PauseSavePrompt.NONE;
        gp.ui.commandNum = 0;

        if (prompt == UI.PauseSavePrompt.MAIN_MENU) {
            gp.returnToMainMenuFromPause();
        } else if (prompt == UI.PauseSavePrompt.QUIT) {
            gp.ui.pauseQuitConfirm = true;
        }
    }

    private void cancelPauseSavePrompt() {
        UI.PauseSavePrompt prompt = gp.ui.pauseSavePrompt;
        gp.ui.pauseSavePrompt = UI.PauseSavePrompt.NONE;
        gp.ui.commandNum = prompt == UI.PauseSavePrompt.MAIN_MENU ? 2 : 3;
    }

    private void handlePauseQuitConfirmationInput(int code) {
        if (code == KeyEvent.VK_ESCAPE) {
            gp.ui.pauseQuitConfirm = false;
            gp.ui.commandNum = 3;
            return;
        }

        if (code == KeyEvent.VK_A || code == KeyEvent.VK_D || code == KeyEvent.VK_W || code == KeyEvent.VK_S) {
            gp.ui.commandNum = (gp.ui.commandNum + 1) % 2;
            return;
        }

        if (code != KeyEvent.VK_ENTER) {
            return;
        }

        if (gp.ui.commandNum == 0) {
            System.exit(0);
        } else {
            gp.ui.pauseQuitConfirm = false;
            gp.ui.commandNum = 3;
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
        if (gp.ui.titleScreenState == TitleScreenState.LOAD_GAME) {
            handleLoadGameInput(code);
            return;
        }

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
                refreshSaveFiles();
                gp.ui.titleScreenState = TitleScreenState.LOAD_GAME;
                gp.ui.commandNum = 0;
            }
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
            case 5 -> {
                gp.ui.titleScreenState = TitleScreenState.MAIN_MENU;
                gp.ui.commandNum = 0;
            }
            }

            if (gp.ui.commandNum >= 0 && gp.ui.commandNum <= 4) {
                gp.requestNewGame(selectedCharacter);
            }
        }

    }

    private void handleLoadGameInput(int code) {
        if (gp.ui.loadDeleteConfirm) {
            handleLoadDeleteConfirmationInput(code);
            return;
        }

        int options = gp.ui.saveFiles.isEmpty() ? 1 : gp.ui.saveFiles.size() + 1;

        if (code == KeyEvent.VK_ESCAPE) {
            gp.ui.titleScreenState = TitleScreenState.MAIN_MENU;
            gp.ui.commandNum = 1;
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

        if (code == KeyEvent.VK_D && !gp.ui.saveFiles.isEmpty() && gp.ui.commandNum < gp.ui.saveFiles.size()) {
            gp.ui.pendingDeleteSaveIndex = gp.ui.commandNum;
            gp.ui.loadDeleteConfirm = true;
            gp.ui.commandNum = 0;
            return;
        }

        if (code != KeyEvent.VK_ENTER) {
            return;
        }

        if (gp.ui.saveFiles.isEmpty() || gp.ui.commandNum == gp.ui.saveFiles.size()) {
            gp.ui.titleScreenState = TitleScreenState.MAIN_MENU;
            gp.ui.commandNum = 1;
            return;
        }

        gp.requestLoadSavedGame(gp.ui.saveFiles.get(gp.ui.commandNum));
    }

    private void handleLoadDeleteConfirmationInput(int code) {
        if (code == KeyEvent.VK_ESCAPE) {
            gp.ui.loadDeleteConfirm = false;
            gp.ui.commandNum = gp.ui.pendingDeleteSaveIndex;
            return;
        }

        if (code == KeyEvent.VK_A || code == KeyEvent.VK_D || code == KeyEvent.VK_W || code == KeyEvent.VK_S) {
            gp.ui.commandNum = (gp.ui.commandNum + 1) % 2;
            return;
        }

        if (code != KeyEvent.VK_ENTER) {
            return;
        }

        if (gp.ui.commandNum == 1) {
            gp.ui.loadDeleteConfirm = false;
            gp.ui.commandNum = gp.ui.pendingDeleteSaveIndex;
            return;
        }

        int deleteIndex = Math.max(0, Math.min(gp.ui.pendingDeleteSaveIndex, gp.ui.saveFiles.size() - 1));
        try {
            Path savePath = gp.ui.saveFiles.get(deleteIndex);
            SaveManager.delete(savePath);
            refreshSaveFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
        gp.ui.loadDeleteConfirm = false;
        gp.ui.commandNum = 0;
    }

    private void refreshSaveFiles() {
        try {
            gp.ui.saveFiles = SaveManager.listSaves();
        } catch (IOException e) {
            gp.ui.saveFiles = java.util.List.of();
            e.printStackTrace();
        }
    }

    public void resetMovementInput() {
        upPressed = downPressed = leftPressed = rightPressed = false;
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
