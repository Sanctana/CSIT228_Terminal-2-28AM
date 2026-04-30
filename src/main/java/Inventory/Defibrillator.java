package Inventory;

import entity.Player.Character;

public class Defibrillator extends Item {
    public Defibrillator(int quantity) {
        super("Defibrillator", "Stabilizes player's heart rate between 60 and 80 bpm.", quantity);
    }

    @Override
    public void use(Character character) {
        if (use()) {
            character.setHeartBeat((int) (Math.random() * (80 - 70 + 1)) + 70);
        }
    }
}
