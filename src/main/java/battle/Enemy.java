package battle;
import java.util.Random;


// not final
public class Enemy {

    private int health = 100;

    public int skill(){

        Random rand = new Random();
        return rand.nextInt(20) + 10; // damage 10-30
    }

    public void takeDamage(int dmg){
        health -= dmg;
    }

    public int getHealth(){
        return health;
    }

}
