package org.cmyers.nappysak.utilityfunction;

import java.util.List;

/**
 * Classes that calculate a utility function based only on the direct neighbors in the list.
 * 
 * This class uses Long values to represent utility.
 * @author cmyers
 *
 */
public class NeighborUtilityFunction<T extends UtilityEntity<Long>> implements UtilityFunction<Long, T> {

	private final boolean wraparound;
	
	public NeighborUtilityFunction() {
		this.wraparound = true; // default
	}
	
	/**
	 * 
	 * @param wraparound if true, first and last element in list are "neighbors".
	 */
	public NeighborUtilityFunction(boolean wraparound) {
		this.wraparound = wraparound;
	}
	
	@Override
	public Long evaluate(List<T> list) {
		if (list.size() < 2) {
			// 0 or 1 entities can't have their utility compared
			return 0L;
		}
		long utility = 0;
		// because we are looking at 2 elements at once, we go to size - 1 instead of size.
		for (int i = 0; i < list.size() - 1; i++) {
			// since it can be the case that A likes B but B does not like A, utility is not always symmetric.
			// For this reason, we include both directions.
			utility += list.get(i).getPartialUtility(list.get(i+1));
			utility += list.get(i+1).getPartialUtility(list.get(i));
		}
		if (wraparound) {
			utility += list.get(0).getPartialUtility(list.get(list.size()-1));
			utility += list.get(list.size() - 1).getPartialUtility(list.get(0));
		}
		return utility;
	} 
	

	
}
