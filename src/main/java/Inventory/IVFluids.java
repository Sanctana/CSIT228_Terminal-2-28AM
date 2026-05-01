package Inventory;

import entity.Player.Character;

public class IVFluids extends Item {

    public IVFluids(int quantity) {
        super("IV Fluids", "Adds 40 heartbeat to the player.", quantity);
    }

    @Override
    public void use(Character character) {
        if (use()) {
            character.setHeartBeat(character.getHeartBeat() + 100);
        }
    }
}
