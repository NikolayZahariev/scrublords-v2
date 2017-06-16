package scrublords.states;

import scrublords.entities.characters.Berserker;
import scrublords.entities.characters.Lich;
import scrublords.entities.characters.Player;
import scrublords.entities.core.EnemySpawn;
import scrublords.entities.enemies.Enemy;
import scrublords.entities.core.EnemyMovement;
import scrublords.entities.enemies.Slugger;
import scrublords.main.GamePanel;
import scrublords.main.State;
import scrublords.tilemaps.Background;
import scrublords.tilemaps.TileMap;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * @author Denis Dimitrov <denis.k.dimitrov@gmail.com>.
 */
public class LevelOne implements State {
    private TileMap tileMap;
    private Background background = new Background(0.1);
    private Berserker berserker;
    private Lich lich;
    private Player player;
    private ArrayList<Enemy> enemies;
    private EnemyMovement enemyMovement;
    private Slugger slugger;
    private int enemyNumber = 10;
    private EnemySpawn enemySpawn = new EnemySpawn();

    public LevelOne() {
        init();
    }

    @Override
    public void init() {
        tileMap = new TileMap(30);
        tileMap.tileLoading.loadTiles("/tilesets/grasstileset.gif");
        tileMap.mapLoading.loadMap("/maps/level1-map.map");
        tileMap.setPosition(0, 0);
        background.getResource("/backgrounds/levelone.gif");
        berserker = new Berserker(tileMap);
        lich = new Lich(tileMap);
        slugger = new Slugger(tileMap);
        if (CharState.character == "berserker") {
            player = new Player(tileMap, berserker.spriteSheet, berserker.character, berserker.movement);
            player.collision.characterMapPlacement.setPosition(100, 200);
        }
        if (CharState.character == "lich") {
            player = new Player(tileMap, lich.spriteSheet, lich.character, lich.movement);
            player.collision.characterMapPlacement.setPosition(100, 200);
        }

        enemies = new ArrayList<>();
        enemySpawn.spawnEnemies(enemyNumber, tileMap, slugger.spriteSheet, slugger.enemyStats, slugger.movement, enemies);
    }

    @Override
    public void update() {
        player.update();
        tileMap.setPosition(GamePanel.WIDTH / 2 - player.collision.characterMapPlacement.getx(), GamePanel.HEIGHT / 2 - player.collision.characterMapPlacement.gety());
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            player.checkDamageTaken(enemy);
            player.meleeAttack(enemy);
            enemyMovement = new EnemyMovement(player, enemy);
            enemyMovement.moveToHero();
            enemy.update();
            if (enemy.enemyStats.dead) {
                enemies.remove(i);
                enemy.enemyStats.dead = false;
            }
        }
    }

    @Override
    public void draw(Graphics g) {
        background.draw(g);
        tileMap.draw(g);
        player.draw(g);
        for (Enemy enemy : enemies) {
            enemy.draw(g);
        }
    }

    @Override
    public void keyPressed(int k) {
        if (k == KeyEvent.VK_A | k == KeyEvent.VK_LEFT) {
            player.moveSet.left = false;
        }
        if (k == KeyEvent.VK_D | k == KeyEvent.VK_RIGHT) {
            player.moveSet.right = false;
        }
        if (k == KeyEvent.VK_W | k == KeyEvent.VK_UP) {
            player.moveSet.jumping = false;
        }
        if (k == KeyEvent.VK_J | k == KeyEvent.VK_Z) {
            player.character.attacking = false;
        }
    }

    @Override
    public void keyReleased(int k) {
        if (k == KeyEvent.VK_A | k == KeyEvent.VK_LEFT) {
            player.moveSet.left = true;
        }
        if (k == KeyEvent.VK_D | k == KeyEvent.VK_RIGHT) {
            player.moveSet.right = true;
        }
        if (k == KeyEvent.VK_W | k == KeyEvent.VK_UP) {
            player.moveSet.jumping = true;
        }
        if (k == KeyEvent.VK_J | k == KeyEvent.VK_Z) {
            player.character.attacking = true;
        }
    }
}