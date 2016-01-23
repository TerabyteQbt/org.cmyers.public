package org.cmyers.games.threes.random;

import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Based heavily off of @{link java.util.Random}, but with immutable internal structures.
 *
 * To use this PRNG, you would do something like this:
 *
 * ImmutablePrng prng = ImmutablePrng.of();
 * Pair<Integer, ImmutablePrng> first = prng.nextInt();
 * Pair<Integer, ImmutablePrng> second = first.getRight().nextInt();
 * and so on...
 *
 * If you call prng.nextInt() twice in a row on the same prng object, you will get the
 * same number, so you must always update your reference to the PRNG.
 *
 * @author cmyers
 */
public class ImmutablePrng {
    // only used to help ensure that multiple calls of no-arg ctor yield vastly different results
    private static final AtomicLong seedUniquifier = new AtomicLong(8682522807148012L);

    /**
     * The internal state of the PRNG is entirely held by this Long value.
     * 
     * By being immutable, a ton of their nasty AtomicLong code gets to die.
     */
    private final Long seed;

    private static final long multiplier = 0x5DEECE66DL;
    private static final long addend = 0xBL;
    private static final long mask = (1L << 48) - 1;
    private static final double DOUBLE_UNIT = 0x1.0p-53; // 1.0 / (1L << 53)

    /**
     * Creates a new random number generator. This constructor sets
     * the seed of the random number generator to a value very likely
     * to be distinct from any other invocation of this constructor.
     */
    public static ImmutablePrng of() {
        return new ImmutablePrng();
    }

    /**
     * Creates a prng with the next seed in the sequence after the given seed.
     */
    public static ImmutablePrng next(long seed) {
        return new ImmutablePrng(nextSeed(seed));
    }

    // Manually retrieve the next Prng in sequence (would have been the right() value in the next getInt() call
    public ImmutablePrng next() {
        return new ImmutablePrng(nextSeed(seed));
    }

    /**
     * Creates a new random number generator using a single {@code long} seed.
     */
    public static ImmutablePrng of(long seed) {
        return new ImmutablePrng(initialScramble(seed));
    }

    private ImmutablePrng() {
        this(System.nanoTime() ^ seedUniquifier());
    }

    private static long seedUniquifier() {
        // L'Ecuyer, "Tables of Linear Congruential Generators of
        // Different Sizes and Good Lattice Structure", 1999
        for(;;) {
            long current = seedUniquifier.get();
            long next = current * 181783497276652981L;
            if(seedUniquifier.compareAndSet(current, next))
                return next;
        }
    }

    // no scramble here, used for deserialization
    private ImmutablePrng(long seed) {
        this.seed = seed;
    }

    private static long initialScramble(long seed) {
        return (seed ^ multiplier) & mask;
    }

    private static long nextSeed(long seed) {
        return (seed * multiplier + addend) & mask;
    }

    /**
     * Generates the next pseudorandom number. Subclasses should
     * override this, as this is used by all other methods.
     *
     * <p>
     * The general contract of {@code next} is that it returns an
     * {@code Integer} value and if the argument {@code bits} is between
     * {@code 1} and {@code 32} (inclusive), then that many low-order
     * bits of the returned value will be (approximately) independently
     * chosen bit values, each of which is (approximately) equally
     * likely to be {@code 0} or {@code 1}. The returned ImmutablePrng returned along with the random value has a seed related to this object's seed by the following transformation:
     *
     * <pre>
     * {@code (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1)}
     * </pre>
     * 
     * and the random value returned is:
     * 
     * <pre>
     * {@code (int)(seed >>> (48 - bits))}
     * </pre>
     *
     * This is a linear congruential pseudorandom number generator, as
     * defined by D. H. Lehmer and described by Donald E. Knuth in
     * <i>The Art of Computer Programming,</i> Volume 3:
     * <i>Seminumerical Algorithms</i>, section 3.2.1., and implemented exactly like {@link java.utils.Random}.
     *
     * @param bits
     *            random bits
     * @return the next pseudorandom value from this random number
     *         generator's sequence
     */
    protected Pair<Integer, ImmutablePrng> next(int bits) {
        return Pair.of((int) (this.seed >>> (48 - bits)), this.next());
    }

