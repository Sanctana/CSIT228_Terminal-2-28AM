package battle;
import java.util.*;

public class EnemyContainer {
    ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    Random random = new Random();
    public EnemyContainer(){
        enemies.add(new Scalper());
        enemies.add(new Stillborn());
        enemies.add(new Brighteyes());
    }

    public Enemy getEnemyAt(int index){
        try{
            return enemies.get(index);
        }catch(Exception e){
            System.out.println("No such entity exists here.");
        }

        return null;
    }

    public Enemy getRandomEnemy(){
        if(enemies.size() == 0){
            System.out.println("No enemy in container");
            return null;
        }
        else{
            return enemies.get(random.nextInt(enemies.size()));
        }
    }
}
