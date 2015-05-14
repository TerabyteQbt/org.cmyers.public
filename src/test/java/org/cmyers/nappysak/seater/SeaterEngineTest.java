package org.cmyers.nappysak.seater;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.cmyers.nappysak.entities.PreferencedEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public class SeaterEngineTest {

	private SeaterEngine<PreferencedEntity> se;
	
	@Before
	public void setup() {
		se = new SeaterEngine<PreferencedEntity>();
	}
	
	@Test
	public void testPerfectInSmallN() throws InterruptedException {
		// bobby and sally want to sit together
		// jenny and johnny want to sit together
		// bobby and johnny also want to sit together
		PreferencedEntity bobby = new PreferencedEntity("bobby", ImmutableMap.of("sally", 100L, "johnny", 11L));
		PreferencedEntity sally = new PreferencedEntity("sally", ImmutableMap.of("bobby", 101L));
		
		PreferencedEntity jenny = new PreferencedEntity("jenny", ImmutableMap.of("johnny", 103L));
		PreferencedEntity johnny = new PreferencedEntity("johnny", ImmutableMap.of("jenny", 104L, "bobby", 12L));
		
		List<PreferencedEntity> guests = Lists.newArrayList(bobby, sally, johnny, jenny);
		
		Pair<Long, List<PreferencedEntity>> seatedGuests = se.findArrangement(guests);
		// Optimal arrangement is sally, bobby, johnny, jenny (or the reverse of that)
		// The utility of that arrangement is 101 + 100 + 11 + 12 + 103 + 104 = 431
		Assert.assertEquals((Long)431L, (Long)seatedGuests.getLeft());
	}
	
	@Test
	public void testFastInSmallN() throws InterruptedException {
		PreferencedEntity bobby = new PreferencedEntity("bobby", ImmutableMap.of("sally", 100L, "johnny", 11L));
		PreferencedEntity sally = new PreferencedEntity("sally", ImmutableMap.of("bobby", 101L));
		PreferencedEntity jenny = new PreferencedEntity("jenny", ImmutableMap.of("johnny", 103L));
		PreferencedEntity johnny = new PreferencedEntity("johnny", ImmutableMap.of("jenny", 104L, "bobby", 12L));
		
		List<PreferencedEntity> guests = Lists.newArrayList(bobby, sally, johnny, jenny);
		long start = System.nanoTime();
		se.findArrangement(guests);
		long difference = System.nanoTime() - start;
		// should run in under 2s
		Assert.assertTrue(difference < (2L * 1000000000L));
	}
	
	@Test
	public void testLargeEntitySearch() throws InterruptedException {
		List<PreferencedEntity> guests = Lists.newArrayList();
		// make 25 guests, each of whom wants to sit next to the next guest
		for (int i = 0; i < 25; i++) {
			guests.add(new PreferencedEntity("Guest " + i, ImmutableMap.of("Guest " + (i+1), Long.valueOf(100+i))));
		}
		long start = System.nanoTime();
		Pair<Long, List<PreferencedEntity>> seatedGuests = se.findArrangement(guests);
		long difference = System.nanoTime() - start;
		// should run in the full 5s
		Assert.assertTrue(difference > (5L * 1000000000L));
		// Should find a good value
		// Finds 2676
		System.out.println("Found utiltiy: " + seatedGuests.getLeft());
		Assert.assertTrue(seatedGuests.getLeft() > 25*100);
	}
}
