package scrublords.entities.characters;

import scrublords.core.CollisionDetection;
import scrublords.core.SpriteDimensions;
import scrublords.core.SpriteSheet;
import scrublords.core.Visualization;
import scrublords.entities.core.Actions;
import scrublords.entities.core.ActionsBuilder;
import scrublords.entities.core.Character;
import scrublords.entities.core.Movement;
import scrublords.tilemaps.TileMap;

import java.util.ArrayList;

/**
 * @author Denis Dimitrov <denis.k.dimitrov@gmail.com>.
 */
public class Berserker {
    public CollisionDetection collision;
    public SpriteDimensions spriteDimensions;
    public SpriteSheet spriteSheet;
    public Character character = new Character(20, 20, false, false, 0, false, 3, 31, new ArrayList<>(), new int[]{2, 8, 1, 2, 4, 2, 5});
    public Actions action = new ActionsBuilder().buildAnimations();
    public Movement movement;
    public Visualization visualization;

    public Berserker(TileMap tileMap) {
        collision = new CollisionDetection(tileMap);
        spriteSheet = new SpriteSheet("/sprites/player/berserker.gif", 7);
        movement = new Movement(0.9, 3.6, 0.4, 0.15, 4.0, -4.8, 0.3);
        spriteDimensions = new SpriteDimensions(30, 30);
        collision.cwidth = 20;
        collision.cheight = 20;
        spriteSheet.getCharacterSpriteSheet(character, spriteDimensions);
        visualization = new Visualization();
        visualization.setFrames(character.sprites.get(action.idle));
        visualization.setDelay(400);
    }
}