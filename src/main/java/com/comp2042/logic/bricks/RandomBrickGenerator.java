package com.comp2042.logic.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
/**
 * Generates random Tetris bricks.
 * <p>
 * Maintains a queue of upcoming bricks to support "next piece" previews.
 * </p>
 */

public class RandomBrickGenerator implements BrickGenerator {
    /** List of all available brick types. */
    private final List<Brick> brickList;
    /** Queue of upcoming bricks. */
    private final Deque<Brick> nextBricks = new ArrayDeque<>();
    /**
     * Constructs a random brick generator and initializes the queue
     * with two random bricks.
     */
    public RandomBrickGenerator() {
        brickList = new ArrayList<>();
        brickList.add(new IBrick());
        brickList.add(new JBrick());
        brickList.add(new LBrick());
        brickList.add(new OBrick());
        brickList.add(new SBrick());
        brickList.add(new TBrick());
        brickList.add(new ZBrick());
        nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
    }
    /**
     * Returns the next brick from the queue and ensures the queue
     * always contains at least one upcoming brick.
     *
     * @return the next brick to be used
     */
    @Override
    public Brick getBrick() {
        if (nextBricks.size() <= 1) {
            nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        }
        return nextBricks.poll();
    }
    /**
     * Selects a random brick from the available brick list.
     *
     * @return a randomly chosen brick
     */

    @Override
    public Brick getNextBrick() {
        return nextBricks.peek();
    }
}
