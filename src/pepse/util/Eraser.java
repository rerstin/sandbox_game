package pepse.util;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Represents an object that deletes a world outside of window.
 */
public class Eraser extends GameObject {
    private final GameObjectCollection gameObjects;
    private final int groundLayer;
    private final int treeLayer;
    private final int leavesLayer;

    /**
     * Constructor
     * @param topLeftCorner top left corner of object
     * @param dimensions dimensions of object
     * @param renderable rendering of object
     * @param gameObjects game object collection
     * @param eraserLayer layer of object
     * @param groundLayer layer of ground
     * @param treeLayer layer of trees
     * @param leavesLayer layer of leaves
     */
    public Eraser(Vector2 topLeftCorner, Vector2 dimensions,
                  Renderable renderable, GameObjectCollection gameObjects, int eraserLayer,
                  int groundLayer, int treeLayer, int leavesLayer) {
        super(topLeftCorner, dimensions, renderable);
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.treeLayer = treeLayer;
        this.leavesLayer = leavesLayer;
        gameObjects.addGameObject(this, eraserLayer);
    }

    /**
     * Removes all objects (trees, leaves, ground blocks) that collide with eraser
     * @param other trees, leaves, ground blocks
     * @param collision collision type
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        gameObjects.removeGameObject(other, groundLayer);
        gameObjects.removeGameObject(other, leavesLayer);
        gameObjects.removeGameObject(other, treeLayer);
    }
}
