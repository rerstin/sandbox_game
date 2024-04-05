package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import java.awt.*;

/**
 * Represents the halo of sun.
 */
public class SunHalo{
    private static final String SUN_HALO_TAG = "sunHalo";
    private static final int GROWTH_FACTOR = 2;

    /**
     * This function creates a halo around a given object that represents the sun.
     * The halo will be tied to the given sun, and will always move with it.
     * @param gameObjects - The collection of all participating game objects.
     * @param layer - The number of the layer to which the created halo should be added.
     * @param sun - A game object representing the sun (it will be followed by the created game object).
     * @param color - The color of the halo.
     * @return A new game object representing the sun's halo.
     */
    public static GameObject create(
            GameObjectCollection gameObjects,
            int layer,
            GameObject sun,
            Color color){
        GameObject sunHalo = new GameObject(Vector2.ZERO, new Vector2(Sun.SUN_RADIUS,
                Sun.SUN_RADIUS).mult(GROWTH_FACTOR), new OvalRenderable(color));
        sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sunHalo.addComponent(deltaTime -> sunHalo.setCenter(sun.getCenter()));
        sunHalo.setTag(SUN_HALO_TAG);
        gameObjects.addGameObject(sunHalo, layer);
        return sunHalo;
    }
}
