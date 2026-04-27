package battle;

import main.GamePanel;
import javax.swing.JPanel;
import java.util.Arrays;

public final class BattleLauncher {

    public static Enemy createRandomEnemy() {
        return new EnemyContainer().getRandomEnemy();
    }

    public static JPanel createBattlePanel(GamePanel gp, Enemy enemy, BattleResultListener resultListener) {
        Character player = createCharacterForBattle(gp);
        player.setHeartBeat(gp.player.heartRate);
        player.inventory = gp.player.inventory;
        player.itemAmounts = Arrays.copyOf(gp.player.itemAmounts, gp.player.itemAmounts.length);
        return new Panel(gp, player, enemy, resultListener);
    }

    private static Character createCharacterForBattle(GamePanel gp) {
        return switch (gp.player.characterType) {
            case OFFICER -> new Officer(gp);
            case DETECTIVE -> new Detective(gp);
            case INTRUDER -> new Intruder(gp);
            case ARTIST -> new Artist(gp);
            case COLLECTOR -> new DebtCollector(gp);
        };
    }

    public interface BattleResultListener {
        void onBattleWon(Character player);
        void onBattleLost(Character player);
    }
}
