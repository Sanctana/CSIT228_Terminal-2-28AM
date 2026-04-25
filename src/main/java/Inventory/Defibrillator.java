package Inventory;

import entity.Player;

public class Defibrillator extends Item {
    public Defibrillator() {
        super("Defibrillator", "Stabilizes player's heart rate between 60 and 80 bpm.");
    }

    @Override
    public void use(Player player) {
        player.heartRate = 70;
    }
}
