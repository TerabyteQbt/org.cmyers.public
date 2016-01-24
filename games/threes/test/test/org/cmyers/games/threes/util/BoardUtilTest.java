package org.cmyers.games.threes.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.Map;
import misc1.commons.ds.ImmutableSalvagingMap;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

public class BoardUtilTest {

    @Test
    public void testGetEmptyBoard() {
        ArrayList<Integer> e = BoardUtil.getEmptyBoard(3, 3);
        Assert.assertEquals(9, e.size());
        e = BoardUtil.getEmptyBoard(4, 4);
        Assert.assertEquals(16, e.size());
        e = BoardUtil.getEmptyBoard(4, 5);
        Assert.assertEquals(20, e.size());
    }

    @Test
    public void testShiftRow() {
        ImmutableSet.Builder<Pair<ImmutableList<Integer>, ImmutableList<Integer>>> tests = ImmutableSet.builder();

        // input, output
        tests.add(Pair.of(ImmutableList.of(0, 0, 0, 0), ImmutableList.of(0, 0, 0, 0)));
        tests.add(Pair.of(ImmutableList.of(1, 0, 0, 0), ImmutableList.of(1, 0, 0, 0)));
        tests.add(Pair.of(ImmutableList.of(1, 3, 0, 0), ImmutableList.of(1, 3, 0, 0)));
        tests.add(Pair.of(ImmutableList.of(1, 3, 6, 0), ImmutableList.of(1, 3, 6, 0)));
        tests.add(Pair.of(ImmutableList.of(1, 3, 6, 12), ImmutableList.of(1, 3, 6, 12)));
        tests.add(Pair.of(ImmutableList.of(1, 1, 1, 1), ImmutableList.of(1, 1, 1, 1)));

        tests.add(Pair.of(ImmutableList.of(0, 1, 0, 0), ImmutableList.of(1, 0, 0, 0)));
        tests.add(Pair.of(ImmutableList.of(0, 0, 0, 1), ImmutableList.of(0, 0, 1, 0)));
        tests.add(Pair.of(ImmutableList.of(1, 2, 0, 0), ImmutableList.of(3, 0, 0, 0)));
        tests.add(Pair.of(ImmutableList.of(1, 0, 2, 0), ImmutableList.of(1, 2, 0, 0)));
        tests.add(Pair.of(ImmutableList.of(0, 1, 0, 1), ImmutableList.of(1, 0, 1, 0)));
        tests.add(Pair.of(ImmutableList.of(1, 1, 2, 0), ImmutableList.of(1, 3, 0, 0)));
        tests.add(Pair.of(ImmutableList.of(1, 1, 2, 2), ImmutableList.of(1, 3, 2, 0)));
        tests.add(Pair.of(ImmutableList.of(0, 0, 0, 0), ImmutableList.of(0, 0, 0, 0)));
        tests.add(Pair.of(ImmutableList.of(0, 0, 0, 0), ImmutableList.of(0, 0, 0, 0)));

        for(Pair<ImmutableList<Integer>, ImmutableList<Integer>> p : tests.build()) {
            ImmutableList<Integer> result = BoardUtil.shiftRow(p.getLeft());
            Assert.assertEquals(p.getRight(), result);
        }
    }

    @Test
    public void testCanCombine() {
        ImmutableSalvagingMap<Pair<Integer, Integer>, Boolean> tests = ImmutableSalvagingMap.of();
        tests = tests.simplePut(Pair.of(0, 0), false);
        tests = tests.simplePut(Pair.of(1, 2), true);
        tests = tests.simplePut(Pair.of(2, 1), true);
        tests = tests.simplePut(Pair.of(3, 3), true);
        tests = tests.simplePut(Pair.of(6, 6), true);
        tests = tests.simplePut(Pair.of(1, 1), false);
        tests = tests.simplePut(Pair.of(3, 1), false);
        tests = tests.simplePut(Pair.of(1, 3), false);
        // 0 into 1 is a no go
        tests = tests.simplePut(Pair.of(1, 0), false);
        // but 1 into 0 works
        tests = tests.simplePut(Pair.of(0, 1), true);
        for(Map.Entry<Pair<Integer, Integer>, Boolean> e : tests.entries()) {
            Assert.assertEquals(e.getValue(), BoardUtil.canCombine(e.getKey().getLeft(), e.getKey().getRight()));
        }
    }
}
