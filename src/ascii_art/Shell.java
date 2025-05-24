package ascii_art;

import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import exceptions.EmptyCharSetException;
import exceptions.IllegalResolutionException;
import image.Image;
import image.ImageProcessor;
import image_char_matching.RoundMethod;
import image_char_matching.SubImgCharMatcher;
import exceptions.IllegalFormatException;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * This class represents a shell for the ASCII art application.
 * It is the location of the main method of the program. The Shell class is responsible for collecting
 * user input, processing commands, and managing the ASCII art generation process.
 *
 * @author Eitan Kayesar and Ariel Monzon
 */
public class Shell {

	private static final String NEXT_INPUT_MSG = ">>> ";
	private static final String EXIT_INPUT = "exit";
	private static final String SPACEBAR_STRING = " ";
	private static final String RUN_ASCII_ART_INPUT = "asciiArt";
	private static final String CONSOLE_ASCII_OUTPUT_STR = "console";
	private static final String HTML_ASCII_OUTPUT_STR = "html";
	// Error messages
	private static final String INVALID_IMG_PATH_MSG =
			"Invalid Image Path, try running the program again with a valid path.";
	private static final String INCORRECT_INPUT_FORMAT_MSG
									= "Did not execute due to incorrect command.";
	private static final String INCORRECT_ASCII_OUTPUT_FORMAT_EXCEPTION
									= "Did not change output method due to incorrect format.";
	private static final String RESOLUTION_FORMAT_EXCEPTION_MESSAGE
									= "Did not change resolution due to incorrect format.";
	private static final String EXCEEDING_BOUNDRIES_EXCEPTION_MESSEGE
									= "Did not change resolution due to exceeding boundaries.";
	private static final String ROUNDING_METHOD_FORMAT_EXCEPTION
									= "Did not change rounding method due to incorrect format.";
	private static final String ADD_REMOVE_ERROR_MESSAGE = "Did not %s due to incorrect format.";


	// defaults
	private static final char[] DEFAULT_CHAR_DATABASE = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
	private static final int DEFAULT_RESOLUTION = 2;
	private static final String DEFAULT_ASCII_OUTPUT = CONSOLE_ASCII_OUTPUT_STR;

	// output settings
	private static final String HTML_OUT_FILE_SRC = "out.html";
	private static final String OUT_FONT_NAME = "Courier New";
	private static final String ASCII_OUTPUT_INPUT_STR = "output";
	private static final int OUTPUT_TYPE_ARG_HOLDER = 1;

	// add/remove char settings
	private static final int CHAR_HANDLE_ARG = 1;
	private static final int CHAR_LENGTH = 1;
	private static final String CHARS_INPUT = "chars";
	private static final String ADD_INPUT_STR = "add";
	private static final String SPACE_INPUT_STRING = "space";
	private static final char FIRST_LEGAL_CHAR = ' ';
	private static final char LAST_LEGAL_CHAR = '~';
	private static final char HYPHEN_CHAR = '-';
	private static final String ALL_INPUT_STRING = "all";
	private static final int CHAR_RANGE_ARG_LENGTH = 3;
	private static final int CHAR_RANGE_HYPHEN_LOC = 1;
	private static final int CHAR_RANGE_FIRST_CHAR_LOC = 0;
	private static final int CHAR_RANGE_LAST_CHAR_LOC = 2;
	private static final String REMOVE_INPUT_STR = "remove";

	// res settings
	private static final String RES_INPUT_STR = "res";
	private static final String ROUND_UP_STR = "up";
	private static final String ROUND_DOWN_STR = "down";

	// round settings
	private static final String ROUND_INPUT_STR = "round";
	private static final String ROUND_ABS_STR = "abs";
	private static final int RES_ARG_HOLDER = 1;
	private static final String RESOLUTION_SET_TO_MESSAGE = "Resolution set to %d.";
	private static final int ROUND_ARG_HOLDER = 1;


	// The charMatcher object, in charge of handling the char collection and preparing it for processing
	private final SubImgCharMatcher charMatcher;

	// The image to be manipulated
	private Image image;

	// The set resolution of the image output
	private int resolution = DEFAULT_RESOLUTION;

	// The preferred output method
	private AsciiOutput asciiOutput;


	/**
	 * Constructs a new Shell instance, initializing the character matcher
	 * with the default character database and setting the default ASCII output method.
	 */
	public Shell(){
		charMatcher = new SubImgCharMatcher(DEFAULT_CHAR_DATABASE);
		setAsciiOutput(new String[]{ASCII_OUTPUT_INPUT_STR,DEFAULT_ASCII_OUTPUT});
	}

