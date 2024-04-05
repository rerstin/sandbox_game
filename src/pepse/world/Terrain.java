package pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.PerlinNoise;
import java.awt.*;

/**
 * Responsible for the creation and management of terrain.
 */
public class Terrain {
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int HALF_OF_WINDOW = 2;
    private final GameObjectCollection gameObjects;
    private final int groundLayer;
    private final int groundHeightAtX0;
    public int seed;
    private final PerlinNoise perlinNoise;
    private static final int TERRAIN_DEPTH = 20;

    /**
     * Constructor
     * @param gameObjects - The collection of all participating game objects.
     * @param groundLayer - The number of the layer to which the created ground objects should be added.
     * @param windowDimensions - The dimensions of the windows.
     * @param seed - A seed for a random number generator.
     */
    public Terrain(GameObjectCollection gameObjects,
                   int groundLayer, Vector2 windowDimensions,
                   int seed) {
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.seed = seed;
        this.perlinNoise = new PerlinNoise(seed);
        this.groundHeightAtX0 = (int)(windowDimensions.y() / HALF_OF_WINDOW);
    }

    /**
     * This method returns the ground height at a given location.
     * @param x X coordinate of location
     * @return The ground height at the given location.
     */
    public float groundHeightAt(float x){
        return groundHeightAtX0 + perlinNoise.noise(x);
    }

    /**
     * This method creates terrain in a given range of x-values.
     * @param minX - The lower bound of the given range (will be rounded to a multiple of Block.SIZE).
     * @param maxX - The upper bound of the given range (will be rounded to a multiple of Block.SIZE).
     */
    public void createInRange(int minX, int maxX){
        for(int i = minX; i < maxX; i += Block.SIZE){
            for (int j = 0; j < Block.SIZE * TERRAIN_DEPTH; j += Block.SIZE) {
                gameObjects.addGameObject(new Block(new Vector2(i, j + (int)groundHeightAt(i)),
                        new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR))), groundLayer);
            }
        }
    }
}
