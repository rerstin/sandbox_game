package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.Eraser;
import pepse.world.Avatar;
import pepse.world.Block;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Tree;
import java.awt.*;
import java.util.Random;

/**
 * The main class of the simulator.
 */
public class PepseGameManager extends GameManager {
    private static final float CYCLE_LENGTH = 30;
    private static final int SUN_LAYER = Layer.BACKGROUND + 1;
    private static final int SUN_HALO_LAYER = SUN_LAYER + 1;
    private static final int GROUND_LAYER = Layer.STATIC_OBJECTS - 1;
    private static final int LEAVES_LAYER = Layer.STATIC_OBJECTS + 1;
    private static final int AVATAR_LAYER = Layer.STATIC_OBJECTS + 2;
    private static final int ERASER_LAYER = Layer.STATIC_OBJECTS + 3;
    private static final int FRAME_RATE = 70;
    private static final int WORLD_OUTSIDE_OF_WINDOW = 4;
    private static final int CREATING_FACTOR = 6;
    private static final String TITLE = "PEPSE";
    private static final Vector2 WINDOW_DIMENSIONS = new Vector2(1000, 700);
    private int minX;
    private int maxX;
    private static final int TREE_LAYER = Layer.STATIC_OBJECTS;
    private Vector2 windowDimensions;
    private static final Color sunHaloColor = new Color(255, 255, 0, 20);
    private Tree tree;
    private Terrain terrain;
    private GameObject avatar;
    private Eraser leftEraser;
    private Eraser rightEraser;

    /**
     * Constructor
     * @param title title of window
     * @param dimesions dimensions of window
     */
    public PepseGameManager(String title, Vector2 dimesions) {
        super(title, dimesions);
    }

    /**
     * The method will be called once when a GameGUIComponent is created,
     * and again after every invocation of windowController.resetGame().
     * @param imageReader - Contains a single method: readImage, which reads an image from disk.
     *                    See its documentation for help.
     * @param soundReader - Contains a single method: readSound, which reads a wav file from disk.
     *                    See its documentation for help.
     * @param inputListener - Contains a single method: isKeyPressed, which returns whether a
     *                      given key is currently pressed by the user or not. See its documentation.
     * @param windowController - Contains an array of helpful, self explanatory methods concerning the window.
     */
    @Override
    public void initializeGame(ImageReader imageReader,
                               SoundReader soundReader,
                               UserInputListener inputListener,
                               WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        windowDimensions = windowController.getWindowDimensions();
        // set borders and random seed
        Random random = new Random();
        int seed = random.nextInt();
        minX = - WORLD_OUTSIDE_OF_WINDOW * Block.SIZE;
        maxX = (int)windowDimensions.x() + WORLD_OUTSIDE_OF_WINDOW * Block.SIZE;
        maxX -= maxX % Block.SIZE;
        //create terrain
        terrain = new Terrain(gameObjects(), GROUND_LAYER, windowDimensions, seed);
        terrain.createInRange(minX, maxX);

        // create sky, night and sun with sun halo
        createBackground();

        //create trees
        createTrees(windowController, seed);

        // add avatar
        createAvatar(imageReader, inputListener, windowController);

        // create eraser
        createEraser();
    }

    /**
     * Creates Erasers that deletes a world outside of window
     */
    private void createEraser() {
        leftEraser = new Eraser(new Vector2(-windowDimensions.x(), 0),
                new Vector2(Block.SIZE, 2 * windowDimensions.y()), new RectangleRenderable(Color.GRAY), gameObjects(),
                ERASER_LAYER, GROUND_LAYER, TREE_LAYER, LEAVES_LAYER);
        rightEraser = new Eraser(new Vector2(2 * windowDimensions.x(), 0),
                new Vector2(Block.SIZE, 2 * windowDimensions.y()), new RectangleRenderable(Color.GRAY), gameObjects(),
                ERASER_LAYER, GROUND_LAYER, TREE_LAYER, LEAVES_LAYER);
        // for removing objects in different layers
        gameObjects().layers().shouldLayersCollide(ERASER_LAYER, GROUND_LAYER, true);
        gameObjects().layers().shouldLayersCollide(ERASER_LAYER, LEAVES_LAYER, true);
        gameObjects().layers().shouldLayersCollide(ERASER_LAYER, TREE_LAYER, true);
    }

