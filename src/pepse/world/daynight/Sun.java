package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Represents the sun - moves across the sky in an elliptical path.
 */
public class Sun {
    private static final String SUN_TAG = "sun";
    private static final float INITIAL_DEGREE = 0;
    public static final int SUN_RADIUS = 100;
    private static final float FINAL_DEGREE = 360;
    private static final float HALF = 0.5f;
    private static final float ELLIPSE_INDEX = 4f / 5;
    private static final float DEGREE_FACTOR = 1.91f;


    /**
     * This function creates a yellow circle that moves in the sky in an elliptical path (in camera coordinates).
     * @param gameObjects - The collection of all participating game objects.
     * @param layer - The number of the layer to which the created sun should be added.
     * @param windowDimensions - The dimensions of the windows.
     * @param cycleLength - The amount of seconds it should take the created game object to complete a full cycle.
     * @return A new game object representing the sun.
     */
    public static GameObject create(
            GameObjectCollection gameObjects,
            int layer,
            Vector2 windowDimensions,
            float cycleLength){
        GameObject sun = new GameObject(Vector2.ZERO, new Vector2(SUN_RADIUS , SUN_RADIUS), new OvalRenderable(Color.YELLOW));
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag(SUN_TAG);
        gameObjects.addGameObject(sun, layer);
        // set transition for movement of sun
        Consumer<Float> consumer = degree -> {sun.setCenter(calcSunPosition(windowDimensions,
                degree / cycleLength / DEGREE_FACTOR));};
        new Transition<Float>(sun, consumer, INITIAL_DEGREE, FINAL_DEGREE,
                Transition.LINEAR_INTERPOLATOR_FLOAT, cycleLength,
                Transition.TransitionType.TRANSITION_LOOP, null);
        return sun;
    }

    /**
     * Calculates a sun position according to angle
     * @param windowDimensions dimensions of window
     * @param angleInSky given angle
     * @return position of sun
     */
    private static Vector2 calcSunPosition(Vector2 windowDimensions,
                                           float angleInSky){
        float sunOrbite = windowDimensions.y() * HALF;
        return new Vector2(windowDimensions.x() * HALF - sunOrbite * (float) Math.sin(angleInSky),
                windowDimensions.x() * HALF - (float) Math.cos(angleInSky) * sunOrbite/ ELLIPSE_INDEX);
    }
}
