package org.cmyers.nappysak.entities;

import java.util.Map;
import org.cmyers.nappysak.utilityfunction.UtilityEntity;

/**
 * This class represents an entity which has preferences about other entities.
 *
 * The preferenceMap is a map of named entities and the partial utility of being associated with them.
 *
 * The utility of an unknown entity is 0.
 *
 * @author cmyers
 *
 */
public class PreferencedEntity implements UtilityEntity<Long> {

    private final String name;
    private final Map<String, Long> preferenceMap;

    public PreferencedEntity(String name, Map<String, Long> preferenceMap) {
        this.name = name;
        this.preferenceMap = preferenceMap;
    }

    @Override
    public Long getPartialUtility(UtilityEntity<Long> other) {
        if(preferenceMap.containsKey(other.getName())) {
            return preferenceMap.get(other.getName());
        }
        return 0L;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "[Entity:" + name + "]";
    }

}
