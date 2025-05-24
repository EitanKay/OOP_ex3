package image_char_matching;


/**
 * This enum is used in the SubImgCharMatcher class to determine how brightness values
 * ROUND_UP rounds to the nearest higher value,
 * ROUND_DOWN rounds to the nearest lower value,
 * ROUND_ABS rounds to the nearest absolute value.
 *
 * @author Eitan Kayesar and Ariel Monzon
 */
public enum RoundMethod {
	/** Rounds to the nearest higher brightness value. */
	ROUND_UP,

	/** Rounds to the nearest lower brightness value. */
	ROUND_DOWN,

	/** Rounds to the nearest absolute (closest) brightness value. */
	ROUND_ABS
}