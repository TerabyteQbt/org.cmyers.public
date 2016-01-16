package org.cmyers.nappysak.permutationengine;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;

/**
 * This permutation engine uses Knuth Shuffles to generate a random permutation given an identity permutation.
 *
 * http://en.wikipedia.org/wiki/Random_permutation#Knuth_shuffles
 *
 * This process is O(n) in the length of the list.
 *
 * @author cmyers
 *
 */
public class KnuthShufflesPermutationEngine<T> implements PermutationEngine<T> {

    private final Random r;

    public KnuthShufflesPermutationEngine() {
        r = new Random();
    }

    // For testing
    KnuthShufflesPermutationEngine(Random r) {
        this.r = r;
    }

    @Override
    public List<T> next(List<T> prev) {
        List<T> permutation = Lists.newArrayList(prev);
        if(prev.size() <= 1) {
            return Lists.newArrayList(prev);
        }
        for(int i = 0; i < prev.size(); i++) {
            // generate a uniform random value from i (inclusive) to prev.size() - 1 (exclusive)
            // r.nextInt(x) returns 0 (inclusive) to x (exclusive)
            int index = r.nextInt(prev.size() - i) + i;
            swap(permutation, i, index);
        }
        return permutation;
    }

    private void swap(List<T> list, int i, int j) {
        T value = list.get(i);
        list.set(i, list.get(j));
        list.set(j, value);
    }

}
