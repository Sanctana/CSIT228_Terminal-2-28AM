package main;

import javax.swing.JFrame;
public class Game {
    public static JFrame window;

    void main(String[] args) {
        window = new JFrame("Terminal-2-28AM");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);
        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gamePanel.setupGame();
        gamePanel.startGameThread();
    }

}
