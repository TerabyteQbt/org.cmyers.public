package org.cmyers.games.threes.state;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import misc1.commons.ds.ImmutableSalvagingMap;
import misc1.commons.ds.SimpleStructKey;
import misc1.commons.ds.Struct;
import misc1.commons.ds.StructBuilder;
import misc1.commons.ds.StructKey;
import misc1.commons.ds.StructType;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.cmyers.games.threes.random.ImmutablePrng;
import org.cmyers.games.threes.util.BoardUtil;

/**
 * To build a blank board:
 *
 * GameState g = GameState.TYPE.builder().emptyBoard().emptyNext().random().build();
 *
 * To initialize a new game, take that empty board and run:
 *
 * g = g.init();
 *
 * Which is the same as:
 *
 * g = g.setNextNext().populateBoard();
 *
 * @author cmyers
 *
 */
public class GameState extends Struct<GameState, GameState.Builder> {

    private GameState(ImmutableMap<StructKey<GameState, ?, ?>, Object> map) {
        super(TYPE, map);
    }

    public static class Builder extends StructBuilder<GameState, Builder> {
        private Builder(ImmutableSalvagingMap<StructKey<GameState, ?, ?>, Object> map) {
            super(TYPE, map);
        }

        public Builder random() {
            return set(RANDOM, ImmutablePrng.of());
        }

        public Builder random(long l) {
            return set(RANDOM, ImmutablePrng.of(l));
        }

        public Builder emptyNext() {
            return set(NEXT, ImmutableList.of());
        }

        public Builder emptyBoard() {
            return set(BOARD, ImmutableList.copyOf(BoardUtil.getEmptyBoard(get(HEIGHT), get(WIDTH))));
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
        // no default value, because if we did it'd be used for all structs, giving them all the same seed
        b.add(RANDOM = new SimpleStructKey<GameState, ImmutablePrng>("prng"));
        b.add(BOARD = new SimpleStructKey<GameState, ImmutableList<Integer>>("board"));
        b.add(NEXT = new SimpleStructKey<GameState, ImmutableList<Integer>>("next"));

        TYPE = new StructType<GameState, Builder>(b.build(), GameState::new, Builder::new);
    }

