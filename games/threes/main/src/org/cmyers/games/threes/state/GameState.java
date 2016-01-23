package org.cmyers.games.threes.state;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import misc1.commons.ds.ImmutableSalvagingMap;
import misc1.commons.ds.SimpleStructKey;
import misc1.commons.ds.Struct;
import misc1.commons.ds.StructBuilder;
import misc1.commons.ds.StructKey;
import misc1.commons.ds.StructType;
import org.apache.commons.lang3.tuple.Pair;
import org.cmyers.games.threes.random.ImmutablePrng;

public class GameState extends Struct<GameState, GameState.Builder> {
    // 6144 is the largest possible in stock game of "threes", I think.
    private static final ImmutableList<Integer> VALID_GAME_INTS = ImmutableList.of(0, 1, 2, 3, 6, 12, 24, 48, 96, 192, 384, 768, 1536, 3072, 6144, 12288, 24576, 49152, 98304);

    private GameState(ImmutableMap<StructKey<GameState, ?, ?>, Object> map) {
        super(TYPE, map);
    }

    public static class Builder extends StructBuilder<GameState, Builder> {
        private Builder(ImmutableSalvagingMap<StructKey<GameState, ?, ?>, Object> map) {
            super(TYPE, map);
        }

        public Builder emptyNext() {
            return set(NEXT, ImmutableList.of());
        }

        public Builder emptyBoard() {
            return set(BOARD, ImmutableList.copyOf(getEmptyBoard(get(HEIGHT), get(WIDTH))));
        }

    }

    public static final SimpleStructKey<GameState, Integer> WIDTH;
    public static final SimpleStructKey<GameState, Integer> HEIGHT;
    public static final SimpleStructKey<GameState, ImmutablePrng> RANDOM;
    public static final SimpleStructKey<GameState, ImmutableList<Integer>> BOARD;
    public static final SimpleStructKey<GameState, ImmutableList<Integer>> NEXT;

    public static final StructType<GameState, Builder> TYPE;
    // Calculated lazily
    private long boardValue = 0;
    private long boardScore = 0;

    static {
        ImmutableList.Builder<StructKey<GameState, ?, ?>> b = ImmutableList.builder();

        b.add(WIDTH = new SimpleStructKey<GameState, Integer>("width", 4));
        b.add(HEIGHT = new SimpleStructKey<GameState, Integer>("height", 4));
        b.add(RANDOM = new SimpleStructKey<GameState, ImmutablePrng>("prng", ImmutablePrng.of()));
        b.add(BOARD = new SimpleStructKey<GameState, ImmutableList<Integer>>("board"));
        b.add(NEXT = new SimpleStructKey<GameState, ImmutableList<Integer>>("next"));

        TYPE = new StructType<GameState, Builder>(b.build(), GameState::new, Builder::new);
    }

    /**
     * Main entrypoint to start a new game
     * 
     * Runs getNext(), emptyBoard(), populateBoard()
     * 
     * @return
     */
    public GameState init() {
        GameState g = this.getNext();
        g = g.emptyBoard();
        g = g.populateBoard();
        return g;
    }

    // Sets up board for initial game
    public GameState populateBoard() {
        GameState g = this;
        for(int i = 0; i < ((get(WIDTH) - 1) * (get(HEIGHT) - 1)); i++) {
            g = g.placeInitialPieceOnBoard();
        }
        return g;
    }

    public GameState oldPopulateBoard() {
        int num = (get(WIDTH) - 1) * (get(HEIGHT) - 1); // is 9 for default board
        ArrayList<Integer> b = Lists.newArrayList(get(BOARD));
        LinkedList<Integer> next = Lists.newLinkedList(get(NEXT));
        Pair<Integer, ImmutablePrng> rng = Pair.of(0, get(RANDOM));
        for(int i = 0; i < num; i++) {
            rng = rng.getRight().nextInt(b.size());
            // ensure space is empty, otherwise try again
            if(b.get(rng.getLeft()) != 0) {
                i -= 1;
                continue;
            }
            b.add(rng.getLeft(), next.pop());
        }
        return this.set(BOARD, ImmutableList.copyOf(b)).set(RANDOM, rng.getRight()).set(NEXT, ImmutableList.copyOf(next));
    }