    /**
     * Creates an Avatar and puts it in center of window
     * @param imageReader - Contains a single method: readImage, which reads an image from disk.
     *                    See its documentation for help.
     * @param inputListener - Contains a single method: isKeyPressed, which returns whether a
     *                      given key is currently pressed by the user or not. See its documentation.
     * @param windowController - Contains an array of helpful, self explanatory methods concerning the window.
     */
    private void createAvatar(ImageReader imageReader, UserInputListener inputListener, WindowController windowController) {
        avatar = Avatar.create(gameObjects(), AVATAR_LAYER, Vector2.ZERO, inputListener, imageReader);
        gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, GROUND_LAYER, true);
        gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, TREE_LAYER, true);
        // puts avatar without colliding with trees
        int characterX = (int)windowDimensions.x() / 2;
        characterX = characterX - (characterX % Block.SIZE);
        while (tree.treeAt(characterX) || tree.treeAt(((characterX + Block.SIZE))))
            characterX += Block.SIZE;
        avatar.setCenter(new Vector2(characterX + Block.SIZE, terrain.groundHeightAt(characterX) -
                avatar.getDimensions().y() / 2 - Block.SIZE));
        setCamera(
                new Camera(
                        avatar,            //object to follow
                        Vector2.ZERO,    //follow the center of the object
                        windowController.getWindowDimensions(),  //widen the frame a bit
                        windowController.getWindowDimensions()   //share the window dimensions
                ));
    }

    /**
     * Creates a sky, night, sun with sun halo
     */
    private void createBackground() {
        // create sky
        Sky.create(gameObjects(), windowDimensions, Layer.BACKGROUND);
        //create night
        Night.create(gameObjects(), Layer.FOREGROUND,
                windowDimensions, CYCLE_LENGTH);

        //create sun (with halo)
        GameObject sun = Sun.create(gameObjects(), SUN_LAYER, windowDimensions, CYCLE_LENGTH);
        SunHalo.create(gameObjects(), SUN_HALO_LAYER, sun, sunHaloColor);
    }

    /**
     * Create trees
     * @param windowController - Contains an array of helpful, self explanatory methods concerning the window.
     * @param seed seed for random creating of trees
     */
    private void createTrees(WindowController windowController, int seed) {
        tree = new Tree(terrain::groundHeightAt, gameObjects(), TREE_LAYER, seed);
        tree.createInRange(minX, maxX);
        windowController.setTargetFramerate(FRAME_RATE);
        gameObjects().layers().shouldLayersCollide(LEAVES_LAYER, GROUND_LAYER, true);
    }

    /**
     * updates a simulator
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        // calculate new borders of world
        float coordinateLeftX = avatar.getCenter().x() - windowDimensions.x();
        // set erasers to correct positions according to avatar
        leftEraser.setCenter(new Vector2(coordinateLeftX, terrain.groundHeightAt(coordinateLeftX)));
        float coordinateRightX = avatar.getCenter().x() + windowDimensions.x();
        rightEraser.setCenter(new Vector2(coordinateRightX, terrain.groundHeightAt(coordinateRightX)));
        int newMinX = (int)( coordinateLeftX - coordinateLeftX % Block.SIZE + CREATING_FACTOR * Block.SIZE);
        int newMaxX = (int)(coordinateRightX - coordinateRightX % Block.SIZE - CREATING_FACTOR * Block.SIZE);
        // create world according to movement of avatar
        if(minX > newMinX){
            terrain.createInRange(newMinX, minX);
            tree.createInRange(newMinX, minX);
        }
        if(maxX < newMaxX){
            terrain.createInRange(maxX, newMaxX);
            tree.createInRange(maxX, newMaxX);
        }
        minX = newMinX;
        maxX = newMaxX;
    }

    public static void main(String[] args) {
        new PepseGameManager(TITLE, WINDOW_DIMENSIONS).run();
    }
}
