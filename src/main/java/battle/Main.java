package battle;
import javax.swing.*;

public class Main {
    public static void main(String[] args){
        EnemyContainer enemyContainer = new EnemyContainer();

        CharacterContainer characterContainer = new CharacterContainer();

        JFrame frame = new JFrame("Terminal228 Battle Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setResizable(false);

        frame.setContentPane(new Panel(characterContainer.getCharacter(0), enemyContainer.getRandomEnemy()));

        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
    }
}