package Utilities.States;

// Don't change the order please since all tile names rely on this.
// Any additional tile types must be appended at the end instead of inserted in the middle
public enum TileType {
    WALKABLE, // 0
    TO_NEXT_MAP, // 1
    TO_PREVIOUS_MAP, // 2
    SPAWN_POINT, // 3
    COLLISION_TILE, // 4
    BATTLE_TRIGGER // 5
}
