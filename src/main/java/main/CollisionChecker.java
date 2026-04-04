package main;

import entity.Entity;
import entity.EntityState;
import tile.TileType;

public class CollisionChecker {
    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    private boolean isWalkable(TileType tileType) {
        return tileType == TileType.WALKABLE || tileType == TileType.SPAWN_POINT;
    }

    public void checkTile(Entity entity) {

        int entityLeftWorldX = entity.worldX + entity.solidArea.x;
        int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.worldY + entity.solidArea.y;
        int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        int entityLeftCol = entityLeftWorldX / gp.tileSize;
        int entityRightCol = entityRightWorldX / gp.tileSize;
        int entityTopRow = entityTopWorldY / gp.tileSize;
        int entityBottomRow = entityBottomWorldY / gp.tileSize;

        int tileNum1, tileNum2;

        switch (entity.direction) {

            case UP:
                entityTopRow = (entityTopWorldY - entity.speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum.get(entityTopRow)[entityLeftCol];
                tileNum2 = gp.tileM.mapTileNum.get(entityTopRow)[entityRightCol];
                break;

            case DOWN:
                entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum.get(entityBottomRow)[entityLeftCol];
                tileNum2 = gp.tileM.mapTileNum.get(entityBottomRow)[entityRightCol];
                break;

            case LEFT:
                entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum.get(entityTopRow)[entityLeftCol];
                tileNum2 = gp.tileM.mapTileNum.get(entityBottomRow)[entityLeftCol];
                break;

            case RIGHT:
                entityRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum.get(entityTopRow)[entityRightCol];
                tileNum2 = gp.tileM.mapTileNum.get(entityBottomRow)[entityRightCol];
                break;

            default:
                return;
        }

        TileType tileType1 = gp.tileM.tile.get(tileNum1).getTileType();
        TileType tileType2 = gp.tileM.tile.get(tileNum2).getTileType();

        if (tileType1 == TileType.COLLISION_TILE || tileType2 == TileType.COLLISION_TILE) {
            entity.state = EntityState.IDLE;
        } else if (isWalkable(tileType1) && isWalkable(tileType2)) {
            entity.state = EntityState.MOVING;
        } else if (tileType1 == TileType.TO_NEXT_MAP || tileType2 == TileType.TO_NEXT_MAP) {
            entity.state = EntityState.TO_NEXT_MAP;
        } else if (tileType1 == TileType.TO_PREVIOUS_MAP || tileType2 == TileType.TO_PREVIOUS_MAP) {
            entity.state = EntityState.TO_PREVIOUS_MAP;
        }
    }
}