    /**
     * Generates random bytes and places them into a user-supplied
     * byte array. The number of random bytes produced is equal to
     * the length of the byte array.
     *
     * <p>
     * The method {@code nextBytes} is implemented as if by:
     *
     * <pre>
     *  {@code
     * public void nextBytes(byte[] bytes) {
     *   for (int i = 0; i < bytes.length; )
     *     for (int rnd = nextInt(), n = Math.min(bytes.length - i, 4);
     *          n-- > 0; rnd >>= 8)
     *       bytes[i++] = (byte)rnd;
     * }}
     * </pre>
     * 
     * Except for the seed of this object remains unchanged and a new
     * ImmutablePrng object is returned instead which has the new seed and should be used for future calls.
     *
     * @param bytes
     *            the byte array to fill with random bytes
     * @throws NullPointerException
     *             if the byte array is null
     */
    public ImmutablePrng nextBytes(byte[] bytes) {
        Pair<Integer, ImmutablePrng> p = Pair.of(0, this);

        for(int i = 0, len = bytes.length; i < len;) {
            p = p.getRight().nextInt();
            int rnd = p.getLeft();
            int n = Math.min(len - i, Integer.SIZE / Byte.SIZE);
            while(n-- > 0) {
                rnd >>= Byte.SIZE;
                bytes[i++] = (byte) rnd;
            }
        }
        return p.getRight();
    }

    /**
     * Returns the next pseudorandom, uniformly distributed {@code int}
     * value from this random number generator's sequence. The general
     * contract of {@code nextInt} is that one {@code Integer} value is
     * pseudorandomly generated and returned. All 2<sup>32</sup> possible
     * {@code Integer} values are produced with (approximately) equal probability.
     *
     * @return the next pseudorandom, uniformly distributed {@code Integer}
     *         value from this random number generator's sequence and a new
     *         ImmutablePrng with a seed ready to generate the next {@code Integer}
     *         of the sequence
     */
    public Pair<Integer, ImmutablePrng> nextInt() {
        return next(32);
    }

    /**
     * Returns a pseudorandom, uniformly distributed {@code Integer} value
     * between 0 (inclusive) and the specified value (exclusive), drawn from
     * this random number generator's sequence. The general contract of
     * {@code nextInt} is that one {@code Integer} value in the specified range
     * is pseudorandomly generated and returned. All {@code bound} possible
     * {@code Integer} values are produced with (approximately) equal
     * probability. The method {@code nextInt(int bound)} is implemented
     * as if by:
     * 
     * <pre>
     *  {@code
     * public int nextInt(int bound) {
     *   if (bound <= 0)
     *     throw new IllegalArgumentException("bound must be positive");
     *
     *   if ((bound & -bound) == bound)  // i.e., bound is a power of 2
     *     return (int)((bound * (long)next(31)) >> 31);
     *
     *   int bits, val;
     *   do {
     *       bits = next(31);
     *       val = bits % bound;
     *   } while (bits - val + (bound-1) < 0);
     *   return val;
     * }}
     * </pre>
     * 
     * Except for the seed of this object remains unchanged and a new
     * ImmutablePrng object is returned instead which has the new seed and should be used for future calls.
     * <p>
     * The hedge "approximately" is used in the foregoing description only
     * because the next method is only approximately an unbiased source of
     * independently chosen bits. If it were a perfect source of randomly
     * chosen bits, then the algorithm shown would choose {@code int}
     * values from the stated range with perfect uniformity.
     * <p>
     * The algorithm is slightly tricky. It rejects values that would result
     * in an uneven distribution (due to the fact that 2^31 is not divisible
     * by n). The probability of a value being rejected depends on n. The
     * worst case is n=2^30+1, for which the probability of a reject is 1/2,
     * and the expected number of iterations before the loop terminates is 2.
     * <p>
     * The algorithm treats the case where n is a power of two specially: it
     * returns the correct number of high-order bits from the underlying
     * pseudo-random number generator. In the absence of special treatment,
     * the correct number of <i>low-order</i> bits would be returned. Linear
     * congruential pseudo-random number generators such as the one
     * implemented by this class are known to have short periods in the
     * sequence of values of their low-order bits. Thus, this special case
     * greatly increases the length of the sequence of values returned by
     * successive calls to this method if n is a small power of two.
     *
     * @param bound
     *            the upper bound (exclusive). Must be positive.
     * @return the next pseudorandom, uniformly distributed {@code int}
     *         value between zero (inclusive) and {@code bound} (exclusive)
     *         from this random number generator's sequence
     * @throws IllegalArgumentException
     *             if bound is not positive
     */
    public Pair<Integer, ImmutablePrng> nextInt(int bound) {
        if(bound <= 0)
            throw new IllegalArgumentException("bound must be positive");

        Pair<Integer, ImmutablePrng> p = next(31);
        int r = p.getLeft();
        int m = bound - 1;
        if((bound & m) == 0) // i.e., bound is a power of 2
            r = (int) ((bound * (long) r) >> 31);
        else {
            for(int u = r; u - (r = u % bound) + m < 0;) {
                p = p.getRight().next(31);
                u = p.getLeft();
            }
        }
        return Pair.of(r, p.getRight());
    }

