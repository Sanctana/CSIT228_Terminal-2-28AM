package main;

import entity.Entity;
import entity.EntityState;

public class CollisionChecker {
    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
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

        if (gp.tileM.tile.get(tileNum1).collision || gp.tileM.tile.get(tileNum2).collision) {
            entity.state = EntityState.IDLE;
        } else if (gp.tileM.tile.get(tileNum1).isMoveNextMap() || gp.tileM.tile.get(tileNum2).isMoveNextMap()) {
            entity.state = EntityState.MOVING_NEXT_MAP;
        } else {
            entity.state = EntityState.MOVING;
        }
    }
}