    /**
     * Main entrypoint to start a new game
     *
     * Runs setNextNext() and populateBoard()
     *
     * If the board is not empty, you should also run emptyBoard() first.
     *
     * @return
     */
    public GameState init() {
        GameState g = this.setNextNext();
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

    public long calculateBoardScore() {
        if(boardScore != 0) {
            return boardScore;
        }
        for(int i = 0; i < get(BOARD).size(); i++) {
            boardScore += BoardUtil.scoreTile(get(BOARD).get(i));
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
        return boardValue + calculateBoardScore();
    }

    public int getCoord(int x, int y) {
        if(x < 0 || y < 0) {
            throw new IllegalArgumentException("Coordinates must be positive integers starting from 0");
        }
        if(x >= get(WIDTH)) {
            throw new IllegalArgumentException("Cannot get coordinate for width " + Integer.valueOf(x) + " (width = " + Integer.valueOf(get(WIDTH)) + ")");
        }
        if(y >= get(HEIGHT)) {
            throw new IllegalArgumentException("Cannot get coordinate for height " + Integer.valueOf(y) + " (height = " + Integer.valueOf(get(HEIGHT)) + ")");
        }
        return x + (y * get(WIDTH));
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

    // This generates the new "next" list when it has become empty
    public GameState setNextNext() {
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

    // PRIVATE stuffs

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
        return set(BOARD, ImmutableList.copyOf(BoardUtil.getEmptyBoard(get(HEIGHT), get(WIDTH))));
    }

    public boolean canMove(MoveDirection md) {
        HashSet<Integer> rowsThatMoved = Sets.newHashSet();
        ImmutableList<ImmutableList<Integer>> slices = BoardUtil.boardToSlices(this, md);
        for(int i = 0; i < slices.size(); i++) {
            ImmutableList<Integer> slice = slices.get(i);
            ImmutableList<Integer> newslice = BoardUtil.shiftRow(slice);
            if(!slice.equals(newslice)) {
                rowsThatMoved.add(i);
            }
        }
        if(!rowsThatMoved.isEmpty()) {
            return true;
        }
        return false;
    }

    public GameState shiftBoardAndInsertNewTile(MoveDirection md) {
        HashSet<Integer> rowsThatMoved = Sets.newHashSet();
        ImmutableList<ImmutableList<Integer>> slices = BoardUtil.boardToSlices(this, md);
        ImmutableList.Builder<ImmutableList<Integer>> newSlices = ImmutableList.builder();
        for(int i = 0; i < slices.size(); i++) {
            ImmutableList<Integer> slice = slices.get(i);
            ImmutableList<Integer> newSlice = BoardUtil.shiftRow(slice);
            if(!slice.equals(newSlice)) {
                rowsThatMoved.add(i);
            }
            newSlices.add(newSlice);
        }
        if(rowsThatMoved.isEmpty()) {
            throw new IllegalArgumentException("Unable to move " + md);
        }
        Triple<GameState, Integer, Integer> newPiece = this.getNextPieceAndRow(md, ImmutableList.copyOf(rowsThatMoved));
        ImmutableList<ImmutableList<Integer>> builtNewSlices = newSlices.build();
        ImmutableList.Builder<ImmutableList<Integer>> newSlicesWithNewPiece = ImmutableList.builder();
        for(int i = 0; i < builtNewSlices.size(); i++) {
            ImmutableList<Integer> slice = builtNewSlices.get(i);
            if(i == newPiece.getRight()) {
                // replace the end of this row with the new tile
                if(!slice.get(slice.size() - 1).equals(0)) {
                    throw new IllegalStateException("Moved row does not end in a 0");
                }
                newSlicesWithNewPiece.add(ImmutableList.copyOf(Iterables.concat(slice.subList(0, slice.size() - 1), ImmutableList.of(newPiece.getMiddle()))));
                continue;
            }
            // otherwise unchanged
            newSlicesWithNewPiece.add(slice);
        }
        return newPiece.getLeft().set(GameState.BOARD, ImmutableList.copyOf(BoardUtil.slicesToBoard(newPiece.getLeft(), newSlicesWithNewPiece.build(), md)));
    }

    private GameState placeInitialPieceOnBoard() {
        ArrayList<Integer> next = Lists.newArrayList(get(NEXT));
        ArrayList<Integer> board = Lists.newArrayList(get(BOARD));
        Pair<Integer, ImmutablePrng> rng = Pair.of(0, get(RANDOM));

        Integer nextTile = -1;
        while(nextTile == -1) {
            // it should be impossible to run out, since we start with 12 or 13, and only one can be "special"
            nextTile = next.get(0);
            next = Lists.newArrayList(next.subList(1, next.size()));
        }
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
        return this.set(BOARD, ImmutableList.copyOf(board)).set(NEXT, ImmutableList.copyOf(next)).set(RANDOM, rng.getRight()).setNextNext();
    }

    private Triple<GameState, Integer, Integer> getNextPieceAndRow(MoveDirection md, ImmutableList<Integer> rowsThatMoved) {
        ArrayList<Integer> next = Lists.newArrayList(get(NEXT));
        ImmutableList<Integer> board = get(BOARD);
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
                        temp = temp.setNextNext();
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

        int row = -1;
        if(rowsThatMoved.size() == 1) {
            row = Iterables.getOnlyElement(rowsThatMoved);
        }
        else {
            // select one
            rng = rng.getRight().nextInt(rowsThatMoved.size());
            row = rowsThatMoved.get(rng.getLeft());
        }
        return Triple.of(this.set(NEXT, ImmutableList.copyOf(next)).set(RANDOM, rng.getRight()).setNextNext(), nextTile, row);
    }
}