    /**
     * Returns the next pseudorandom, uniformly distributed {@code long}
     * value from this random number generator's sequence. The general
     * contract of {@code nextLong} is that one {@code long} value is
     * pseudorandomly generated and returned.
     *
     * <p>
     * The method {@code nextLong} is implemented by class {@code Random}
     * as if by:
     * 
     * <pre>
     *  {@code
     * public long nextLong() {
     *   return ((long)next(32) << 32) + next(32);
     * }}
     * </pre>
     *
     * Except for the seed of this object remains unchanged and a new
     * ImmutablePrng object is returned instead which has the new seed and should be used for future calls.
     *
     * Because this class uses a seed with only 48 bits,
     * this algorithm will not return all possible {@code long} values.
     *
     * @return the next pseudorandom, uniformly distributed {@code long}
     *         value from this random number generator's sequence
     */
    public Pair<Long, ImmutablePrng> nextLong() {
        // it's okay that the bottom word remains signed.
        Pair<Integer, ImmutablePrng> p = next(32);
        Pair<Integer, ImmutablePrng> p2 = p.getRight().next(32);
        return Pair.of((((long) (p.getLeft())) << 32) + p2.getLeft(), p2.getRight());
    }

    /**
     * Returns the next pseudorandom, uniformly distributed
     * {@code boolean} value from this random number generator's
     * sequence. The general contract of {@code nextBoolean} is that one
     * {@code boolean} value is pseudorandomly generated and returned. The
     * values {@code true} and {@code false} are produced with
     * (approximately) equal probability.
     *
     * <p>
     * The method {@code nextBoolean} is implemented as if by:
     * 
     * <pre>
     *  {@code
     * public boolean nextBoolean() {
     *   return next(1) != 0;
     * }}
     * </pre>
     * 
     * Except for the seed of this object remains unchanged and a new
     * ImmutablePrng object is returned instead which has the new seed and should be used for future calls.
     *
     * @return the next pseudorandom, uniformly distributed
     *         {@code boolean} value from this random number generator's
     *         sequence
     */
    public Pair<Boolean, ImmutablePrng> nextBoolean() {
        Pair<Integer, ImmutablePrng> p = next(1);
        return Pair.of(p.getLeft() != 0, p.getRight());
    }

