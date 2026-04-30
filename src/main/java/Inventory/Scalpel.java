package Inventory;

import entity.Player.Character;

public class Scalpel extends Item {

    public Scalpel(int quantity) {
        super("Scalpel", "Reduces 40 heartbeat to the player.", quantity);
    }

    @Override
    public void use(Character character) {
        if (use()) {
            character.setHeartBeat(character.getHeartBeat() - 40);
        }
    }
}
