package main;

import Utilities.States.TileType;
import Utilities.States.GameState; // ADDED THIS IMPORT
import entity.Entity;
import entity.EntityState;
import entity.Player;

public class CollisionChecker {
    GamePanel gp;
    java.util.Random random = new java.util.Random();

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    private boolean isWalkable(TileType tileType) {
        return tileType == TileType.WALKABLE
                || tileType == TileType.SPAWN_POINT
                || tileType == TileType.BATTLE_TRIGGER;
    }

    public TileType getTileTypeUnderEntity(Entity entity) {
        int centerX = entity.worldX + entity.solidArea.x + (entity.solidArea.width / 2);
        int centerY = entity.worldY + entity.solidArea.y + (entity.solidArea.height / 2);

        int col = centerX / gp.tileSize;
        int row = centerY / gp.tileSize;
        int tileNum = gp.map.mapTileNum.get(row)[col];

        return gp.map.tile.get(tileNum).getTileType();
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
                tileNum1 = gp.map.mapTileNum.get(entityTopRow)[entityLeftCol];
                tileNum2 = gp.map.mapTileNum.get(entityTopRow)[entityRightCol];
                break;
            case DOWN:
                entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
                tileNum1 = gp.map.mapTileNum.get(entityBottomRow)[entityLeftCol];
                tileNum2 = gp.map.mapTileNum.get(entityBottomRow)[entityRightCol];
                break;
            case LEFT:
                entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
                tileNum1 = gp.map.mapTileNum.get(entityTopRow)[entityLeftCol];
                tileNum2 = gp.map.mapTileNum.get(entityBottomRow)[entityLeftCol];
                break;
            case RIGHT:
                entityRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;
                tileNum1 = gp.map.mapTileNum.get(entityTopRow)[entityRightCol];
                tileNum2 = gp.map.mapTileNum.get(entityBottomRow)[entityRightCol];
                break;
            default:
                return;
        }

        TileType tileType1 = gp.map.tile.get(tileNum1).getTileType();
        TileType tileType2 = gp.map.tile.get(tileNum2).getTileType();

        if (tileType1 == TileType.COLLISION_TILE || tileType2 == TileType.COLLISION_TILE) {
            entity.state = EntityState.IDLE;
        } else if (isWalkable(tileType1) && isWalkable(tileType2)) {
            entity.state = EntityState.MOVING;
            if (entity instanceof Player) {
                checkRandomEncounter(10);
            }
        } else if (tileType1 == TileType.DANGER_ZONE || tileType2 == TileType.DANGER_ZONE) {
            entity.state = EntityState.MOVING;
            if (entity instanceof Player) {
                checkRandomEncounter(1000);
            }
        }
    }

    private void checkRandomEncounter(int chance) {
        if (random.nextInt(1000) < chance) {
            gp.gameState = GameState.BATTLE;
            gp.player.state = EntityState.IDLE;
        }
    }
}