    /**
     * Returns the next pseudorandom, uniformly distributed {@code float}
     * value between {@code 0.0} and {@code 1.0} from this random
     * number generator's sequence.
     *
     * <p>
     * The general contract of {@code nextFloat} is that one
     * {@code float} value, chosen (approximately) uniformly from the
     * range {@code 0.0f} (inclusive) to {@code 1.0f} (exclusive), is
     * pseudorandomly generated and returned. All 2<sup>24</sup> possible
     * {@code float} values of the form <i>m&nbsp;x&nbsp;</i>2<sup>-24</sup>,
     * where <i>m</i> is a positive integer less than 2<sup>24</sup>, are
     * produced with (approximately) equal probability.
     *
     * <p>
     * The method {@code nextFloat} is implemented as if by:
     * 
     * <pre>
     *  {@code
     * public float nextFloat() {
     *   return next(24) / ((float)(1 << 24));
     * }}
     * </pre>
     * 
     * Except for the seed of this object remains unchanged and a new
     * ImmutablePrng object is returned instead which has the new seed and should be used for future calls.
     *
     * <p>
     * The hedge "approximately" is used in the foregoing description only
     * because the next method is only approximately an unbiased source of
     * independently chosen bits. If it were a perfect source of randomly
     * chosen bits, then the algorithm shown would choose {@code float}
     * values from the stated range with perfect uniformity.
     * <p>
     * 
     * @return the next pseudorandom, uniformly distributed {@code float}
     *         value between {@code 0.0} and {@code 1.0} from this
     *         random number generator's sequence
     */
    public Pair<Float, ImmutablePrng> nextFloat() {
        Pair<Integer, ImmutablePrng> p = next(24);
        return Pair.of(p.getLeft() / ((float) (1 << 24)), p.getRight());
    }

    /**
     * Returns the next pseudorandom, uniformly distributed
     * {@code double} value between {@code 0.0} and
     * {@code 1.0} from this random number generator's sequence.
     *
     * <p>
     * The general contract of {@code nextDouble} is that one
     * {@code double} value, chosen (approximately) uniformly from the
     * range {@code 0.0d} (inclusive) to {@code 1.0d} (exclusive), is
     * pseudorandomly generated and returned.
     *
     * <p>
     * The method {@code nextDouble} is implemented as if by:
     * 
     * <pre>
     *  {@code
     * public double nextDouble() {
     *   return (((long)next(26) << 27) + next(27))
     *     / (double)(1L << 53);
     * }}
     * </pre>
     * 
     * Except for the seed of this object remains unchanged and a new
     * ImmutablePrng object is returned instead which has the new seed and should be used for future calls.
     *
     * <p>
     * The hedge "approximately" is used in the foregoing description only
     * because the {@code next} method is only approximately an unbiased
     * source of independently chosen bits. If it were a perfect source of
     * randomly chosen bits, then the algorithm shown would choose
     * {@code double} values from the stated range with perfect uniformity.
     * <p>
     * 
     * @return the next pseudorandom, uniformly distributed {@code double}
     *         value between {@code 0.0} and {@code 1.0} from this
     *         random number generator's sequence
     */
    public Pair<Double, ImmutablePrng> nextDouble() {
        Pair<Integer, ImmutablePrng> p1 = next(26);
        Pair<Integer, ImmutablePrng> p2 = p1.getRight().next(27);
        return Pair.of((((long) (p1.getLeft()) << 27) + p2.getLeft()) * DOUBLE_UNIT, p2.getRight());
    }

    // TODO: removed gaussian methods, don't need them yet and they look messy (have internal state)
    // TODO: streams appear stupid and I shall not implement them.

    // used for serialization and deserialization
    public long getSeed() {
        return seed;
    }

    /**
     * This is used to deserialize a serialized ImmutablePrng (all state amounts to only the value of the seed).
     * 
     * ImmutablePrng.of(seed) scrambles the seed, this method uses it untouched, so use this only for constructing an ImmutablePrng with a specific seed value.
     */
    public static ImmutablePrng raw(long seed) {
        return new ImmutablePrng(seed);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof ImmutablePrng)) {
            return false;
        }
        return this.seed.equals(((ImmutablePrng) obj).seed);
    }

    @Override
    public int hashCode() {
        return seed.intValue();
    }

    @Override
    public String toString() {
        return "[PRNG:" + seed + "]";
    }
}
