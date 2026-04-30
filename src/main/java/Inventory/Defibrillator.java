package Inventory;

import java.util.Random;
import battle.Character;

public class Defibrillator extends Item {
    Random random = new Random();

    public Defibrillator(int quantity) {
        super("Defibrillator", "Stabilizes player's heart rate between 60 and 80 bpm.", quantity);
    }

    @Override
    public void use(Character character) {
        if (use()) {
            character.setHeartBeat(random.nextInt(70, 81));
        }
    }
}
