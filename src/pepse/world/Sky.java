package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Represents a sky.
 */
public class Sky extends GameObject {
    private static final Color BASIC_SKY_COLOR = Color.decode("#80C6E5");

    /**
     * Constructor
     * @param topLeftCorner - The location of the top-left corner of the created avatar.
     * @param dimensions dimensions of object
     * @param renderable rendering of object
     */
    public Sky(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);
    }

    /**
     * This function creates a light blue rectangle which is always at the back of the window.
     * @param gameObjects - The collection of all participating game objects.
     * @param windowDimensions - The number of the layer to which the created game object should be added.
     * @param skyLayer - The number of the layer to which the created sky should be added.
     * @return A new game object representing the sky.
     */
    public static GameObject create(GameObjectCollection gameObjects, Vector2 windowDimensions, int skyLayer){
        GameObject sky = new GameObject(
                Vector2.ZERO, windowDimensions,
                new RectangleRenderable(BASIC_SKY_COLOR));
        sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(sky, skyLayer);
        return sky;
    }
}
