package org.cmyers.nappysak.permutationengine;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class KnuthShufflesPermutationEngineTest {

    private PermutationEngine<String> pe;

    // We trust java's randomness is "actually random"
    // So let's just remove the randomness from the test by using a constant seed.
    @Before
    public void setUp() {
        pe = new KnuthShufflesPermutationEngine<String>(new Random(6)); // chosen by fair dice roll
    }

    @Test
    public void testWithEmptyList() {
        List<String> empty = ImmutableList.of();
        List<String> result = pe.next(empty);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testWithSingletonList() {
        List<String> singleton = ImmutableList.of("foo");
        List<String> result = pe.next(singleton);
        Assert.assertEquals(singleton, result);
    }

    @Test
    public void testWithTwoList() {
        List<String> twolist = ImmutableList.of("foo", "bar");
        List<String> result = pe.next(twolist);
        Assert.assertTrue(result.contains("foo"));
        Assert.assertTrue(result.contains("bar"));
    }

    @Test
    public void testWithTwoListProbabilityLooksRight() {
        List<String> twolist = ImmutableList.of("foo", "bar");
        int iter = 10000;
        int count = 0;
        for(int i = 0; i < iter; i++) {
            List<String> result = pe.next(twolist);
            if(result.get(0).equals("foo")) {
                count = count + 1;
            }
        }
        // There are only two possible permutations of two items, count the number of times we see one of them.
        // While this test could fail with rare probability, we have chosen a set seed so we know it doesn't fail due to luck. If that ever changes, it is because the JDK's random library changed *and* we got very very unlucky - or more likely, our code is busted.
        // As of the time of this writing, the actual ratio is 0.5034 (on JDK8)
        Assert.assertTrue(count > (1.0 * iter * 0.48));
        Assert.assertTrue(count < (1.0 * iter * 0.52));
        // System.out.println("ratio = " + Double.toString(1.0*count / iter));
    }
}
