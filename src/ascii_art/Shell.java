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

public class Shell {



	private static final String NEXT_INPUT_MSG = ">>> ";
	private static final String INVALID_IMG_PATH_MSG =
			"Invalid Image Path, try running the program again with a valid path.";
	private static final String EXIT_INPUT = "exit";
	private static final String SPACEBAR_STRING = " ";
	private static final String INCORRECT_INPUT_FORMAT_MSG
									= "Did not execute due to incorrect command.";
	private static final String RUN_ASCII_ART_INPUT = "asciiArt";
	private static final String CONSOLE_ASCII_OUTPUT_STR = "console";
	private static final String HTML_ASCII_OUTPUT_STR = "html";


	// defaults
	private static final char[] DEFAULT_CHAR_DATABASE = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
	private static final int DEFAULT_RESOLUTION = 2; // TODO: change back to defauld
	private static final String DEFAULT_ASCII_OUTPUT = CONSOLE_ASCII_OUTPUT_STR; // TODO: change back to defauld


	private static final String HTML_OUT_FILE_SRC = "out.html";
	private static final String OUT_FONT_NAME = "Courier New";
	private static final int CHAR_HANDLE_ARG = 1;
	private static final String ADD_REMOVE_ERROR_MESSAGE = "Did not %s due to incorrect format.";
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
	private static final String ASCII_OUTPUT_INPUT_STR = "output";
	private static final String RES_INPUT_STR = "res";
	private static final String ROUND_INPUT_STR = "round";
	private static final String ROUND_UP_STR = "up";
	private static final String ROUND_DOWN_STR = "down";
	private static final String ROUND_ABS_STR = "abs";
	private static final String INCORRECT_ASCII_OUTPUT_FORMAT_EXCEPTION
									= "Did not change output method due to incorrect format.";
	private static final int OUTPUT_TYPE_ARG_HOLDER = 1;
	private static final String RESOLUTION_FORMAT_EXCEPTION_MESSAGE
									= "Did not change resolution due to incorrect format.";
	private static final int RES_ARG_HOLDER = 1;
	private static final String EXCEEDING_BOUNDRIES_EXCEPTION_MESSEGE
									= "Did not change resolution due to exceeding boundaries.";
	private static final String RESOLUTION_SET_TO_MESSAGE = "Resolution set to %d.";
	private static final int ROUND_ARG_HOLDER = 1;
	private static final String ROUNDING_METHOD_FORMAT_EXCEPTION
									= "Did not change rounding method due to incorrect format.";


	private final SubImgCharMatcher charMatcher;
	private Image image;
	private int resolution = DEFAULT_RESOLUTION;
	private String asciiOutput = DEFAULT_ASCII_OUTPUT;

	public Shell(){
		charMatcher = new SubImgCharMatcher(DEFAULT_CHAR_DATABASE);
	}

	private void setAsciiOutput(String[] args) throws IllegalFormatException {
		//TODO: return to only set implementaion
		if (args.length <= 1
					|| (!args[OUTPUT_TYPE_ARG_HOLDER].equals(HTML_ASCII_OUTPUT_STR)
				    	 && !args[OUTPUT_TYPE_ARG_HOLDER].equals(CONSOLE_ASCII_OUTPUT_STR))) {
			throw new IllegalFormatException(INCORRECT_ASCII_OUTPUT_FORMAT_EXCEPTION);
		}
		this.asciiOutput = args[OUTPUT_TYPE_ARG_HOLDER];
	}

	private AsciiOutput getAsciiOutput(String asciiOutputString) {
		switch (asciiOutputString) {
			case CONSOLE_ASCII_OUTPUT_STR -> {
				return new ConsoleAsciiOutput();
			} case HTML_ASCII_OUTPUT_STR -> {
				return new HtmlAsciiOutput(HTML_OUT_FILE_SRC,OUT_FONT_NAME);
			}default -> {
				return getAsciiOutput(DEFAULT_ASCII_OUTPUT);
			}

		}
	}

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

	private void setRoundingMethod(String[] inputArgs) {
		if (inputArgs.length <= ROUND_ARG_HOLDER) {
			throw new IllegalFormatException(ROUNDING_METHOD_FORMAT_EXCEPTION);
		}

		switch (inputArgs[ROUND_ARG_HOLDER]) {
			case ROUND_UP_STR -> charMatcher.setRoundMethod(RoundMethod.ROUND_UP);
			case "down" -> charMatcher.setRoundMethod(RoundMethod.ROUND_DOWN);
			case "abs" -> charMatcher.setRoundMethod(RoundMethod.ROUND_ABS);
			default -> throw new IllegalFormatException(ROUNDING_METHOD_FORMAT_EXCEPTION);
		}
	}

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


	private void runAsciiArt() throws EmptyCharSetException{
		// TOOD: Check why can this not run more than once
		AsciiArtAlgorithm asciiArtAlgorithm = new AsciiArtAlgorithm(image, resolution, charMatcher);
		getAsciiOutput(asciiOutput).out(asciiArtAlgorithm.run());

	}

	private void addChar (String[] args) throws IllegalFormatException {
		try{
			handleChar(args, charMatcher::addChar);
		} catch (IllegalFormatException e) {
			throw new IllegalFormatException(String.format(e.getMessage(), ADD_INPUT_STR));
		}
	}

	private void removeChar(String[] args) throws IllegalFormatException {
		try{
			handleChar(args, charMatcher::removeChar);
		} catch (IllegalFormatException e) {
			throw new IllegalFormatException(String.format(e.getMessage(), REMOVE_INPUT_STR));
		}
	}
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

	public boolean isLegalCharRange(String arg) throws IllegalFormatException {
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

	public void checkIfCharIsInRange(char c) throws IllegalFormatException {
		if (c < FIRST_LEGAL_CHAR || c > LAST_LEGAL_CHAR){
			throw new IllegalFormatException(ADD_REMOVE_ERROR_MESSAGE);
		}
	}

	private void handleCharRange(char char1, char char2,  Consumer<Character> characterConsumer) {
		for (char c = (char) Math.min(char1, char2); c <= (char) Math.max(char1, char2); c++) {
			characterConsumer.accept(c);
		}
	}

}
