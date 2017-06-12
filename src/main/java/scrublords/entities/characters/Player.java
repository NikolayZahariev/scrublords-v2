package scrublords.entities.characters;

import scrublords.core.CollisionDetection;
import scrublords.core.SpriteDimensions;
import scrublords.core.SpriteSheet;
import scrublords.core.Visualization;
import scrublords.entities.core.*;
import scrublords.entities.core.Character;
import scrublords.entities.enemies.Enemy;
import scrublords.main.GamePanel;
import scrublords.tilemaps.TileMap;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author Denis Dimitrov <denis.k.dimitrov@gmail.com>.
 */
public class Player {
    public CollisionDetection collision;
    public MoveSet moveSet;
    private SpriteDimensions spriteDimensions;
    private Movement movement;
    private Visualization visualization;
    private boolean facingRight;
    private int currentAction;
    private Actions action = new ActionsBuilder().buildAnimations();
    private SpriteSheet spriteSheet;
    public Character character;

    public Player(TileMap tileMap, SpriteSheet spriteSheet, Character character, Movement movement) {
        this.spriteSheet = spriteSheet;
        this.character = character;
        this.movement = movement;
        collision = new CollisionDetection(tileMap);
        spriteDimensions = new SpriteDimensions(30, 30);
        collision.cwidth = 20;
        collision.cheight = 20;
        facingRight = true;
        spriteSheet.getCharacterSpriteSheet(character, spriteDimensions);
        visualization = new Visualization();
        currentAction = action.idle;
        visualization.setFrames(character.sprites.get(action.idle));
        visualization.setDelay(400);
        moveSet = new MoveSet(false, false, false, false, false);
    }

    public void checkDamageTaken(Enemy enemy) {
        if (collision.hitboxIntersection(enemy)) {
            onDamageTaken(enemy.getDamage());
        }
    }

    public void meleeAttack(Enemy enemy) {
        if (character.attacking) {
            if (facingRight) {
                attackRightEnemy(enemy);
            }
            attackLeftEnemy(enemy);
        }
    }

    public void update() {
        if (collision.characterMapPlacement.y > 220) {
            isDead();
            return;
        }
        getNextPosition();
        collision.checkTileMapCollision();
        collision.characterMapPlacement.setPosition(collision.xtemp, collision.ytemp);
        if (character.attacking) {
            if (currentAction != action.attacking) {
                currentAction = action.attacking;
                visualization.setFrames(character.sprites.get(action.attacking));
                visualization.setDelay(50);
                spriteDimensions.width = 60;
            }
        } else if (collision.dy > 0) {
            if (currentAction != action.falling) {
                currentAction = action.falling;
                visualization.setFrames(character.sprites.get(action.falling));
                visualization.setDelay(100);
                spriteDimensions.width = 30;
            }
        } else if (collision.dy < 0) {
            if (currentAction != action.jumping) {
                currentAction = action.jumping;
                visualization.setFrames(character.sprites.get(action.jumping));
                visualization.setDelay(-1);
                spriteDimensions.width = 30;
            }
        } else if (moveSet.left || moveSet.right) {
            if (currentAction != action.walking) {
                currentAction = action.walking;
                visualization.setFrames(character.sprites.get(action.walking));
                visualization.setDelay(40);
                spriteDimensions.width = 30;
            }
        } else {
            if (currentAction != action.idle) {
                currentAction = action.idle;
                visualization.setFrames(character.sprites.get(action.idle));
                visualization.setDelay(400);
                spriteDimensions.width = 30;
            }
        }
        visualization.update();
        if (currentAction != action.attacking && currentAction != action.fireball) {
            if (moveSet.right) {
                facingRight = true;
            }
            if (moveSet.left) {
                facingRight = false;
            }
        }
        if (character.flinching) {
            long elapsed = (System.nanoTime() - character.flinchTimer) / 1000000;
            if (elapsed > 1000) {
                character.flinching = false;
            }
        }
    }

