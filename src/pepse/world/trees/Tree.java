package pepse.world.trees;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;
import java.awt.*;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

/**
 * Responsible for the creation and management of trees and leaves.
 */
public class Tree {
    private static final Color BASE_TREE_COLOR = new Color(100, 50, 20);
    private static final int TREE_PROBABILITY = 9;
    private static final int LEAVES_PROBABILITY = 4;
    private final Function<Integer, Float> groundHeightAt;
    private final GameObjectCollection gameObjects;
    private final int layer;
    private final int seed;
    private static final int MIN_HEIGHT = 9;
    private static final int MAX_HEIGHT = 5;
    private final HashMap<Integer, Integer> Memory = new HashMap<>();
    private static final float PART_OF_HEIGHT = 2f / 3;

    /**
     * Constructor
     * @param groundHeightAt function to calculating height of ground
     * @param gameObjects gameObjectsCollection
     * @param layer layer of trees
     * @param seed seed for random crating
     */
    public Tree(Function<Integer, Float> groundHeightAt, GameObjectCollection gameObjects, int layer, int seed){
        this.groundHeightAt = groundHeightAt;
        this.gameObjects = gameObjects;
        this.layer = layer;
        this.seed = seed;
    }

    /**
     * Creates a tree in current X coordinate according to probability with random height and random leaves.
     * @param index X coordinate (will be rounded to a multiple of Block.SIZE)
     * @return tree height
     */
    private int get_tree_height(int index){
        // check in memory
        Random random = new Random(Objects.hash(index, seed));
        if(Memory.containsKey(index))
            return Memory.get(index);
        int thereIsTree = random.nextInt() % TREE_PROBABILITY;
        int tree_height = 0;
        if(thereIsTree == 0){
            tree_height = random.nextInt(MAX_HEIGHT) + MIN_HEIGHT;
            // safe a tree in Memory to recover It after
            Memory.put(index, tree_height);
            // two tree can not adjacent to each other
            Memory.put(index + Block.SIZE, 0);
            Memory.put(index - Block.SIZE, 0);
        }
        else
            // not tree
            Memory.put(index, 0);
        return tree_height;
    }

    /**
     * Check if there is a tree in given X coordinate.
     * @param x X coordinate
     * @return true if there is, false otherwise.
     */
    public boolean treeAt(int x){
        return Memory.get(x) != 0;
    }

    /**
     * Creates leaves of the given tree
     * @param topLeftCorner top left corner of the highest tree block
     * @param tree_height height of tree
     */
    private void addLeaves(Vector2 topLeftCorner, int tree_height){
        // create new random generator according to the given seed
        Random leafRand = new Random(Objects.hash(topLeftCorner.x(), seed));
        //calculate number leves in row and col
        int leavesInRow = (int)(tree_height * PART_OF_HEIGHT);
        if(leavesInRow % 2 == 0) leavesInRow ++;
        int leavesInCol = leavesInRow + 2;
        int startX = (int)topLeftCorner.x() - (leavesInRow / 2) * Block.SIZE;
        int startY = (int)topLeftCorner.y() - (leavesInCol / 2) * Block.SIZE;
        for (int i = 0; i < leavesInRow; i++) {
            for (int j = 0; j < leavesInCol; j++) {
                // probability of creating leaves is 0.75
                int isLeaf = leafRand.nextInt() % LEAVES_PROBABILITY;
                if (isLeaf != 0){
                    new Leaf(new Vector2(startX + i * Block.SIZE,
                            startY + j * Block.SIZE), gameObjects, layer + 1);
                }
            }
        }
    }

    /**
     * This method creates trees in a given range of x-values.
     * @param minX - The lower bound of the given range (will be rounded to a multiple of Block.SIZE).
     * @param maxX - The upper bound of the given range (will be rounded to a multiple of Block.SIZE).
     */
    public void createInRange(int minX, int maxX) {
        for(int i = minX; i < maxX; i += Block.SIZE) {
            int tree_height = get_tree_height(i);
            // get the ground height
            float terrain_height = groundHeightAt.apply(i);
            for (int j = 0; j < tree_height; j++) {
                gameObjects.addGameObject(new Block(new Vector2(i, - (j + 1) * Block.SIZE + terrain_height),
                        new RectangleRenderable(ColorSupplier.approximateColor(BASE_TREE_COLOR))), layer);
            }
            if(tree_height != 0)
                addLeaves(new Vector2(i, - tree_height  * Block.SIZE + terrain_height), tree_height);
        }
    }
}
