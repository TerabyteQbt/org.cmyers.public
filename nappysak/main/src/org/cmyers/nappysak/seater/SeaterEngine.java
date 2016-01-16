package org.cmyers.nappysak.seater;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.cmyers.nappysak.entities.PreferencedEntity;
import org.cmyers.nappysak.permutationengine.PermutationEngine;
import org.cmyers.nappysak.util.ReturnValue;
import org.cmyers.nappysak.utilityfunction.NeighborUtilityFunction;
import org.cmyers.nappysak.utilityfunction.UtilityFunction;

/**
 * This class provides the engine for searching for good seating arrangements for PreferencedEntities, a la the knapsack problem.
 *
 * @author cmyers
 *
 */
public class SeaterEngine<T extends PreferencedEntity> {

    private final ExecutorService es;
    private final int scaleFactor;
    private final int ncpus;
    private final int timeoutInMillis;
    private final UtilityFunction<Long, T> uf;
    private final PermutationEngine<T> randomizer;
    private final PermutationEngine<T> wiggler;

    public SeaterEngine() {
        ncpus = Runtime.getRuntime().availableProcessors();
        scaleFactor = 2;
        es = Executors.newFixedThreadPool(ncpus * scaleFactor);
        timeoutInMillis = 5000;
        uf = new NeighborUtilityFunction<T>(false);
        randomizer = PermutationEngine.getDefaultRandomizerEngine();
        wiggler = PermutationEngine.getDefaultPermuterEngine();
    }

    public Pair<Long, List<T>> findArrangement(List<T> list) throws InterruptedException {

        // number of swaps to perform without finding a better value before picking a new random starting point.
        // i.e. a list of 100 items would try 10,000 swaps
        // TODO: tune this?
        final long searchDepth = list.size() * list.size();
        // 20! is the largest factorial that fits in a long.
        // Long.MAX_VALUE =~ 9.2e18
        // 20! =~ 2.4e18
        // 2*20! < Long.MAX_VALUE
        // if N = 6, we want to do more than just 6! searches, so we make it 2*N! as the cutoff.
        long x;
        try {
            x = CombinatoricsUtils.factorial(list.size()) * 2;
        }
        catch(MathArithmeticException e) {
            // n! > MAX_LONG
            x = Long.MAX_VALUE;
        }
        final long maxSearch = x;

        List<ReturnValue<Pair<Long, List<T>>>> returnValues = Lists.newArrayList();
        List<Callable<Void>> callables = Lists.newArrayList();
        for(int i = 0; i < (ncpus * scaleFactor); i++) {
            ReturnValue<Pair<Long, List<T>>> ret = ReturnValue.create();
            returnValues.add(ret);
            callables.add(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    try {
                        doSearch(list, searchDepth, maxSearch, ret);
                    }
                    catch(InterruptedException e) {
                        // nothing to do
                    }
                    return null;
                }

            });
        }
        // if list.size() is small, we might finish an actual exhaustive search within the timeout.
        // invokeAll blocks until everything is done *or* timeout
        es.invokeAll(callables, timeoutInMillis, TimeUnit.MILLISECONDS);

        // calculate the best value across all the threads
        Collections.sort(returnValues, new Comparator<ReturnValue<Pair<Long, List<T>>>>() {

            @Override
            public int compare(ReturnValue<Pair<Long, List<T>>> o1, ReturnValue<Pair<Long, List<T>>> o2) {
                return o1.getValue().getLeft().compareTo(o2.getValue().getLeft());
            }

        });
        return returnValues.get(returnValues.size() - 1).getValue();
    }

    // Do the actual search - Return value is the best utility found so far and the list that has that utility.
    // this function runs forever and will need to be interrupted.
    private void doSearch(List<T> list, long searchDepth, long maxSearch, ReturnValue<Pair<Long, List<T>>> returnValue) throws InterruptedException {
        // ensure the return value is initialized with a proper utility measure.
        returnValue.setValue(Pair.of(uf.evaluate(list), list));
        long absoluteCount = 0;
        // count of iterations since finding a better value or resetting
        long count = 0;
        // calculate a new permutation which is very random
        list = randomizer.next(list);
        while(true) {
            absoluteCount++;
            count++;
            if(Thread.interrupted()) {
                // System.out.println("I was interrupted!");
                throw new InterruptedException();
            }
            if(absoluteCount > maxSearch) {
                // System.out.println("hit max search");
                return;
            }
            Long utility = uf.evaluate(list);
            // System.out.println("Evaluated sort (" + utility + ")" + list.toString());
            if(utility > returnValue.getValue().getLeft()) {
                // found a new best value
                returnValue.setValue(Pair.of(utility, list));
                count = 0; // reset count "since we found an improvement"
                // try a similar thing
                list = wiggler.next(list);
                continue;
            }
            // didn't find something better
            if(count < searchDepth) {
                // try something similar
                list = wiggler.next(list);
                continue;
            }
            // we've reached our maximum number of tries, start over from a new random place
            count = 0;
            list = randomizer.next(list);
        }
    }
}
