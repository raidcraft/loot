package de.raidcraft.loot.util;

import java.util.*;

public class RandomUtil {

    private static Random random = null;

    static {
        setRandomizer(null);
    }

    /**
     * You may replace the randomizer used by calling the SetRandomizer method with any object derived from Random.
     * Supply NULL to SetRandomizer to reset it to the default RNGCryptoServiceProvider.
     *
     * @param randomizer to use.
     */
    public static void setRandomizer(Random randomizer) {

        random = Objects.requireNonNullElseGet(randomizer, Random::new);
    }

    /**
     * Retrieves the next random value from the random number generator.
     * The result is always between 0.0 and the given max-value (excluding).
     *
     * @param max value
     *
     * @return random double value
     */
    public static double getDoubleValue(double max) {

        return random.nextDouble() * max;
    }

    /**
     * Retrieves the next double random value from the random number generator.
     * The result is always between the given min-value (including) and the given max-value (excluding).
     *
     * @param min value
     * @param max value
     * @return random double value
     */
    public static double getDoubleValue(double min, double max) {

        return min + random.nextDouble() * (max - min);
    }

    /**
     * Retrieves the next integer random value from the random number generator.
     * The result is always between 0 (including) and the given max-value (excluding).
     *
     * @param max value
     * @return random integer value
     */
    public static int getIntValue(int max) {

        return random.nextInt(max);
    }

    /**
     * Retrieves the next integer random value from the random number generator.
     * The result is always between the given min-value (including) and the given max-value (excluding).
     *
     * @param min value
     * @param max value
     * @return random integer value
     */
    public static int getIntValue(int min, int max) {

        return random.nextInt((max - min) + 1) + min;
    }

    public static int getIntNegativePositiveValue(int min, int max) {

        int diff = max - min;

        return getIntValue(diff) + min;
    }

    /**
     * Simulates a dice roll with a given number of dice and a given number of sides per dice.
     * The result is an IEnumberable of integers, where the first element (index 0) contains the sum
     * of all dice rolled and all subsequent elements are the numbers rolled.
     *
     * @param diceCount of the dice
     * @param sidesPerDice of the dice
     * @return A collection of integers, where the first element (index 0) contains the sum
     * of all dice rolled and all subsequent elements are the numbers rolled.
     */
    public static Collection<Integer> rollDice(int diceCount, int sidesPerDice) {

        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < diceCount; i++)
            result.add(getIntValue(1, sidesPerDice + 1));

        result.add(0, result.stream().mapToInt(value -> value).sum());

        return result;
    }

    /**
     * Determines whether a given percent chance is hit.
     * Example: If you have a 3.5% chance of something happening, use this method
     * as "if (IsPercentHit(0.035)) ...".
     *
     * @param percent Value must be between 0.00 and 1.00.
     *                Negative values will always result in a false return.
     * @return <code>true</code> if [is percent hit] [the specified percent]; otherwise, <code>false</code>
     */
    public static boolean isPercentHit(double percent) {

        return (random.nextDouble() < percent);
    }
}
