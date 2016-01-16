package org.cmyers.nappysak.utilityfunction;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.cmyers.nappysak.entities.PreferencedEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NeighborUtilityFunctionTest {

    private NeighborUtilityFunction<PreferencedEntity> uf;
    private NeighborUtilityFunction<PreferencedEntity> ufwrap;

    private PreferencedEntity bobby = new PreferencedEntity("bobby", ImmutableMap.of("sally", 100L));
    private PreferencedEntity sally = new PreferencedEntity("sally", ImmutableMap.of("bobby", 110L));

    private PreferencedEntity john = new PreferencedEntity("john", ImmutableMap.of("jane", 150L));
    private PreferencedEntity jane = new PreferencedEntity("jane", ImmutableMap.of("john", 180L));

    @Before
    public void setup() {
        uf = new NeighborUtilityFunction<PreferencedEntity>(false);
        ufwrap = new NeighborUtilityFunction<PreferencedEntity>(true);
    }

    @Test
    public void testEmpty() {
        Assert.assertEquals((Long) 0L, (Long) uf.evaluate(Lists.newArrayList()));
        Assert.assertEquals((Long) 0L, (Long) ufwrap.evaluate(Lists.newArrayList()));
    }

    @Test
    public void testSingle() {
        Assert.assertEquals((Long) 0L, (Long) uf.evaluate(Lists.newArrayList(john)));
        Assert.assertEquals((Long) 0L, (Long) ufwrap.evaluate(Lists.newArrayList(john)));
    }

    @Test
    public void testTwo() {
        Assert.assertEquals(Long.valueOf(150 + 180), (Long) uf.evaluate(Lists.newArrayList(john, jane)));
        Assert.assertEquals(Long.valueOf(2 * (150 + 180)), (Long) ufwrap.evaluate(Lists.newArrayList(john, jane)));
    }

    @Test
    public void testThree() {
        Assert.assertEquals(Long.valueOf(150 + 180), (Long) uf.evaluate(Lists.newArrayList(john, jane, sally)));
        Assert.assertEquals(Long.valueOf(150 + 180), (Long) ufwrap.evaluate(Lists.newArrayList(john, jane, sally)));
    }

    @Test
    public void testFour() {
        Assert.assertEquals(Long.valueOf(180 + 150 + 110 + 100), (Long) uf.evaluate(Lists.newArrayList(john, jane, sally, bobby)));
        Assert.assertEquals(Long.valueOf(180 + 150 + 110 + 100), (Long) ufwrap.evaluate(Lists.newArrayList(john, jane, sally, bobby)));
    }

    @Test
    public void testFourWithWrap() {
        Assert.assertEquals(Long.valueOf(110 + 100), (Long) uf.evaluate(Lists.newArrayList(jane, sally, bobby, john)));
        Assert.assertEquals(Long.valueOf(180 + 150 + 110 + 100), (Long) ufwrap.evaluate(Lists.newArrayList(jane, sally, bobby, john)));
    }
}
