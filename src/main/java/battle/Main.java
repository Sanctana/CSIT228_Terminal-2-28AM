package battle;

import javax.swing.JFrame;
import main.GamePanel;

public class Main {
    public static void main(String[] args) {
        GamePanel gp = new GamePanel();
        EnemyContainer enemyContainer = new EnemyContainer();

        CharacterContainer characterContainer = new CharacterContainer(gp);

        JFrame frame = new JFrame("Terminal228 Battle Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setResizable(false);

        frame.setContentPane(new Panel(gp, characterContainer.getCharacter(1), enemyContainer.getRandomEnemy(),
                new BattleLauncher.BattleResultListener() {
                    @Override
                    public void onBattleWon(Character player) {
                        frame.dispose();
                    }

                    @Override
                    public void onBattleLost(Character player) {
                        frame.dispose();
                    }
                }));

        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
    }
}
