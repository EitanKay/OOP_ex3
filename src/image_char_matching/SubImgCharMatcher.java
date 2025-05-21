package image_char_matching;

import exceptions.EmptyCharSetException;

import java.util.*;

public class SubImgCharMatcher {

	private static final int MINIMUM_LEGAL_CHAR_COUNT = 2;
	private static final RoundMethod DEFAULT_ROUND_METHOD = RoundMethod.ROUND_ABS;

	// A Tree of all Characters
	private TreeMap<Double, TreeSet<Character>> charTree;

	private double maxBrightness;
	private double minBrightness;
	private RoundMethod roundMethod = DEFAULT_ROUND_METHOD;


	public SubImgCharMatcher(char[] chars) {

		this.charTree = new TreeMap<>();

		for (char c : chars) {
			addChar(c);
		}
	}

	public char getCharByImageBrightness (double brightness)
			throws EmptyCharSetException {

		if (charTree.size() < MINIMUM_LEGAL_CHAR_COUNT) {
			throw new EmptyCharSetException("Did not execute. Charset is too small.");
		}

		// TODO: check if this is redundant
		if (charTree.size() == 1){
			return charTree.firstEntry().getValue().first();
		}

		Double closest = null;

		Double lowerBrightness = charTree.floorKey(brightness);
		Double higherBrightness = charTree.ceilingKey(brightness);

		switch (roundMethod) {
			case ROUND_UP -> closest = higherBrightness;
			case ROUND_DOWN -> closest = lowerBrightness;
			default -> closest = (Math.abs(brightness - lowerBrightness)
					<= Math.abs(brightness - higherBrightness)) ? lowerBrightness : higherBrightness;
		}

		return charTree.get(closest).first();
	}

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
		// TODO: check that chars are sorted in the correct order.
	}

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

	private void normalizeTree() {
		TreeMap<Double, TreeSet<Character>> newTree = new TreeMap<>();
		for (Map.Entry<Double, TreeSet<Character>> entry : charTree.entrySet()) {
			double newBrightness = normalizeBrightness(
									getNonNormalizedBrightness(entry.getValue().first()));
			newTree.put(newBrightness, entry.getValue());
		}

		charTree = newTree;
	}

	private double normalizeBrightness(double nonNormalizedBrightness) {

		if (minBrightness == maxBrightness) {
			return nonNormalizedBrightness;
		}

		return (nonNormalizedBrightness - minBrightness)
				/ (maxBrightness - minBrightness);

	}

	private double getNonNormalizedBrightness(char c) {

		boolean[][] boolArray = CharConverter.convertToBoolArray(c);
		// TODO: figure out black/white
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

	public void setRoundMethod(RoundMethod newMethod) {
		this.roundMethod = newMethod;
	}
}
