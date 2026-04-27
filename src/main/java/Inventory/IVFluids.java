package Inventory;

import entity.Player;

public class IVFluids extends Item {

    public IVFluids() {
        super("IV Fluids", "Adds 40 heartbeat to the player.");
    }

    @Override
    public void use(battle.Character character) {
        character.setHeartBeat(character.getHeartBeat() + 100);
    }
}
