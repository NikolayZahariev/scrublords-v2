package scrublords.entities.enemies;

import scrublords.core.CollisionDetection;
import scrublords.core.SpriteDimensions;
import scrublords.core.SpriteSheet;
import scrublords.core.Visualization;
import scrublords.entities.core.*;
import scrublords.tilemaps.TileMap;

import java.util.ArrayList;

/**
 * @author Nikolay Zahariev <nikolay.g.zahariev@gmail.com>.
 */
public class LevelOneBoss {
    public CollisionDetection collision;
    public SpriteDimensions spriteDimensions;
    public SpriteSheet spriteSheet;
    public EnemyStats enemyStats = new EnemyStats(30, 30, false, false, 0, false, 3, new ArrayList<>(), new int[]{3});
    public Actions action = new ActionsBuilder().buildAnimations();
    public Movement movement;
    public Visualization visualization;

    public LevelOneBoss(TileMap tileMap) {
        collision = new CollisionDetection(tileMap);
        spriteSheet = new SpriteSheet("/sprites/enemies/levelOneBoss.gif", 1);
        movement = new Movement(0.05, 0.6, 0.4, 0.15, 4.0, -4.8, 0.3);
        spriteDimensions = new SpriteDimensions(30, 30);
        collision.cwidth = 20;
        collision.cheight = 20;
        spriteSheet.getTestEnemySpriteSheet(enemyStats, spriteDimensions);
        visualization = new Visualization();
        visualization.setFrames(enemyStats.sprites.get(action.idle));
        visualization.setDelay(300);

    }
}

