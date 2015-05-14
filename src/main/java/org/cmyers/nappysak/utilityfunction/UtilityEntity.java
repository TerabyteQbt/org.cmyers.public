package org.cmyers.nappysak.utilityfunction;

/**
 * Any entity which has a utility with some other utility entity should implement this interface.
 * 
 * @author cmyers
 *
 */
public interface UtilityEntity<U> {

	public String getName();
	public U getPartialUtility(UtilityEntity<U> other);
}
