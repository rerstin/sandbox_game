package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.world.Block;
import java.awt.*;
import java.util.Random;

/**
 * Represents a single leaf.
 */
public class Leaf extends Block{
    private static final Random random = new Random();
    private static final Color LEAF_COLOR = new Color(50 , 200, 30);
    private static final float INITIAL_DEGREE = -5;
    private static final float FINAL_DEGREE = 5;
    private static final int MAX_CYCLE = 3;
    private static final float INITIAL_SIZE = Block.SIZE * 0.99f;
    private static final float FINAL_SIZE = Block.SIZE * 1.01f;
    private static final float FADEOUT_TIME = 3;
    private static final float FADE_OUT_WAIT_TIME = 0;
    private static final float FALLING_VELOCITY = 30;
    private static final float FADEIN_TIME = 3;
    private Transition<Float> leafMovement;
    private Transition<Float> leafSize;
    private static final int MAX_WAIT_TIME = 300;
    private final Vector2 topLeftCorner;

    /**
     * Constructor
     * @param topLeftCorner top left corner of object
     * @param gameObjects game objects collection
     * @param layer layer of leaves
     */
    public Leaf(Vector2 topLeftCorner, GameObjectCollection gameObjects, int layer) {
        super(topLeftCorner, new RectangleRenderable(LEAF_COLOR));
        gameObjects.addGameObject(this, layer);
        this.topLeftCorner = topLeftCorner;
        // create movement under wind
        addTransitions();
    }

    /**
     * Adds transitions for movement of leaves and falling
     */
    private void addTransitions() {
        leafMovement = new Transition<>(this, this.renderer()::setRenderableAngle,
                INITIAL_DEGREE, FINAL_DEGREE,
                Transition.LINEAR_INTERPOLATOR_FLOAT, random.nextInt(MAX_CYCLE) + 1,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
        leafSize = new Transition<>(this, num -> this.setDimensions(new Vector2(num, num)),
                INITIAL_SIZE, FINAL_SIZE,
                Transition.LINEAR_INTERPOLATOR_FLOAT, random.nextInt(MAX_CYCLE) + 1,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
        // leaves falling task
        new ScheduledTask (this, random.nextInt(MAX_WAIT_TIME), false,
                () -> this.setVelocity(Vector2.DOWN.mult(FALLING_VELOCITY)));
    }


    /**
     * Controls collision
     * @param other other object
     * @param collision type of collision
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        new ScheduledTask(this, FADE_OUT_WAIT_TIME,
                false, ()-> this.renderer().fadeOut(FADEOUT_TIME, ()-> {
                    this.setTopLeftCorner(topLeftCorner);
                    this.renderer().fadeIn(FADEIN_TIME, this::addTransitions);}));
        this.removeComponent(leafMovement);
        this.removeComponent(leafSize);
        this.setVelocity(Vector2.ZERO);
    }
}
