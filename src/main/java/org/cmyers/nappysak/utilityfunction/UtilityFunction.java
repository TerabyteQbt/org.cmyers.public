package org.cmyers.nappysak.utilityfunction;

import java.util.List;

public interface UtilityFunction<U, T extends UtilityEntity<U>> {
	
	public U evaluate(List<T> list);
}
