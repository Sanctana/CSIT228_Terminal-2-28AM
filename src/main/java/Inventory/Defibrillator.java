package Inventory;
import java.util.Random;

public class Defibrillator extends Item {
    Random random = new Random();
    public Defibrillator() {
        super("Defibrillator", "Stabilizes player's heart rate between 60 and 80 bpm.");
    }

    @Override
    public void use(battle.Character character) {
        character.setHeartBeat(random.nextInt(70,81));
    }
}
