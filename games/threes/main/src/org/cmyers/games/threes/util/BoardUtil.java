package org.cmyers.games.threes.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import org.apache.commons.lang3.tuple.Pair;
import org.cmyers.games.threes.state.GameState;

public class BoardUtil {

    // 6144 is the largest possible in stock game of "threes", I think.
    private static final ImmutableList<Integer> VALID_GAME_INTS = ImmutableList.of(0, 1, 2, 3, 6, 12, 24, 48, 96, 192, 384, 768, 1536, 3072, 6144, 12288, 24576, 49152, 98304);

    // gets an empty board of the given dimensions
    public static ArrayList<Integer> getEmptyBoard(int height, int width) {
        int dim = height * width;
        ArrayList<Integer> board = new ArrayList<>(dim);
        for(int i = 0; i < dim; i++) {
            board.add(0);
        }
        return board;
    }

    /**
     * Shift tiles towards the 0 index.
     */
    public static Pair<Boolean, ImmutableList<Integer>> oldshiftRow(ImmutableList<Integer> row) {
        boolean shifted = false;
        ArrayList<Integer> newRow = Lists.newArrayList(row);
        for(int i = 0, j = 0; i < row.size(); i++, j++) {
            // read from i, write to j
            if(shifted) {
                // already shifted, just keep writing
                newRow.set(j, row.get(i));
                continue;
            }
            // check for combine
            if(i < (row.size() - 1)) {
                // check for combine
                if(canCombine(row.get(i), row.get(i + 1))) {
                    shifted = true;
                    newRow.set(j, combine(row.get(i), row.get(i + 1)));
                    i++;
                    continue;
                }
            }
            newRow.set(j, row.get(i));
        }
        if(shifted) {
            newRow.set(row.size() - 1, 0);
        }
        return Pair.of(shifted, ImmutableList.copyOf(newRow));
    }

    /**
     * shifting in the direction TOWARDS from
     */
    public static boolean canCombine(int from, int to) {
        if(from == 0 && to == 0) {
            return false;
        }
        if(from == 0) {
            return true;
        }
        if(from + to == 3) {
            return true;
        }
        if(from == to && (from >= 3)) {
            return true;
        }
        return false;
    }

    public static int combine(int x, int y) {
        return x + y;
    }

    public static int scoreTile(int i) {
        if(!validGameInteger(i)) {
            throw new IllegalStateException(i + " is not a valid game integer");
        }

        if(i < 3) {
            return 0;
        }
        int score = 3;
        while(i != 3) {
            score = score * 3;
            i = i / 2;
        }
        return score;
    }

    public static boolean validGameInteger(int x) {
        return VALID_GAME_INTS.contains(x);
    }

    public static ImmutableList<Integer> shiftRow(ImmutableList<Integer> row) {
        if(row.size() < 2) {
            return row;
        }
        if(canCombine(row.get(0), row.get(1))) {
            return ImmutableList.copyOf(Iterables.concat(ImmutableList.of(combine(row.get(0), row.get(1))), row.subList(2, row.size()), ImmutableList.of(0)));
        }
        return ImmutableList.copyOf(Iterables.concat(ImmutableList.of(row.get(0)), shiftRow(row.subList(1, row.size()))));
    }

    /**
     * Converts an array of integers into an array of slices based on the move direction.
     * Say our board was this:
     *
     * 1 2 3
     * 4 5 6
     * 7 8 9
     *
     * If moving up, we'd get:
     *
     * [[1, 4, 7], [2, 5, 8], [3, 6, 9]]
     *
     * If moving down, we'd get:
     *
     * [[7, 4, 1], [8, 5, 2], [9, 6, 3]]
     *
     * If moving left, we'd get:
     *
     * [[1, 2, 3], [4, 5, 6], [7, 8, 9]]
     *
     * And finally, if moving right, we'd get:
     *
     * [[3, 2, 1], [6, 5, 4], [9, 8, 7]]
     *
     * The direction of motion is always towards the column that is indexed first.
     */
    public static ImmutableList<ImmutableList<Integer>> boardToSlices(GameState g, GameState.MoveDirection md) {
        ImmutableList<Integer> board = g.get(GameState.BOARD);
        int width = g.get(GameState.WIDTH);
        int height = g.get(GameState.HEIGHT);

        ImmutableList.Builder<ImmutableList<Integer>> pieces = ImmutableList.builder();
        switch(md) {
        case LEFT:
        case RIGHT:
            for(int i = 0; i < height; i++) {
                ImmutableList.Builder<Integer> b = ImmutableList.builder();
                for(int j = 0; j < width; j++) {
                    b.add(board.get(g.getCoord(j, i)));
                }
                pieces.add(b.build());
            }
            break;
        case UP:
        case DOWN:
            for(int i = 0; i < width; i++) {
                ImmutableList.Builder<Integer> b = ImmutableList.builder();
                for(int j = 0; j < height; j++) {
                    b.add(board.get(g.getCoord(i, j)));
                }
                pieces.add(b.build());
            }
            break;
        }
        ImmutableList<ImmutableList<Integer>> ret = pieces.build();
        if(md.equals(GameState.MoveDirection.RIGHT) || md.equals(GameState.MoveDirection.DOWN)) {
            ret = ImmutableList.copyOf(Iterables.transform(ret, (ImmutableList<Integer> l) -> l.reverse()));
        }
        return ret;
    }

    public static ArrayList<Integer> slicesToBoard(GameState g, ImmutableList<ImmutableList<Integer>> slices, GameState.MoveDirection md) {
        int width = g.get(GameState.WIDTH);
        int height = g.get(GameState.HEIGHT);

        // reverse the slices based upon the direction of movement (see boardToSlices() impl)
        ImmutableList<ImmutableList<Integer>> newSlices = slices;
        if(md.equals(GameState.MoveDirection.RIGHT) || md.equals(GameState.MoveDirection.DOWN)) {
            newSlices = ImmutableList.copyOf(Iterables.transform(newSlices, (ImmutableList<Integer> l) -> l.reverse()));
        }

        // now "undo" what boardToSlices() did
        ArrayList<Integer> board = getEmptyBoard(width, height);
        int index = 0;
        switch(md) {
        case LEFT:
        case RIGHT:
            for(int i = 0; i < height; i++) {
                for(int j = 0; j < width; j++) {
                    board.set(index++, newSlices.get(i).get(j));
                }
            }
            break;
        case UP:
        case DOWN:
            for(int i = 0; i < height; i++) {
                for(int j = 0; j < width; j++) {
                    board.set(index++, newSlices.get(j).get(i));
                }
            }
            break;
        }
        return board;
    }

}