    public void draw(Graphics g) {
        collision.characterMapPlacement.setMapPosition();
        if (character.flinching) {
            long elapsed = (System.nanoTime() - character.flinchTimer) / 1000000;
            if (elapsed / 100 % 2 == 0) {
                return;
            }
        }
        if (facingRight) {
            g.drawImage(visualization.getImage(), (int) (collision.characterMapPlacement.x + collision.characterMapPlacement.xmap - spriteDimensions.width / 2), (int) (collision.characterMapPlacement.y + collision.characterMapPlacement.ymap - spriteDimensions.height / 2), null);
        } else {
            g.drawImage(visualization.getImage(), (int) (collision.characterMapPlacement.x + collision.characterMapPlacement.xmap - spriteDimensions.width / 2 + spriteDimensions.width), (int) (collision.characterMapPlacement.y + collision.characterMapPlacement.ymap - spriteDimensions.height / 2), -spriteDimensions.width, spriteDimensions.height, null);
        }
    }

    private void onDamageTaken(int damageTaken) {
        if (character.flinching) {
            return;
        }
        character.health -= damageTaken;
        if (character.health < 0) {
            character.health = 0;
        }
        if (character.health == 0) {
            isDead();
        }
        character.flinching = true;
        character.flinchTimer = System.nanoTime();
    }

    public void isDead() {
        character.dead = true;
        GamePanel.stateManager.setState(0);
    }

    private void getNextPosition() {
        if (moveSet.left) {
            collision.dx -= movement.moveSpeed;
            if (collision.dx < -movement.maxSpeed) {
                collision.dx = -movement.maxSpeed;
            }
        } else if (moveSet.right) {
            collision.dx += movement.moveSpeed;
            if (collision.dx > movement.maxSpeed) {
                collision.dx = movement.maxSpeed;
            }
        } else {
            if (collision.dx > 0) {
                collision.dx -= movement.stopSpeed;
                if (collision.dx < 0) {
                    collision.dx = 0;
                }
            } else if (collision.dx < 0) {
                collision.dx += movement.stopSpeed;
                if (collision.dx > 0) {
                    collision.dx = 0;
                }
            }
        }
        if ((currentAction == action.attacking || currentAction == action.fireball) && !(moveSet.jumping || collision.falling)) {
            collision.dx = 0;
        }
        if (moveSet.jumping && !collision.falling) {
            collision.dy = movement.jumpStart;
            collision.falling = true;
        }
        if (collision.falling) {
            collision.dy += movement.fallSpeed;
            if (collision.dy > 0) {
                moveSet.jumping = false;
            }
            if (collision.dy < 0 && !moveSet.jumping) {
                collision.dy += movement.stopJumpSpeed;
            }
            if (collision.dy > movement.maxFallSpeed) {
                collision.dy = movement.maxFallSpeed;
            }
        }
    }

    private void attackRightEnemy(Enemy enemy) {
        if (enemy.collision.characterMapPlacement.getx() > collision.characterMapPlacement.x
                && enemy.collision.characterMapPlacement.getx() < collision.characterMapPlacement.x + character.attackRange
                && enemy.collision.characterMapPlacement.gety() > collision.characterMapPlacement.y - spriteDimensions.height / 2
                && enemy.collision.characterMapPlacement.gety() < collision.characterMapPlacement.y + spriteDimensions.height / 2) {
            enemy.onDamageTaken(character.attackDamage);
        }
    }

    private void attackLeftEnemy(Enemy enemy) {
        if (enemy.collision.characterMapPlacement.getx() < collision.characterMapPlacement.x
                && enemy.collision.characterMapPlacement.getx() > collision.characterMapPlacement.x - character.attackRange
                && enemy.collision.characterMapPlacement.gety() > collision.characterMapPlacement.y - spriteDimensions.height / 2
                && enemy.collision.characterMapPlacement.gety() < collision.characterMapPlacement.y + spriteDimensions.height / 2) {
            enemy.onDamageTaken(character.attackDamage);
        }
    }
}