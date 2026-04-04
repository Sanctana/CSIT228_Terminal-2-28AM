package battle;

public class Event {

    public void battle(Character player, Enemy enemy) {
        int playerDMG;
        int enemyDMG;

        while (player.getIsAlive() && enemy.getIsAlive()) {
            // player button selection
            playerDMG = player.getDamageOutput();
            enemyDMG = enemy.skill();

            // apply effects
            enemy.takeDamage(playerDMG);
            player.takeDamage(enemyDMG);

            // check player death (heartbeat condition)
            if (!player.getIsAlive() || !enemy.getIsAlive()) {
                break;
            }
        }

        if (player.getIsAlive()) {
            System.out.println("You won");
        } else {
            System.out.println("You lost");
        }
    }
}