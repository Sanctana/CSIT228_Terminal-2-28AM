package battle;

public class Event {

    public void battle(Character player, Enemy enemy){
        int playerDMG;
        int enemyDMG;
        boolean playerWin = false;

        while(player.getIsAlive() && enemy.getIsAlive()){
            // player button selection
            playerDMG = player.getDamageOutput();
            enemyDMG = enemy.skill();

            // apply effects
            enemy.setHealth(playerDMG);
            player.setHeartBeat((int)(enemyDMG - (enemyDMG * player.getResistance())));

            // check player death (heartbeat condition)
            if(player.getHeartBeat() > player.getMaxHeartBeat() || player.getHeartBeat() < 40){
                player.setIsAlive(false);
                playerWin = false;
                break;
            }

            // check enemy death (health condition)
            if(enemy.getHealth() <= 0){
                enemy.setIsAlive(false);
                playerWin = true;
                break;
            }
        }

        if(playerWin){
            System.out.println("You won");
        } else {
            System.out.println("You lost");
        }
    }
}