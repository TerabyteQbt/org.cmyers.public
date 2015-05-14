package org.cmyers.nappysak.permutationengine;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

/**
 * This PermutationEngine, given a specific Permutation <List<T>>, will generate a permutation that is "one off" from the given permutation in a random way, by swapping two elements.
 * 
 * This process is O(1)
 * 
 * @author cmyers
 *
 */
public class NearbyPermutationEngine<T> implements PermutationEngine<T> {

	private final Random r;
	
	public NearbyPermutationEngine() {
		r = new Random();
	}
	
	// for testing
	NearbyPermutationEngine(Random r) {
		this.r = r;
	}
	@Override
	public List<T> next(List<T> prev) {
		List<T> permutation = Lists.newArrayList(prev);
		if (prev.size() <= 1) {
			return Lists.newArrayList(prev);
		}
		int i = r.nextInt(prev.size());
		int j = r.nextInt(prev.size()-1);
		// j = i means we chose the last element
		if (j == i) {
			j = prev.size() - 1;
		}
		swap(permutation, i, j);
		return permutation;
	}
	
	private void swap(List<T> list, int i, int j) {
		T value = list.get(i);
		list.set(i, list.get(j));
		list.set(j, value);
	}

}
