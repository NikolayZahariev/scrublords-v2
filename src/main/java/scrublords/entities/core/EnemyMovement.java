package scrublords.entities.core;

import scrublords.entities.characters.Player;
import scrublords.entities.enemies.Enemy;

/**
 * @author Nikolay Zahariev <nikolay.g.zahariev@gmail.com>.
 */
public class EnemyMovement {
    public Player player;
    public Enemy enemy;

    public EnemyMovement(Player player, Enemy enemy) {
        this.player = player;
        this.enemy = enemy;
    }

    public void moveToHero() {
        if (checkHeroPosition(enemy)) {
            if (enemy.collision.characterMapPlacement.x < player.collision.characterMapPlacement.x + 100) {
                enemy.moveSet.right = false;
                enemy.moveSet.left = true;
            }
            if (enemy.collision.characterMapPlacement.x > player.collision.characterMapPlacement.x + 100) {
                enemy.moveSet.left = false;
                enemy.moveSet.right = false;
            }
        } else {
            if (enemy.collision.characterMapPlacement.x > player.collision.characterMapPlacement.x - 100) {
                enemy.moveSet.right = true;
                enemy.moveSet.left = false;
            } else {
                enemy.moveSet.right = false;
                enemy.moveSet.left = false;
            }
        }
        enemy.moveSet.jumping = jump(enemy);
    }

    private boolean checkHeroPosition(Enemy enemy) {
        return enemy.collision.characterMapPlacement.x > player.collision.characterMapPlacement.x;
    }

    private boolean jump(Enemy enemy) {
        return (enemy.collision.characterMapPlacement.y > player.collision.characterMapPlacement.y) && (enemy.collision.characterMapPlacement.y <= 200);
    }
}
