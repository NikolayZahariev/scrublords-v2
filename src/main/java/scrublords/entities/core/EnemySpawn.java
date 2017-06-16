package scrublords.entities.core;

import scrublords.core.SpriteSheet;
import scrublords.entities.characters.Player;
import scrublords.entities.enemies.Enemy;
import scrublords.tilemaps.TileMap;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author Nikolay Zahariev <nikolay.g.zahariev@gmail.com>.
 */
public class EnemySpawn {
    private Enemy enemy;
    private Point enemySpawnPoint;
    private int enemyXSpawnCoordinate;
    private Random randomCoordinateGenerator = new Random();
    private int enemyYSpawnCoordinate;

    public EnemySpawn() {

    }

    public void spawnEnemies(int enemyNumber, TileMap tileMap, SpriteSheet spriteSheet, EnemyStats enemyStats, Movement movement, ArrayList<Enemy> enemies, Player player) {
        for (int i = 0; i < enemyNumber; i++) {
            enemy = new Enemy(tileMap, spriteSheet, enemyStats, movement);

            while (true) {
                enemyXSpawnCoordinate = randomCoordinateGenerator.nextInt(3000) + 50;
                enemyYSpawnCoordinate = randomCoordinateGenerator.nextInt(200) + 50;
                enemySpawnPoint = new Point(enemyXSpawnCoordinate, enemyYSpawnCoordinate);
                enemy.collision.calculateCorners(enemySpawnPoint.x, enemySpawnPoint.y);

                if(!(heroOnLeft(enemy, player) || heroOnRight(enemy, player))){
                    System.out.println("LOOOOOP");
                    continue;
                }

                if (checkEnemyFloat(enemy)) {
                    continue;
                }

                if (checkTileCollision(enemy)) {
                    enemyYSpawnCoordinate -= 1;
                    enemySpawnPoint = new Point(enemyXSpawnCoordinate, enemyYSpawnCoordinate);
                    enemy.collision.calculateCorners(enemySpawnPoint.x, enemySpawnPoint.y);
                }

                if (checkEnemyFloat(enemy)) {
                    enemy.collision.characterMapPlacement.setPosition(enemySpawnPoint.x, enemySpawnPoint.y);
                    enemies.add(enemy);
                    break;
                }
            }
        }
    }

    private boolean heroOnLeft(Enemy enemy, Player player) {
        return enemy.collision.characterMapPlacement.x > player.collision.characterMapPlacement.x - 150 && enemy.collision.characterMapPlacement.x < player.collision.characterMapPlacement.x;
    }

    private boolean heroOnRight(Enemy enemy, Player player) {
        return enemy.collision.characterMapPlacement.x < player.collision.characterMapPlacement.x + 150 && enemy.collision.characterMapPlacement.x > player.collision.characterMapPlacement.x;
    }

    private boolean checkEnemyFloat(Enemy enemy) {
        return ((!enemy.collision.bottomLeft && !enemy.collision.bottomRight) && (!enemy.collision.topLeft && !enemy.collision.topRight));
    }

    private boolean checkTileCollision(Enemy enemy) {
        return (enemy.collision.bottomLeft || enemy.collision.bottomRight || enemy.collision.topLeft || enemy.collision.topRight);
    }
}