	/**
	 * Sets the ASCII output method based on the provided arguments.
	 * @param args The arguments array, where the first argument is expected to be "output"
	 * @throws IllegalFormatException if the arguments are not formatted correctly or if an
	 * unsupported output type is specified.
	 */
	private void setAsciiOutput(String[] args) throws IllegalFormatException {
		if (args.length <= 1) {
			throw new IllegalFormatException(INCORRECT_ASCII_OUTPUT_FORMAT_EXCEPTION);
		} else if (args[OUTPUT_TYPE_ARG_HOLDER].equals(HTML_ASCII_OUTPUT_STR)) {
			asciiOutput = new HtmlAsciiOutput(HTML_OUT_FILE_SRC, OUT_FONT_NAME);
		} else if (args[OUTPUT_TYPE_ARG_HOLDER].equals(CONSOLE_ASCII_OUTPUT_STR)) {
			asciiOutput = new ConsoleAsciiOutput();
		} else {
			throw new IllegalFormatException(INCORRECT_ASCII_OUTPUT_FORMAT_EXCEPTION);
		}
	}

	/**
	 * Main method to run the ASCII art shell application.
	 * @param args The command line arguments, where the first argument is expected to be the image path.
	 */
	public static void main(String[] args) {
		String imgSrc;
		try {
			imgSrc = args[0];
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Your must provide an image path.");
			return;
		}

		Shell shell = new Shell();
		shell.run(imgSrc);
	}

	/**
	 * Runs the shell application with the specified image name.
	 * @param imageName Path to the image file to be processed.
	 *                     Program will exit if the image path is invalid.
	 */
	public void run(String imageName) {
		try {
			image = new Image(imageName);
		} catch (IOException e) {
			System.out.println(INVALID_IMG_PATH_MSG);
			return;
		}

		String input;
		do {
			System.out.print(NEXT_INPUT_MSG);
			input = KeyboardInput.readLine();
			String[] input_args = input.split(SPACEBAR_STRING);
			try {
				switch (input_args[0]) {
					case EXIT_INPUT -> {
					}
					case CHARS_INPUT -> charMatcher.printChars();
					case RUN_ASCII_ART_INPUT -> runAsciiArt();
					case ADD_INPUT_STR -> addChar(input_args);
					case REMOVE_INPUT_STR -> removeChar(input_args);
					case ASCII_OUTPUT_INPUT_STR -> setAsciiOutput(input_args);
					case RES_INPUT_STR -> setResolution(input_args);
					case ROUND_INPUT_STR -> setRoundingMethod(input_args);
					default -> System.out.println(INCORRECT_INPUT_FORMAT_MSG);
				}
			} catch (IllegalFormatException | IllegalResolutionException | EmptyCharSetException e){
				System.out.println(e.getMessage());
			}
		} while(!input.equals(EXIT_INPUT));
	}

	/**
	 * Sets the rounding method for the character matcher based on the provided input arguments.
	 * @param inputArgs The input arguments array, where the first argument is expected to be "round"
	 */
	private void setRoundingMethod(String[] inputArgs) {
		if (inputArgs.length <= ROUND_ARG_HOLDER) {
			throw new IllegalFormatException(ROUNDING_METHOD_FORMAT_EXCEPTION);
		}

		switch (inputArgs[ROUND_ARG_HOLDER]) {
			case ROUND_UP_STR -> charMatcher.setRoundMethod(RoundMethod.ROUND_UP);
			case ROUND_DOWN_STR -> charMatcher.setRoundMethod(RoundMethod.ROUND_DOWN);
			case ROUND_ABS_STR -> charMatcher.setRoundMethod(RoundMethod.ROUND_ABS);
			default -> throw new IllegalFormatException(ROUNDING_METHOD_FORMAT_EXCEPTION);
		}
	}

	/**
	 * Sets the resolution for the image processing based on the provided input arguments.
	 * @param inputArgs The input arguments array, where the first argument is expected to be "res",
	 *                  and the second argument can be "up","down", or omitted to display the current resolution.
	 * @throws IllegalFormatException if the input arguments are not formatted correctly.
	 * @throws IllegalResolutionException if the new resolution exceeds the legal boundaries for the image.
	 */
	private void setResolution(String[] inputArgs)
			throws IllegalFormatException, IllegalResolutionException {

		int newRes;
		if (inputArgs.length <= RES_ARG_HOLDER) {
			newRes = resolution;
		} else if (inputArgs[RES_ARG_HOLDER].equals("up")) {
			newRes = resolution * 2;
		} else if (inputArgs[RES_ARG_HOLDER].equals("down")) {
			newRes = resolution / 2;
		} else {
			throw new IllegalFormatException(RESOLUTION_FORMAT_EXCEPTION_MESSAGE);
		}

		if (!ImageProcessor.isLegalResolution(image, newRes)) {
			throw new IllegalResolutionException(EXCEEDING_BOUNDRIES_EXCEPTION_MESSEGE);
		}

		resolution = newRes;
		System.out.println(String.format(RESOLUTION_SET_TO_MESSAGE, newRes) );

	}


