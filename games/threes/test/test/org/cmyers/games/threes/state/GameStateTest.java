package org.cmyers.games.threes.state;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.cmyers.games.threes.random.ImmutablePrng;
import org.cmyers.games.threes.util.BoardUtil;
import org.junit.Assert;
import org.junit.Test;

public class GameStateTest {

    private static final ImmutableList<Integer> BOARD_CAN_ONLY_MOVE_LEFT = ImmutableList.of(0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1);
    private static final ImmutableList<Integer> BOARD_CAN_ONLY_MOVE_RIGHT = ImmutableList.of(1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0);
    private static final ImmutableList<Integer> BOARD_CAN_ONLY_MOVE_UP = ImmutableList.of(0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
    private static final ImmutableList<Integer> BOARD_CAN_ONLY_MOVE_DOWN = ImmutableList.of(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0);

    private static final ImmutableList<Integer> BOARD_WITH_INDEXES = ImmutableList.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);
    private static final ImmutableList<Integer> NON_SQUARE_BOARD = ImmutableList.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);

    private static final ImmutablePrng SEEDED_RANDOM = ImmutablePrng.of(1); // chosen by fair dice roll

    @Test
    public void testGameStateConstruction() {
        GameState gs1 = GameState.TYPE.builder().emptyBoard().emptyNext().random().build();
        // assert board is correct size
        Assert.assertEquals(gs1.get(GameState.WIDTH) * gs1.get(GameState.HEIGHT), gs1.get(GameState.BOARD).size());
        // assert all spaces are empty
        Assert.assertEquals(gs1.get(GameState.BOARD).size(), Iterables.frequency(gs1.get(GameState.BOARD), 0));
        // assert next list is empty
        Assert.assertTrue(gs1.get(GameState.NEXT).isEmpty());
    }

    @Test
    public void testGameStateInitializeGame() {
        GameState gs1 = GameState.TYPE.builder().emptyBoard().emptyNext().random().build();
        gs1 = gs1.init();
        // assert all except 9 spaces
        int size = gs1.get(GameState.BOARD).size();
        Assert.assertEquals(size - 9, Iterables.frequency(gs1.get(GameState.BOARD), 0));
        // check next buffer
        int nextsize = gs1.get(GameState.NEXT).size();
        // initial next is 12 or 13, we took 9, should be 3 or 4
        Assert.assertTrue(nextsize <= 4);
        Assert.assertTrue(nextsize >= 3);
    }

    @Test
    public void testCanMoveLeft() {
        GameState left = GameState.TYPE.builder().emptyNext().set(GameState.BOARD, BOARD_CAN_ONLY_MOVE_LEFT).set(GameState.RANDOM, SEEDED_RANDOM).build().setNextNext();
        Assert.assertTrue(left.canMove(GameState.MoveDirection.LEFT));
        Assert.assertFalse(left.canMove(GameState.MoveDirection.UP));
        Assert.assertFalse(left.canMove(GameState.MoveDirection.DOWN));
        Assert.assertFalse(left.canMove(GameState.MoveDirection.RIGHT));
    }

    @Test
    public void testCanMoveRight() {
        GameState right = GameState.TYPE.builder().emptyNext().set(GameState.BOARD, BOARD_CAN_ONLY_MOVE_RIGHT).set(GameState.RANDOM, SEEDED_RANDOM).build().setNextNext();
        Assert.assertFalse(right.canMove(GameState.MoveDirection.LEFT));
        Assert.assertFalse(right.canMove(GameState.MoveDirection.UP));
        Assert.assertFalse(right.canMove(GameState.MoveDirection.DOWN));
        Assert.assertTrue(right.canMove(GameState.MoveDirection.RIGHT));
    }

    @Test
    public void testCanMoveUp() {
        GameState up = GameState.TYPE.builder().emptyNext().set(GameState.BOARD, BOARD_CAN_ONLY_MOVE_UP).set(GameState.RANDOM, SEEDED_RANDOM).build().setNextNext();
        Assert.assertFalse(up.canMove(GameState.MoveDirection.LEFT));
        Assert.assertTrue(up.canMove(GameState.MoveDirection.UP));
        Assert.assertFalse(up.canMove(GameState.MoveDirection.DOWN));
        Assert.assertFalse(up.canMove(GameState.MoveDirection.RIGHT));
    }

    @Test
    public void testCanMoveDown() {
        GameState down = GameState.TYPE.builder().emptyNext().set(GameState.BOARD, BOARD_CAN_ONLY_MOVE_DOWN).set(GameState.RANDOM, SEEDED_RANDOM).build().setNextNext();
        Assert.assertFalse(down.canMove(GameState.MoveDirection.LEFT));
        Assert.assertFalse(down.canMove(GameState.MoveDirection.UP));
        Assert.assertTrue(down.canMove(GameState.MoveDirection.DOWN));
        Assert.assertFalse(down.canMove(GameState.MoveDirection.RIGHT));
    }

    private static Integer box(int i) {
        return Integer.valueOf(i);
    }

    @Test
    public void testBoardToSlicesLeft() {
        GameState g = GameState.TYPE.builder().emptyNext().set(GameState.BOARD, BOARD_WITH_INDEXES).set(GameState.RANDOM, SEEDED_RANDOM).build().setNextNext();
        ImmutableList<ImmutableList<Integer>> slices = BoardUtil.boardToSlices(g, GameState.MoveDirection.LEFT);
        /*
         * 1, 2, 3, 4
         * 5, 6, 7, 8,
         * 9, 10, 11, 12,
         * 13, 14, 15, 16
         */
        Assert.assertEquals(box(1), slices.get(0).get(0));
        Assert.assertEquals(box(5), slices.get(1).get(0));
        Assert.assertEquals(box(9), slices.get(2).get(0));
        Assert.assertEquals(box(13), slices.get(3).get(0));
        Assert.assertEquals(box(4), slices.get(0).get(3));
        Assert.assertEquals(box(16), slices.get(3).get(3));
    }

    @Test
    public void testBoardToSlicesRight() {
        GameState g = GameState.TYPE.builder().emptyNext().set(GameState.BOARD, BOARD_WITH_INDEXES).set(GameState.RANDOM, SEEDED_RANDOM).build().setNextNext();
        ImmutableList<ImmutableList<Integer>> slices = BoardUtil.boardToSlices(g, GameState.MoveDirection.RIGHT);
        /*
         * 1, 2, 3, 4
         * 5, 6, 7, 8,
         * 9, 10, 11, 12,
         * 13, 14, 15, 16
         */
        Assert.assertEquals(box(4), slices.get(0).get(0));
        Assert.assertEquals(box(8), slices.get(1).get(0));
        Assert.assertEquals(box(12), slices.get(2).get(0));
        Assert.assertEquals(box(16), slices.get(3).get(0));
        Assert.assertEquals(box(1), slices.get(0).get(3));
        Assert.assertEquals(box(13), slices.get(3).get(3));
    }

    @Test
    public void testBoardToSlicesDown() {
        GameState g = GameState.TYPE.builder().emptyNext().set(GameState.BOARD, BOARD_WITH_INDEXES).set(GameState.RANDOM, SEEDED_RANDOM).build().setNextNext();
        ImmutableList<ImmutableList<Integer>> slices = BoardUtil.boardToSlices(g, GameState.MoveDirection.DOWN);
        /*
         * 1, 2, 3, 4
         * 5, 6, 7, 8,
         * 9, 10, 11, 12,
         * 13, 14, 15, 16
         */
        Assert.assertEquals(box(13), slices.get(0).get(0));
        Assert.assertEquals(box(14), slices.get(1).get(0));
        Assert.assertEquals(box(15), slices.get(2).get(0));
        Assert.assertEquals(box(16), slices.get(3).get(0));
        Assert.assertEquals(box(1), slices.get(0).get(3));
        Assert.assertEquals(box(4), slices.get(3).get(3));
    }

    @Test
    public void testBoardToSlicesUp() {
        GameState g = GameState.TYPE.builder().emptyNext().set(GameState.BOARD, BOARD_WITH_INDEXES).set(GameState.RANDOM, SEEDED_RANDOM).build().setNextNext();
        ImmutableList<ImmutableList<Integer>> slices = BoardUtil.boardToSlices(g, GameState.MoveDirection.UP);
        /*
         * 1, 2, 3, 4
         * 5, 6, 7, 8,
         * 9, 10, 11, 12,
         * 13, 14, 15, 16
         */
        Assert.assertEquals(box(1), slices.get(0).get(0));
        Assert.assertEquals(box(2), slices.get(1).get(0));
        Assert.assertEquals(box(3), slices.get(2).get(0));
        Assert.assertEquals(box(4), slices.get(3).get(0));
        Assert.assertEquals(box(13), slices.get(0).get(3));
        Assert.assertEquals(box(16), slices.get(3).get(3));
    }

    @Test
    public void testBoardToSlicesWithNonSquareBoardUp() {
        GameState g = GameState.TYPE.builder().emptyNext().set(GameState.HEIGHT, 3).set(GameState.BOARD, NON_SQUARE_BOARD).set(GameState.RANDOM, SEEDED_RANDOM).build().setNextNext();
        ImmutableList<ImmutableList<Integer>> slices = BoardUtil.boardToSlices(g, GameState.MoveDirection.UP);
        /*
         * 1, 2, 3, 4
         * 5, 6, 7, 8,
         * 9, 10, 11, 12,
         */
        Assert.assertEquals(box(1), slices.get(0).get(0));
        Assert.assertEquals(box(2), slices.get(1).get(0));
        Assert.assertEquals(box(3), slices.get(2).get(0));
        Assert.assertEquals(box(4), slices.get(3).get(0));
        Assert.assertEquals(box(9), slices.get(0).get(2));
        Assert.assertEquals(box(12), slices.get(3).get(2));
    }

    @Test
    public void testBoardToSlicesWithNonSquareBoardRight() {
        GameState g = GameState.TYPE.builder().emptyNext().set(GameState.HEIGHT, 3).set(GameState.BOARD, NON_SQUARE_BOARD).set(GameState.RANDOM, SEEDED_RANDOM).build().setNextNext();
        ImmutableList<ImmutableList<Integer>> slices = BoardUtil.boardToSlices(g, GameState.MoveDirection.RIGHT);
        /*
         * 1, 2, 3, 4
         * 5, 6, 7, 8,
         * 9, 10, 11, 12,
         */
        Assert.assertEquals(box(4), slices.get(0).get(0));
        Assert.assertEquals(box(8), slices.get(1).get(0));
        Assert.assertEquals(box(12), slices.get(2).get(0));
        Assert.assertEquals(box(1), slices.get(0).get(3));
        Assert.assertEquals(box(9), slices.get(2).get(3));
    }

    @Test
    public void testSlicesToBoard() {
        for(GameState.MoveDirection md : GameState.MoveDirection.values()) {
            GameState g = GameState.TYPE.builder().emptyNext().set(GameState.BOARD, BOARD_WITH_INDEXES).set(GameState.RANDOM, SEEDED_RANDOM).build().setNextNext();
            ImmutableList<ImmutableList<Integer>> slices = BoardUtil.boardToSlices(g, md);
            GameState g2 = g.set(GameState.BOARD, ImmutableList.copyOf(BoardUtil.slicesToBoard(g, slices, md)));
            Assert.assertEquals(g.get(GameState.BOARD), g2.get(GameState.BOARD));
        }
    }

    @Test
    public void testSlicesToBoardNonSquare() {
        for(GameState.MoveDirection md : GameState.MoveDirection.values()) {
            GameState g = GameState.TYPE.builder().emptyNext().set(GameState.HEIGHT, 3).set(GameState.BOARD, NON_SQUARE_BOARD).set(GameState.RANDOM, SEEDED_RANDOM).build().setNextNext();
            ImmutableList<ImmutableList<Integer>> slices = BoardUtil.boardToSlices(g, md);
            GameState g2 = g.set(GameState.BOARD, ImmutableList.copyOf(BoardUtil.slicesToBoard(g, slices, md)));
            Assert.assertEquals(g.get(GameState.BOARD), g2.get(GameState.BOARD));
        }
    }
}
