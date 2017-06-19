package scrublords.states;

import scrublords.entities.characters.Berserker;
import scrublords.entities.characters.Lich;
import scrublords.entities.characters.Player;
import scrublords.entities.core.EnemyMovement;
import scrublords.entities.core.EnemySpawner;
import scrublords.entities.enemies.Enemy;
import scrublords.entities.enemies.LevelOneBoss;
import scrublords.entities.enemies.Slugger;
import scrublords.main.GamePanel;
import scrublords.main.State;
import scrublords.states.CharState;
import scrublords.tilemaps.Background;
import scrublords.tilemaps.TileMap;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Objects;

/**
 * @author Denis Dimitrov <denis.k.dimitrov@gmail.com>.
 */
public class Stage implements State {
    public static int mapPitfall;
    public static int maxX;
    public static int maxY;
    private TileMap tileMap;
    private Background background = new Background(0.1);
    private Berserker berserker;
    private Lich lich;
    private Player player;
    private EnemySpawner enemySpawner;
    private ArrayList<Enemy> enemies;
    private EnemyMovement enemyMovement;
    private Slugger slugger;
    private LevelOneBoss levelOneBoss;
    private int enemyNumber = 1;

    public Stage() {
        if (Objects.equals(CharState.character, "berserker")) {
            loadLevelOne();
        }
        if (Objects.equals(CharState.character, "lich")) {
            loadLevelTwo();
        }
    }

    @Override
    public void init() {

    }

    @Override
    public void update() {
        player.update();
        if (player.collision.characterMapPlacement.y > mapPitfall) {
            player.isDead();
            return;
        }
        tileMap.setPosition(GamePanel.WIDTH / 2 - player.collision.characterMapPlacement.getx(), GamePanel.HEIGHT / 2 - player.collision.characterMapPlacement.gety());
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            if (!player.character.flinching) {
                player.checkDamageTaken(enemy);
            }
            player.meleeAttack(enemy);
            enemyMovement = new EnemyMovement(player, enemy);
            enemyMovement.moveToHero();
            enemy.update();
            if (enemy.enemyStats.dead) {
                enemies.remove(i);
                enemy.enemyStats.dead = false;
                if (i == enemies.size()) {
                    bossBattleEndTeleport();
                }
            }
        }
        bossBattleStartTeleport();

    }

    public void bossBattleStartTeleport() {
        if (player.collision.characterMapPlacement.x >= 3060 && player.collision.characterMapPlacement.x <= 3100) {
            player.collision.characterMapPlacement.setPosition(3491, 150);
        }
    }

    public void bossBattleEndTeleport() {
        GamePanel.stateManager.setState(0);
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

    private void loadLevelOne() {
        mapPitfall = 220;
        maxX = 3200;
        maxY = 210;
        tileMap = new TileMap(30);
        tileMap.tileLoading.loadTiles("/tilesets/grasstileset.gif");
        tileMap.mapLoading.loadMap("/maps/levelOneV2.map");
        tileMap.setPosition(0, 0);
        background.getResource("/backgrounds/levelone.gif");
        berserker = new Berserker(tileMap);
        lich = new Lich(tileMap);
        slugger = new Slugger(tileMap);
        levelOneBoss = new LevelOneBoss(tileMap);
        player = new Player(tileMap, lich.spriteSheet, lich.character, lich.movement);
        player.collision.characterMapPlacement.setPosition(100, 200);
        enemies = new ArrayList<>();
        enemySpawner = new EnemySpawner();
        enemySpawner.spawnEnemies(enemyNumber, tileMap, slugger.spriteSheet, slugger.enemyStats, slugger.movement, enemies, player);
        enemySpawner.spawnBosses(3934, 195, tileMap, levelOneBoss.spriteSheet, levelOneBoss.enemyStats, levelOneBoss.movement, enemies);
    }

    private void loadLevelTwo() {
        mapPitfall = 410;
        maxX = 3200;
        maxY = 400;
        tileMap = new TileMap(30);
        tileMap.tileLoading.loadTiles("/tilesets/grasstileset.gif");
        tileMap.mapLoading.loadMap("/maps/levelTwo.map");
        tileMap.setPosition(0, 0);
        background.getResource("/backgrounds/levelone.gif");
        berserker = new Berserker(tileMap);
        lich = new Lich(tileMap);
        slugger = new Slugger(tileMap);
        player = new Player(tileMap, berserker.spriteSheet, berserker.character, berserker.movement);
        player.collision.characterMapPlacement.setPosition(600, 380);
        enemies = new ArrayList<>();
        enemySpawner = new EnemySpawner();
        enemySpawner.spawnEnemies(enemyNumber, tileMap, slugger.spriteSheet, slugger.enemyStats, slugger.movement, enemies, player);
    }
}
