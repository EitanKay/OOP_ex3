package image_char_matching;

import exceptions.EmptyCharSetException;

import java.util.*;


/**
 * This class is used to match characters to their brightness in a sub-image.
 * It is responsible for storing characters and their corresponding brightness values,
 * managing duplicates, and normalizing brightness values.
 *
 * @author Eitan Kayesar and Ariel Monzon
 */
public class SubImgCharMatcher {

	private static final int MINIMUM_LEGAL_CHAR_COUNT = 2;
	private static final RoundMethod DEFAULT_ROUND_METHOD = RoundMethod.ROUND_ABS;

	// A Tree of all Characters
	private TreeMap<Double, TreeSet<Character>> charTree;

	private double maxBrightness;
	private double minBrightness;
	private RoundMethod roundMethod = DEFAULT_ROUND_METHOD;


	/**
	 * Constructor for SubImgCharMatcher.
	 * @param chars An array of initial characters to be added to the matcher.
	 */
	public SubImgCharMatcher(char[] chars) {

		this.charTree = new TreeMap<>();

		for (char c : chars) {
			addChar(c);
		}
	}

	/**
	 * Returns a character from the set that is closest to the given brightness.
	 * @param brightness A brightness value between 0 and 1, where 0 is black and 1 is white.
	 *                   Assumes that the brightnessis normalized.
	 * @return
	 * @throws EmptyCharSetException
	 */
	public char getCharByImageBrightness (double brightness)
			throws EmptyCharSetException {

		if (charTree.size() < MINIMUM_LEGAL_CHAR_COUNT) {
			throw new EmptyCharSetException("Did not execute. Charset is too small.");
		}

		Double closest = null;

		Double lowerBrightness = charTree.floorKey(brightness);
		Double higherBrightness = charTree.ceilingKey(brightness);

		// Handle edge cases where brightness is for some reason not normalized
		if (brightness > 1) {
			brightness = 1;
		} else if (brightness < 0) {
			brightness = 0;
		}

		switch (roundMethod) {
			case ROUND_UP -> closest = higherBrightness;
			case ROUND_DOWN -> closest = lowerBrightness;
			default -> closest = (Math.abs(brightness - lowerBrightness)
					<= Math.abs(brightness - higherBrightness)) ? lowerBrightness : higherBrightness;
		}

		return charTree.get(closest).first();
	}

	/**
	 * Adds a character to the the character set.
	 * @param c The character to be added.
	 */
	public void addChar (char c) {

		double nonNormalizedBrightness = getNonNormalizedBrightness(c);

		if (charTree.isEmpty()) {
			maxBrightness = minBrightness = getNonNormalizedBrightness(c);
		}

		if (nonNormalizedBrightness > maxBrightness){
			maxBrightness = nonNormalizedBrightness;
			normalizeTree();
		}

		if (nonNormalizedBrightness < minBrightness){
			minBrightness = nonNormalizedBrightness;
			normalizeTree();
		}

		double normalizedBrightness = normalizeBrightness(nonNormalizedBrightness);
		if (!charTree.containsKey(normalizedBrightness)) {
			charTree.put(normalizedBrightness, new TreeSet<Character>());
		}

		charTree.get(normalizedBrightness).add(c);
	}

	/**
	 * Removes a character from the character set.
	 * @param c The character to be removed.
	 */
	public void removeChar(char c) {

		// find the char
		Double brightness = null;
		for (Map.Entry<Double, TreeSet<Character>> entry : charTree.entrySet()) {
			for (Character character : entry.getValue()) {
				if (character == c) {
					brightness = entry.getKey();
					break;
				}
			}
		}

		if (brightness == null){
			return;
		}

		// properly delete the char
		charTree.get(brightness).remove(c);
		if (charTree.get(brightness).isEmpty()) {
			boolean wasLowest = brightness.equals(charTree.firstKey());
			boolean wasHighest = brightness.equals(charTree.lastKey());

			charTree.remove(brightness);
			if (charTree.isEmpty()) { return; }
			if (wasHighest) {
				maxBrightness = getNonNormalizedBrightness(charTree.lastEntry().getValue().first());
				normalizeTree();
			} else if (wasLowest) {
				minBrightness = getNonNormalizedBrightness(charTree.firstEntry().getValue().first());
				normalizeTree();
			}
		}
	}

	/**
	 * Prints all characters in the character set.
	 */
	public void printChars() {
		TreeSet<Character> charSet = new TreeSet<>();

		for (TreeSet<Character> set : charTree.values()) {
			charSet.addAll(set);
		}

		for (Character c : charSet) {
			System.out.print(c + " ");
		}

		System.out.println();
	}

	/**
	 * Normalizes the brightness values of all characters in the character set,
	 * adjusting them to a range between 0 and 1. must be called after adding or
	 * removing characters that change the brightness range.
	 */
	private void normalizeTree() {
		TreeMap<Double, TreeSet<Character>> newTree = new TreeMap<>();
		for (Map.Entry<Double, TreeSet<Character>> entry : charTree.entrySet()) {
			double newBrightness = normalizeBrightness(
									getNonNormalizedBrightness(entry.getValue().first()));
			newTree.put(newBrightness, entry.getValue());
		}

		charTree = newTree;
	}

	/**
	 * Normalizes the brightness value to a range between 0 and 1.
	 * @param nonNormalizedBrightness The brightness value to be normalized.
	 * @return The normalized brightness value.
	 */
	private double normalizeBrightness(double nonNormalizedBrightness) {

		if (minBrightness == maxBrightness) {
			return nonNormalizedBrightness;
		}

		return (nonNormalizedBrightness - minBrightness)
				/ (maxBrightness - minBrightness);

	}

/**
	 * Calculates the non-normalized brightness of a character.
	 * The brightness is calculated as the ratio of white pixels to total pixels.
	 * @param c The character for which to calculate the brightness.
	 * @return The non-normalized brightness value.
	 */
	private double getNonNormalizedBrightness(char c) {

		boolean[][] boolArray = CharConverter.convertToBoolArray(c);
		double blackPixels = 0;
		double whitePixels = 0;

		for (int i = 0; i < boolArray.length; i++) {
			for (int j = 0; j < boolArray[0].length; j++) {
				if (boolArray[i][j]){
					whitePixels++;
				} else {
					blackPixels++;
				}
			}
		}

		return whitePixels / (whitePixels + blackPixels);

	}

	/**
	 * Sets the method used for rounding brightness values.
	 * @param newMethod The new rounding method to be used.
	 */
	public void setRoundMethod(RoundMethod newMethod) {
		this.roundMethod = newMethod;
	}
}