    public long calculateBoardScore() {
        if(boardScore != 0) {
            return boardScore;
        }
        for(int i = 0; i < get(BOARD).size(); i++) {
            boardScore += scoreTile(get(BOARD).get(i));
        }
        return boardScore;
    }

    public long calculateBoardValue() {
        // XXX TODO: write value function
        // score + other attributes
        // two axis of rotation is valuable
        // many potential moves is valuable
        // two high-value things closer together is valuable
        // etc...
        return boardValue;
    }

    public boolean canMoveX() {
        // any blank spots mean we have 2 degrees of freedom
        if(get(BOARD).contains(0)) {
            return true;
        }
        int width = get(WIDTH);
        int height = get(HEIGHT);

        // there must exist at least one instance of two tiles next to each other that can be combined.
        for(int i = 0; i < (width - 1); i++) {
            for(int j = 0; j < (height); j++) {
                int thistile = getCoord(i, j);
                int rightof = getCoord(i + 1, j);

                if(canCombine(get(BOARD).get(thistile), get(BOARD).get(rightof))) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canMoveY() {
        // any blank spots mean we have 2 degrees of freedom
        if(get(BOARD).contains(0)) {
            return true;
        }
        int width = get(WIDTH);
        int height = get(HEIGHT);

        // there must exist at least one instance of two tiles next to each other that can be combined.
        for(int i = 0; i < (width); i++) {
            for(int j = 0; j < (height - 1); j++) {
                int thistile = getCoord(i, j);
                int below = getCoord(i, j + 1);

                if(canCombine(get(BOARD).get(thistile), get(BOARD).get(below))) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getCoord(int x, int y) {
        return x + (y * get(HEIGHT));
    }

    public static enum MoveDirection {
        LEFT, RIGHT, UP, DOWN;
    }

    public GameState move(MoveDirection md) {
        return shiftBoardAndInsertNewTile(md);
    }

    public ImmutableList<Integer> peekNext() {
        Integer next = get(NEXT).get(0);
        if(next == -1) {
            return ImmutableList.copyOf(getSpecialTiles());
        }
        return ImmutableList.of(next);
    }

    public GameState moveLeft() {
        // for each row, figure out what combines and if it moves or not
        ArrayList<Integer> rowsThatMoved = Lists.newArrayListWithCapacity(get(HEIGHT));
        ArrayList<Integer> newBoard = Lists.newArrayListWithCapacity(get(HEIGHT) * get(WIDTH));
        for(int i = 0; i < get(HEIGHT); i++) {
            for(int j = 0; j < (get(WIDTH) - 1); j++) {
                int x = get(BOARD).get(getCoord(j, i));
                int y = get(BOARD).get(getCoord(j + 1, i));
                if(canCombine(x, y)) {
                    // we found ones that can combine
                    // only add the row to "rows that moved" if a non-zero tile moved
                    // This prevents from inserting the new tile in an empty row
                    if(rowContainsNonZeroTile(i)) {
                        rowsThatMoved.add(i);
                    }
                    // place the combined tile in the new board
                    newBoard.add(getCoord(j, i), combine(x, y));
                    // place the remainder as they are in the row
                    for(j = j + 2; j < get(WIDTH); j++) {
                        newBoard.add(getCoord(j - 1, i), get(BOARD).get(getCoord(j, i)));
                    }
                    // add the zero space
                    newBoard.add(getCoord(get(WIDTH) - 1, i), 0);
                    break; // done with this row
                }
                // else: can't combine, keep looking, but copy over this value
                newBoard.add(getCoord(j, i), get(BOARD).get(getCoord(j, i)));
            }
        }
        // finally, add the next tile in
        Integer next = get(NEXT).get(0);
        Pair<Integer, ImmutablePrng> rng = Pair.of(0, get(RANDOM));
        int nextConsumed = 1;
        if(next == -1) {
            // special tile - have to pick one
            ImmutableList<Integer> specials = getSpecialTiles();
            if(specials.size() > 1) {
                rng = rng.getRight().nextInt(specials.size());
                next = specials.get(rng.getLeft());
            }
            else if(specials.size() == 1) {
                next = specials.get(0);
            }
            else {
                // we need to keep going, drop the special on the floor.
                if(get(NEXT).size() == 1) {
                    // oh crap. TODO: fix this edge case. The last item in NEXT was the special which we need to ignore, so we have to get the next value in a hurry.
                }
                next = get(NEXT).get(1); // grab the next item instead
                nextConsumed += 1;
            }
        }
        // ok, now we need to pick a place to put it
        int rowWithNewTile = -1;
        if(rowsThatMoved.size() == 1) {
            // it just goes in that row
            rowWithNewTile = Iterables.getOnlyElement(rowsThatMoved);
        }
        else {
            // gotta pick which row
            rng = rng.getRight().nextInt(rowsThatMoved.size());
            rowWithNewTile = rowsThatMoved.get(rng.getLeft());
        }
        newBoard.add(getCoord(get(WIDTH) - 1, rowWithNewTile), next);
        return this.set(BOARD, ImmutableList.copyOf(newBoard)).set(RANDOM, rng.getRight()).set(NEXT, get(NEXT).subList(nextConsumed, get(NEXT).size())).getNext();
    }

    private boolean rowContainsNonZeroTile(int i) {
        for(int j = 0; j < get(HEIGHT); j++) {
            if(get(BOARD).get(getCoord(j, i)) != 0) {
                return true;
            }
        }
        return false;
    }

    private boolean columnContainsNonZeroTile(int j) {
        for(int i = 0; i < get(WIDTH); i++) {
            if(get(BOARD).get(getCoord(j, i)) != 0) {
                return true;
            }
        }
        return false;
    }

    public GameState moveRight() {
        return null; // TODO
    }

    public GameState moveUp() {
        return null; // TODO
    }

    public GameState moveDown() {
        return null; // TODO
    }

    // PRIVATE stuffs
    // This generates the new "next" list when it has become empty
    private GameState getNext() {
        if(!get(NEXT).isEmpty()) {
            return this;
        }

        int dimension = Math.min(get(WIDTH), get(HEIGHT));
        ArrayList<Integer> b = new ArrayList<Integer>();
        for(int i = 0; i < dimension; i++) {
            b.add(1);
            b.add(2);
            b.add(3);
        }
        // TODO: current best guess is that approximately 50% of the time, a "special" card is inserted into the stack.
        // Thanks to http://kotaku.com/tips-for-playing-threes-the-new-mobile-game-everyones-1522388747
        //
        Pair<Integer, ImmutablePrng> rng = get(RANDOM).nextInt(2);
        if(rng.getLeft() == 1) {
            // insert "special" tile
            // The problem is, if the highest number on the board changes, so will the special tiles, so we have to delay their choice
            // instead, we insert the placeholder -1j
            b.add(-1);
        }
        ImmutablePrng p = rng.getRight();
        // will call "nextInt" size -1 times
        Collections.shuffle(b, new Random(p.getSeed()));
        for(int i = b.size(); i > 1; i--) {
            p = p.next();
        }
        return set(NEXT, ImmutableList.copyOf(b)).set(RANDOM, p);
    }

    // When we get a "special", it depends on the highest number
    // When 48 is the highest, you get a 6
    // By extrapolation, when 96 is the highest, you get 6 or 12
    // When 192 is the highest, you get 6, 12, or 24
    // when 384 is the highest, you get 12, 24, 48
    // etc etc. so you get X/8, X/16, x/32, but not lower than 6, and at most 3 tiles.
    private ImmutableList<Integer> getSpecialTiles() {
        Integer max = 0;
        for(Integer tile : get(BOARD)) {
            max = Math.max(max, tile);
        }
        ImmutableList.Builder<Integer> specials = ImmutableList.builder();
        int count = 0;

        while(count < 3 && max > 24) {
            specials.add(max / 8);
            count += 1;
            max = max / 2;
        }
        return specials.build();
    }

    public GameState emptyBoard() {
        return set(BOARD, ImmutableList.copyOf(getEmptyBoard(get(HEIGHT), get(WIDTH))));
    }

    private static boolean validGameInteger(int x) {
        return VALID_GAME_INTS.contains(x);
    }

    /**
     * shifting in the direction TOWARDS to
     */
    private static boolean canCombine(int from, int to) {
        if(from == 0) {
            return false;
        }
        if(to == 0) {
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

    private static int combine(int x, int y) {
        return x + y;
    }

    private static int scoreTile(int i) {
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

    public GameState shiftBoardAndInsertNewTile(MoveDirection md) {
        ArrayList<Integer> board = Lists.newArrayList(get(BOARD));
        int width = get(WIDTH);
        int height = get(HEIGHT);

        HashSet<Integer> rowsThatMoved = Sets.newHashSet();
        switch(md) {
        case LEFT:
            // shifting to the left
            for(int i = 0; i < height; i++) {
                if(!rowContainsNonZeroTile(i)) {
                    // nothing to do here
                    continue;
                }
                for(int j = 0, js = 0; j < width; j++, js++) {
                    int jcoord = getCoord(j, i);
                    int jscoord = getCoord(js, i);
                    if(j == js) {
                        // haven't found a combine yet
                        if(j == width - 1) {
                            // end of row with no combine, done
                            continue;
                        }
                        if(canCombine(get(BOARD).get(jcoord + 1), get(BOARD).get(jcoord))) {
                            board.set(jcoord, combine(get(BOARD).get(jcoord + 1), get(BOARD).get(jcoord)));
                            rowsThatMoved.add(i);
                            js += 1;
                            continue;
                        }
                        // if havent' found a combine yet but also we can't comine j and j+1, proceed normally
                    }
                    if(js > (width - 1)) {
                        // we had a shift, put a zero in there
                        board.set(jcoord, 0);
                        continue;
                    }
                    // otherwise, normal copy
                    board.set(jcoord, get(BOARD).get(jscoord));
                }
            }
            break;
        case RIGHT:
            // shifting to the right
            for(int i = 0; i < height; i++) {
                if(!rowContainsNonZeroTile(i)) {
                    // nothing to do here
                    continue;
                }
                for(int j = (width - 1), js = (width - 1); j >= 0; j--, js--) {
                    int jcoord = getCoord(j, i);
                    int jscoord = getCoord(js, i);
                    if(j == js) {
                        // haven't found a combine yet
                        if(j == 0) {
                            // end of row with no combine, done
                            continue;
                        }
                        if(canCombine(get(BOARD).get(jcoord - 1), get(BOARD).get(jcoord))) {
                            board.set(jcoord, combine(get(BOARD).get(jcoord - 1), get(BOARD).get(jcoord)));
                            rowsThatMoved.add(i);
                            js -= 1;
                            continue;
                        }
                        // if havent' found a combine yet but also we can't comine j and j+1, proceed normally
                    }
                    if(js < 0) {
                        // we had a shift, put a zero in there
                        board.set(jcoord, 0);
                        continue;
                    }
                    // otherwise, normal copy
                    board.set(jcoord, get(BOARD).get(jscoord));
                }
            }
            break;
        case UP:
            // shifting up
            for(int i = 0; i < width; i++) {
                if(!columnContainsNonZeroTile(i)) {
                    // nothing to do here
                    continue;
                }
                for(int j = 0, js = 0; j < height; j++, js++) {
                    int jcoord = getCoord(i, j);
                    int jscoord = getCoord(i, js);
                    int jcoordp1 = getCoord(i, j + 1);
                    if(j == js) {
                        // haven't found a combine yet
                        if(j == width - 1) {
                            // end of row with no combine, done
                            continue;
                        }
                        if(canCombine(get(BOARD).get(jcoordp1), get(BOARD).get(jcoord))) {
                            board.set(jcoord, combine(get(BOARD).get(jcoord), get(BOARD).get(jcoordp1)));
                            rowsThatMoved.add(i);
                            js += 1;
                            continue;
                        }
                        // if havent' found a combine yet but also we can't comine j and j+1, proceed normally
                    }
                    if(js > (height - 1)) {
                        // we had a shift, put a zero in there
                        board.set(jcoord, 0);
                        continue;
                    }
                    // otherwise, normal copy
                    board.set(jcoord, get(BOARD).get(jscoord));
                }
            }
            break;
        case DOWN:
            // shifting down
            for(int i = 0; i < width; i++) {
                if(!columnContainsNonZeroTile(i)) {
                    // nothing to do here
                    continue;
                }
                for(int j = (height - 1), js = (height - 1); j >= 0; j--, js--) {
                    int jcoord = getCoord(i, j);
                    int jcoordm1 = getCoord(i, j - 1);
                    int jscoord = getCoord(i, js);
                    if(j == js) {
                        // haven't found a combine yet
                        if(j == 0) {
                            // end of row with no combine, done
                            continue;
                        }
                        if(canCombine(get(BOARD).get(jcoordm1), get(BOARD).get(jcoord))) {
                            board.set(jcoord, combine(get(BOARD).get(jcoord), get(BOARD).get(jcoordm1)));
                            rowsThatMoved.add(i);
                            js -= 1;
                            continue;
                        }
                        // if havent' found a combine yet but also we can't comine j and j-1, proceed normally
                    }
                    if(js < 0) {
                        // we had a shift, put a zero in there
                        board.set(jcoord, 0);
                        continue;
                    }
                    // otherwise, normal copy
                    board.set(jcoord, get(BOARD).get(jscoord));
                }
            }
            break;
        default:
            throw new IllegalStateException("unknown direction");
        }
        return this.set(BOARD, ImmutableList.copyOf(board)).placeNextPieceOnBoard(md, ImmutableList.copyOf(rowsThatMoved));
    }

    private GameState placeInitialPieceOnBoard() {
        ArrayList<Integer> next = Lists.newArrayList(get(NEXT));
        ArrayList<Integer> board = Lists.newArrayList(get(BOARD));
        Pair<Integer, ImmutablePrng> rng = Pair.of(0, get(RANDOM));

        Integer nextTile = next.get(0);
        next = Lists.newArrayList(next.subList(1, next.size()));
        // place a tile, but only in empty (0) spaces
        int emptyCount = Iterables.frequency(board, 0);
        rng = rng.getRight().nextInt(emptyCount);
        int emptySlot = rng.getLeft();
        // if there were 10 empty spaces, emptySlot will be 0-9 inclusive
        // if it is 0, insert into first empty slot, etc.
        boolean placed = false;
        for(int i = 0; emptySlot >= 0; i++) {
            if(board.get(i) == 0) {
                if(emptySlot == 0) {
                    // we found our slot
                    board.set(i, nextTile);
                    placed = true;
                    break;
                }
                emptySlot -= 1;
            }
        }
        if(!placed) {
            throw new IllegalStateException("Unable to place tile on board - is board full?");
        }
        return this.set(BOARD, ImmutableList.copyOf(board)).set(NEXT, ImmutableList.copyOf(next)).set(RANDOM, rng.getRight()).getNext();
    }

    private GameState placeNextPieceOnBoard(MoveDirection md, ImmutableList<Integer> rowsThatMoved) {
        ArrayList<Integer> next = Lists.newArrayList(get(NEXT));
        ArrayList<Integer> board = Lists.newArrayList(get(BOARD));
        Pair<Integer, ImmutablePrng> rng = Pair.of(0, get(RANDOM));

        Integer nextTile = -1;
        while(nextTile == -1) {
            // technically possible, though unlikely, to have two empty special tiles in a row
            nextTile = next.get(0);
            next = Lists.newArrayList(next.subList(1, next.size()));
            if(nextTile == -1) {
                // special tile
                ImmutableList<Integer> specialTiles = getSpecialTiles();
                if(specialTiles.isEmpty()) {
                    // we should skip the special tile and place the next one, but if it is empty, we have to pre-fetch it
                    if(next.isEmpty()) {
                        // edge case: if we need another tile and next is empty, we have to manually pre-fetch it
                        GameState temp = this.set(NEXT, ImmutableList.of()).set(RANDOM, rng.getRight());
                        temp = temp.getNext();
                        next = Lists.newArrayList(temp.get(NEXT));
                        rng = Pair.of(0, temp.get(RANDOM));
                    }
                    continue; // nextTile is still -1, so we'll try the next in the list now
                }
                // specialTiles is not empty
                if(specialTiles.size() == 1) {
                    // just use the special tile
                    nextTile = Iterables.getOnlyElement(specialTiles);
                    continue;
                }
                // we have to randomly pick one
                rng = rng.getRight().nextInt(specialTiles.size());
                nextTile = specialTiles.get(rng.getLeft());
            }
        }

        int x;
        int y;
        switch(md) {
        case LEFT:
            // putting the new piece on the right side of the board
            x = get(WIDTH) - 1;
            if(rowsThatMoved.size() == 1) {
                y = Iterables.getOnlyElement(rowsThatMoved);
            }
            else {
                // select one
                rng = rng.getRight().nextInt(rowsThatMoved.size());
                y = rowsThatMoved.get(rng.getLeft());
            }
            break;
        case RIGHT:
            // putting the new piece on the left side of the board
            x = 0;
            if(rowsThatMoved.size() == 1) {
                y = Iterables.getOnlyElement(rowsThatMoved);
            }
            else {
                // select one
                rng = rng.getRight().nextInt(rowsThatMoved.size());
                y = rowsThatMoved.get(rng.getLeft());
            }
            break;
        case UP:
            // putting the new piece on the bottom side of the board
            y = get(HEIGHT) - 1;
            if(rowsThatMoved.size() == 1) {
                x = Iterables.getOnlyElement(rowsThatMoved);
            }
            else {
                // select one
                rng = rng.getRight().nextInt(rowsThatMoved.size());
                x = rowsThatMoved.get(rng.getLeft());
            }
            break;
        case DOWN:
            // putting the new piece on the top side of the board
            y = 0;
            if(rowsThatMoved.size() == 1) {
                x = Iterables.getOnlyElement(rowsThatMoved);
            }
            else {
                // select one
                rng = rng.getRight().nextInt(rowsThatMoved.size());
                x = rowsThatMoved.get(rng.getLeft());
            }
            break;
        default:
            throw new IllegalStateException("unknown direction");
        }
        board.set(getCoord(x, y), nextTile);
        return this.set(BOARD, ImmutableList.copyOf(board)).set(NEXT, ImmutableList.copyOf(next)).set(RANDOM, rng.getRight()).getNext();
    }

    private static ArrayList<Integer> getEmptyBoard(int height, int width) {
        int dim = height * width;
        ArrayList<Integer> board = Lists.newArrayListWithCapacity(dim);
        for(int i = 0; i < dim; i++) {
            board.add(0);
        }
        return board;
    }

    /**
     * Shift tiles towards the 0 index.
     */
    private static Pair<Boolean, ArrayList<Integer>> shiftRow(ImmutableList<Integer> row) {
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
        return Pair.of(shifted, newRow);
    }
}