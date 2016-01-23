package org.cmyers.games.threes.random;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executors;
import misc1.commons.concurrent.ctree.ComputationTree;
import misc1.commons.concurrent.ctree.ComputationTreeComputer;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

public class ImmutablePrngTest {

    private ImmutableSet<ImmutablePrng> generate(ObjectUtils.Null x) {
        ImmutableSet.Builder<ImmutablePrng> b = ImmutableSet.builder();
        try {
            for(int i = 0; i < 100000; i++) {
                b.add(ImmutablePrng.of());
            }
        }
        catch(RuntimeException e) {
            // do nothing
        }
        return b.build();
    }

    @Test
    public void testConstruction() {
        // Ensures multiple simultaneous calls to ctor yields different seeds
        ComputationTree<ImmutableSet<ImmutablePrng>> ct1 = ComputationTree.constant().transform(this::generate);
        ComputationTree<ImmutableSet<ImmutablePrng>> ct2 = ComputationTree.constant().transform(this::generate);
        ComputationTree<ImmutableSet<ImmutablePrng>> ct3 = ComputationTree.constant().transform(this::generate);
        ComputationTree<ImmutableSet<ImmutablePrng>> ct4 = ComputationTree.constant().transform(this::generate);
        ComputationTree<ImmutableSet<ImmutablePrng>> ct5 = ComputationTree.constant().transform(this::generate);
        ComputationTree<ImmutableSet<ImmutablePrng>> ct6 = ComputationTree.constant().transform(this::generate);
        ComputationTreeComputer ctc = new ComputationTreeComputer(Executors.newFixedThreadPool(8));
        ctc.start(ct1);
        ctc.start(ct2);
        ctc.start(ct3);
        ctc.start(ct4);
        ctc.start(ct5);
        ctc.start(ct6);
        HashSet<Long> seeds = Sets.newHashSet();
        for(ComputationTree<ImmutableSet<ImmutablePrng>> ct : ImmutableList.of(ct1, ct2, ct3, ct4, ct5, ct6)) {
            ImmutableSet<ImmutablePrng> set = ctc.await(ct).getCommute();
            for(ImmutablePrng p : set) {
                if(seeds.contains(p.getSeed())) {
                    Assert.fail("Already saw this seed: " + p.getSeed());
                }
                seeds.add(p.getSeed());
            }
        }
    }

    @Test
    public void testValues() {
        // ensure the same object yields the same "random" numbers
        ImmutablePrng p1 = ImmutablePrng.of();
        ImmutablePrng p2 = ImmutablePrng.raw(p1.getSeed());

        Pair<Integer, ImmutablePrng> one = p1.nextInt();
        Pair<Integer, ImmutablePrng> two = p2.nextInt();
        Assert.assertEquals(one, two);
        Assert.assertEquals(one.getRight().nextInt(), two.getRight().nextInt());
    }

    @Test
    public void testValuesAreRandomButRepeatable() {
        // use a set seed so we know we won't get "unlucky"
        // this seed doesn't have a repeat for about 130k rounds
        long seed = 9;
        int rounds = 100000;
        ImmutablePrng p1 = ImmutablePrng.of(seed);

        HashSet<Integer> ints = Sets.newHashSetWithExpectedSize(rounds);
        List<Integer> sequence = Lists.newArrayListWithExpectedSize(rounds);
        Pair<Integer, ImmutablePrng> result = p1.nextInt();
        for(int i = 0; i < rounds; i++) {
            result = result.getRight().nextInt();
            if(ints.contains(result.getLeft())) {
                Assert.fail("Already saw this value: " + result.getLeft() + " (at count " + i + ")");
            }
            // System.out.println("Got: " + result.getLeft());
            ints.add(result.getLeft());
            sequence.add(result.getLeft());
        }

        // Now repeat it
        ImmutablePrng p2 = ImmutablePrng.of(seed);
        result = p2.nextInt();
        for(int i = 0; i < rounds; i++) {
            result = result.getRight().nextInt();
            Assert.assertEquals(sequence.get(i), result.getLeft());
        }

        BigInteger sum = BigInteger.ZERO;
        for(Integer i : sequence) {
            sum = sum.add(BigInteger.valueOf(i));
        }
        BigInteger average = sum.divide(BigInteger.valueOf(rounds));
        Assert.assertTrue(average.abs().compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) == -1);
    }
}
