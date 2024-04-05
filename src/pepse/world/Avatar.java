package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.ImageRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

/**
 * This class implements an avatar that can move around the world.
 */
public class Avatar extends GameObject {
    private static final Vector2 AVATAR_DIMENSIONS = new Vector2(2 * Block.SIZE, 70);
    private static final String AVATAR = "avatar";
    private static final float MOVEMENT_SPEED = 50;
    private static final float JUMP_VELOCITY = 50;
    private static final float FLIGHT_VELOCITY = 6;
    private static final double ENERGY_FACTOR = 0.5;
    private static final float ACCELERATION = 500;
    private static final int IDLE_NUMBER = 16;
    private static final float DOWN_VELOCITY_FACTOR = 3;
    private static UserInputListener inputListener;
    private static final String IDLE_IMAGE = "src/Santapng/Idle (%d).png";
    private static final String JUMP_IMAGE = "src/Santapng/Jump (10).png";
    private static final String FLIGHT_IMAGE = "src/Santapng/Slide (11).png";
    private static final String WALK_IMAGE = "src/Santapng/Walk (%d).png";
    private static ImageRenderable jumpRenderable;
    private static ImageRenderable flightRenderable;
    private double flightEnergy = 100;
    private static int animationIdleCounter;
    private static int animationWalkCounter;
    private static final ImageRenderable[] idleState = new ImageRenderable[IDLE_NUMBER];
    private static final int WALK_NUMBER = 13;
    private static final ImageRenderable[] walkState = new ImageRenderable[WALK_NUMBER];

    /**
     * Constructor
     * @param topLeftCorner - The location of the top-left corner of the created avatar.
     * @param dimensions dimensions of object
     * @param renderable rendering of object
     */
    public Avatar(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);
    }

    /**
     *This function creates an avatar that can travel the world and is followed by the camera.
     *The can stand, walk, jump and fly, and never reaches the end of the world.
     * @param gameObjects - The collection of all participating game objects.
     * @param layer - The number of the layer to which the created avatar should be added.
     * @param topLeftCorner - The location of the top-left corner of the created avatar.
     * @param inputListener - Used for reading input from the user.
     * @param imageReader - Used for reading images from disk or from within a jar.
     */
    public static Avatar create(GameObjectCollection gameObjects,
                                int layer, Vector2 topLeftCorner,
                                UserInputListener inputListener,
                                ImageReader imageReader){
        initializeAvatar(inputListener, imageReader);
        Avatar avatar = new Avatar(topLeftCorner,
                AVATAR_DIMENSIONS, imageReader.readImage(String.format(IDLE_IMAGE, 1), true));
        avatar.setTag(AVATAR);
        gameObjects.addGameObject(avatar, layer);
        avatar.physics().preventIntersectionsFromDirection(Vector2.ZERO);
        avatar.transform().setAccelerationY(ACCELERATION);
        return avatar;
    }

    private static void initializeAvatar(UserInputListener inputListener, ImageReader imageReader) {
        Avatar.inputListener = inputListener;
        // create an array with idle states of avatar
        for (int i = 0; i < IDLE_NUMBER; i++) {
            idleState[i] = imageReader.readImage(String.format(IDLE_IMAGE, i+1), true);
        }
        // create an array with walk states of avatar
        for (int i = 0; i < WALK_NUMBER; i++) {
            walkState[i] = imageReader.readImage(String.format(WALK_IMAGE, i+1), true);
        }
        jumpRenderable = imageReader.readImage(JUMP_IMAGE, true);
        flightRenderable = imageReader.readImage(FLIGHT_IMAGE, true);
        // counters for switching states
        animationIdleCounter = 1;
        animationWalkCounter = 0;
    }

    /**
     * Switches idle state of avatar with array of states.
     */
    private void switchIdle(){
        renderer().setRenderable(idleState[animationIdleCounter]);
        // increment counter
        animationIdleCounter = (animationIdleCounter + 1) % IDLE_NUMBER;
    }

    /**
     * Switches walk state of avatar with array of states.
     */
    private void switchWalk(){
        renderer().setRenderable(walkState[animationWalkCounter]);
        // increment counter
        animationWalkCounter = (animationWalkCounter + 1) % WALK_NUMBER;
    }

    /**
     * Updates state of avatar and its velocity according to keyboard input
     * @param deltaTime time of updating
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        // set velocity down with acceleration
        Vector2 movementDir = Vector2.DOWN.mult(DOWN_VELOCITY_FACTOR);
        // case on ground
        setStateOnGround();
        // jump and flight
        movementDir = setJumpAndFlight(movementDir);
        // move to left or right
        movementDir = setLeftRightMovement(movementDir);
        setVelocity(movementDir.mult(MOVEMENT_SPEED));
    }

    /**
     * set Velocity of left and right directions
     * @param movementDir vector of velocity
     * @return updated vector of velocity
     */
    private Vector2 setLeftRightMovement(Vector2 movementDir) {
        if(inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            renderer().setIsFlippedHorizontally(true);
            movementDir = movementDir.add(Vector2.LEFT).mult(2);
        }
        if(inputListener.isKeyPressed(KeyEvent.VK_RIGHT))
        {
            renderer().setIsFlippedHorizontally(false);
            movementDir = movementDir.add(Vector2.RIGHT).mult(2);
        }
        return movementDir;
    }

    /**
     * Sets state of avatar when It is on the ground (idle or walk)
     */
    private void setStateOnGround() {
        if(getVelocity().y() == 0){
            if(getVelocity().x() != 0)
                switchWalk();
            else {
                switchIdle();
                // recover energy
                flightEnergy += ENERGY_FACTOR;
            }
        }
    }

    /**
     * Sets jump or flying velocity and switch rendering of avatar;
     * @param movementDir vector of velocity
     * @return updated vector of velocity
     */
    private Vector2 setJumpAndFlight(Vector2 movementDir) {
        if(inputListener.isKeyPressed(KeyEvent.VK_SPACE)){
            // flying
            if(inputListener.isKeyPressed(KeyEvent.VK_SHIFT) && flightEnergy > 0){
                movementDir = Vector2.UP.mult(FLIGHT_VELOCITY);
                renderer().setRenderable(flightRenderable);
                flightEnergy -= ENERGY_FACTOR;
            }
            // jumping
            else if(getVelocity().y() == 0f){
                movementDir = Vector2.UP.mult(JUMP_VELOCITY);
                renderer().setRenderable(jumpRenderable);
            }
            // set states counters to zero;
            animationIdleCounter = 0;
            animationWalkCounter = 0;
        }
        return movementDir;
    }
}
