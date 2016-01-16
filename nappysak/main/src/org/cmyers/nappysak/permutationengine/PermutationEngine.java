package org.cmyers.nappysak.permutationengine;

import java.util.List;

/**
 * A PermutationEngine can generate permutations of lists of items of type T.
 *
 * Generally, given a specific Permutation <List<T>>, a PermutationEngine will yield the "next" permutation of that list. If you ask for a new permutation, passing the last permutation, N! times, you should receive all of the N! possible permutations exactly once.
 *
 * @author cmyers
 *
 * @param <T>
 */
public interface PermutationEngine<T> {

    public List<T> next(List<T> prev);

    /**
     * Returns the default Permutation Engine for completely random shuffles
     *
     * @return An engine for completely random shuffles
     */
    public static <S> PermutationEngine<S> getDefaultRandomizerEngine() {
        return new KnuthShufflesPermutationEngine<S>();
    }

    /**
     * Returns the default Permutation Engine for completely random shuffles
     *
     * @return An engine for generating a permutation which is very similar to the input but different in a random way.
     */
    public static <S> PermutationEngine<S> getDefaultPermuterEngine() {
        return new NearbyPermutationEngine<S>();
    }
}
