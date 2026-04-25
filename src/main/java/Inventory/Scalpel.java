package Inventory;

import entity.Player;

public class Scalpel extends Item {

    public Scalpel() {
        super("Scalpel", "Reduces 40 heartbeat to the player.");
    }

    @Override
    public void use(Player player) {
        player.heartRate -= 40;
    }
}
