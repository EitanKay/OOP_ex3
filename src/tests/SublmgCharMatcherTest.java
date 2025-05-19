package tests;

import exceptions.EmptyCharSetException;
import exceptions.IllegalBrightnessException;
import image_char_matching.RoundMethod;
import image_char_matching.SubImgCharMatcher;

public class SublmgCharMatcherTest {

	public static void main(String[] args) {

		char char1 = '.';
		char char2 = '0';
		char char3 = '/';

		System.out.println("Creating charMathcher class...");
		SubImgCharMatcher charMatcher = new SubImgCharMatcher(new char[]{char1, char2});

		System.out.println("Trying to get char...");

		char outChar = charMatcher.getCharByImageBrightness(0.6);
		System.out.println("Brightness of 0.6 gave char " + outChar);

		outChar = charMatcher.getCharByImageBrightness(0.4);
		System.out.println("Brightness of 0.4 gave char " + outChar);

		System.out.println("setting round function to down....");
		charMatcher.setRoundMethod(RoundMethod.ROUND_DOWN);
		outChar = charMatcher.getCharByImageBrightness(0.6);
		System.out.println("Brightness of 0.5 gave char " + outChar);

		outChar = charMatcher.getCharByImageBrightness(0.4);
		System.out.println("Brightness of 0.4 gave char " + outChar);

		System.out.println("setting round function to up....");
		charMatcher.setRoundMethod(RoundMethod.ROUND_UP);
		outChar = charMatcher.getCharByImageBrightness(0.6);
		System.out.println("Brightness of 0.5 gave char " + outChar);

		outChar = charMatcher.getCharByImageBrightness(0.4);
		System.out.println("Brightness of 0.4 gave char " + outChar);

		System.out.println("resetting round function...");
		charMatcher.setRoundMethod(RoundMethod.ROUND_ABS);


		System.out.println("Adding char "+ char3);
		charMatcher.addChar(char3);

		for (double d = 0 ; d <= 1; d+=0.1) {
			outChar = charMatcher.getCharByImageBrightness(d);
			System.out.println("Brightness of "+d+" gave char " + outChar);
		}


		System.out.println("Adding many occurences of the same char");
		for (int i = 0; i < 100; i++) {
			charMatcher.addChar(char1);
		}

		for (double d = 0 ; d <= 1; d+=0.2) {
			outChar = charMatcher.getCharByImageBrightness(d);
			System.out.println("Brightness of "+ d + " gave char " + outChar);
		}


		System.out.println("\nAttempting to delete... " + char1);
		charMatcher.removeChar(char1);
		for (double d = 0 ; d <= 1; d+=0.1) {
			outChar = charMatcher.getCharByImageBrightness(d);
			System.out.println("Brightness of "+ d + " gave char " + outChar);
		}

		System.out.println("\nAttempting to delete non existing item....\n");
		charMatcher.removeChar(char1);

		System.out.println("\nAttempting to delete all items....\n");

		charMatcher.removeChar(char2);
		charMatcher.removeChar(char3);

		System.out.println("\nAttempting to get a char with empty charlist....\n");

		charMatcher = new SubImgCharMatcher(new char[]{});
		try {
			charMatcher.getCharByImageBrightness(0.5);
		} catch (EmptyCharSetException e) {
			System.out.println("Succesfuly caught exception!");;
		}

		System.out.println("\nAttempting to access a non normalized brightness\n");

		charMatcher = new SubImgCharMatcher(new char[]{char1, char3});
		try {
			charMatcher.getCharByImageBrightness(1.5);
		} catch (IllegalBrightnessException e) {
			System.out.println("Succesfuly caught exception!");;
		}
		System.out.println("Finished all tests!!");

	}

}