	/**
	 * Runs the ASCII art generation process using the current image, resolution, and character matcher.
	 * @throws EmptyCharSetException If the character set is empty, indicating that not enough
	 * characters are available for generating ASCII art.
	 */
	private void runAsciiArt() throws EmptyCharSetException{
		AsciiArtAlgorithm asciiArtAlgorithm = new AsciiArtAlgorithm(image, resolution, charMatcher);
		asciiOutput.out(asciiArtAlgorithm.run());
	}

	/**
	 * Adds a character to the character matcher based on the provided input arguments.
	 * @param args The input arguments array, where the first argument is expected to be "add"
	 * @throws IllegalFormatException if the input arguments are not formatted correctly.
	 */
	private void addChar (String[] args) throws IllegalFormatException {
		try{
			handleChar(args, charMatcher::addChar);
		} catch (IllegalFormatException e) {
			throw new IllegalFormatException(String.format(e.getMessage(), ADD_INPUT_STR));
		}
	}

	/**
	 * Removes a character from the character matcher based on the provided input arguments.
	 * @param args The input arguments array, where the first argument is expected to be "remove".
	 * @throws IllegalFormatException if the input arguments are not formatted correctly.
	 */
	private void removeChar(String[] args) throws IllegalFormatException {
		try{
			handleChar(args, charMatcher::removeChar);
		} catch (IllegalFormatException e) {
			throw new IllegalFormatException(String.format(e.getMessage(), REMOVE_INPUT_STR));
		}
	}

	/**
	 * Handles the character input for adding or removing characters from the character matcher.
	 * This method checks the format of the input arguments and applies the appropriate action.
	 * @param args The input arguments array, where the first argument is expected to be "add" or "remove"
	 * @param characterConsumer A consumer that takes a character and applies the add or remove action.
	 * @throws IllegalFormatException if the input arguments are not formatted correctly or if the character
	 * is out of range.
	 */
	private void handleChar(String[] args, Consumer<Character> characterConsumer)
													throws IllegalFormatException {
		if (args.length <= CHAR_HANDLE_ARG) {
			throw new IllegalFormatException(ADD_REMOVE_ERROR_MESSAGE);
		}
		String arg = args[CHAR_HANDLE_ARG];
		
		if (arg.length() == CHAR_LENGTH){
			checkIfCharIsInRange(arg.charAt(0));
			characterConsumer.accept(arg.charAt(0));
		} else if (arg.equals(SPACE_INPUT_STRING)) {
			characterConsumer.accept(SPACEBAR_STRING.charAt(0));
		} else if (arg.equals(ALL_INPUT_STRING)) {
			handleCharRange(FIRST_LEGAL_CHAR, LAST_LEGAL_CHAR, characterConsumer);
		} else if (isLegalCharRange(arg)) {
			handleCharRange(arg.charAt(CHAR_RANGE_FIRST_CHAR_LOC),
								arg.charAt(CHAR_RANGE_LAST_CHAR_LOC), characterConsumer);
		} else {
			throw new IllegalFormatException(ADD_REMOVE_ERROR_MESSAGE);
		}


	}

	/**
	 * Checks if the provided argument is a legal character range.
	 * @param arg The argument to check, expected to be in the format "a-b" where 'a' and 'b' are characters.
	 * @return true if the argument is a legal character range, false otherwise.
	 * @throws IllegalFormatException if the argument is not formatted correctly.
	 */
	private boolean isLegalCharRange(String arg) throws IllegalFormatException {
		if (arg.length() != CHAR_RANGE_ARG_LENGTH) {
			return false;
		}
		if (arg.charAt(CHAR_RANGE_HYPHEN_LOC) != HYPHEN_CHAR) {
			return false;
		}

		checkIfCharIsInRange(arg.charAt(CHAR_RANGE_FIRST_CHAR_LOC));
		checkIfCharIsInRange(arg.charAt(CHAR_RANGE_LAST_CHAR_LOC));

		return true;
	}

	/**
	 * Checks if the given character is within the legal range of characters
	 * @param c The character to check.
	 * @throws IllegalFormatException if the character is not within the legal range.
	 */
	private void checkIfCharIsInRange(char c) throws IllegalFormatException {
		if (c < FIRST_LEGAL_CHAR || c > LAST_LEGAL_CHAR){
			throw new IllegalFormatException(ADD_REMOVE_ERROR_MESSAGE);
		}
	}

	/**
	 * Handles a range of characters from char1 to char2, applying the provided consumer to each character in the range.
	 * @param char1 The first character in the range.
	 * @param char2 The last character in the range.
	 * @param characterConsumer A consumer that takes a character and applies an action to it.
	 */
	private void handleCharRange(char char1, char char2,  Consumer<Character> characterConsumer) {
		for (char c = (char) Math.min(char1, char2); c <= (char) Math.max(char1, char2); c++) {
			characterConsumer.accept(c);
		}
	}

}
