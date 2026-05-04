package battle;

import main.GamePanel;
import utilities.UtilityTool;

import javax.swing.JPanel;

import entity.enemy.Enemy;
import entity.player.Character;

public final class BattleLauncher {

    public static Enemy createRandomEnemy() {
        return UtilityTool.getRandomEnemy();
    }

    public static JPanel createBattlePanel(GamePanel gp, Enemy enemy, BattleResultListener resultListener) {
        return new Panel(gp, gp.player, enemy, resultListener);
    }

    public interface BattleResultListener {
        void onBattleWon(Character player);

        void onBattleLost(Character player);
    }
}
