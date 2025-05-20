package ascii_art;

import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import exceptions.EmptyCharSetException;
import image.Image;
import image_char_matching.SubImgCharMatcher;
import exceptions.IncorrectFormatException;

import java.io.IOException;
import java.util.function.Consumer;

public class Shell {

	private static final String NEXT_INPUT_MSG = ">>> ";
	private static final String INVALID_IMG_PATH_MSG =
			"Invalid Image Path, try running the program again with a valid path.";
	private static final String EXIT_INPUT = "exit";
	private static final char[] DEFAULT_CHAR_DATABASE = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
	private static final String SPACEBAR_STRING = " ";
	private static final String INCORRECT_INPUT_FORMAT_MSG = "Did not change output method due to incorrect format.";
	private static final String RUN_ASCII_ART_INPUT = "asciiArt";
	private static final int DEFAULT_RESOLUTION = 64;
	private static final String CONSOLE_ASCII_OUTPUT_STR = "console";
	private static final String HTML_ASCII_OUTPUT_STR = "html";
	private static final String DEFAULT_ASCII_OUTPUT = CONSOLE_ASCII_OUTPUT_STR;
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


	private final SubImgCharMatcher charMatcher;
	private Image image;
	private int resolution = DEFAULT_RESOLUTION;
	private AsciiOutput asciiOutput;

	public Shell(){
		charMatcher = new SubImgCharMatcher(DEFAULT_CHAR_DATABASE);
		setAsciiOutput(DEFAULT_ASCII_OUTPUT);
	}

	private void setAsciiOutput(String asciiOutputString) {
		switch (asciiOutputString) {
			case CONSOLE_ASCII_OUTPUT_STR -> asciiOutput = new ConsoleAsciiOutput();
			case HTML_ASCII_OUTPUT_STR -> asciiOutput = new HtmlAsciiOutput(HTML_OUT_FILE_SRC,OUT_FONT_NAME);
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

			switch (input_args[0]){
				case EXIT_INPUT -> {}
				case CHARS_INPUT -> charMatcher.printChars();
				case RUN_ASCII_ART_INPUT -> runAsciiArt();
				case ADD_INPUT_STR -> addChar(input_args);
				case REMOVE_INPUT_STR -> removeChar(input_args);
				default -> System.out.println(INCORRECT_INPUT_FORMAT_MSG);
			}

		} while(!input.equals(EXIT_INPUT));
	}


	private void runAsciiArt() {
		AsciiArtAlgorithm asciiArtAlgorithm = new AsciiArtAlgorithm(image, resolution, charMatcher);
		try {
			asciiOutput.out(asciiArtAlgorithm.run());
		} catch (EmptyCharSetException e) {
			System.out.println(e.getMessage());
		}
	}

	private void addChar(String[] args) {
		try{
			handleChar(args, charMatcher::addChar);
		} catch (IncorrectFormatException e) {
			System.out.println(String.format(e.getMessage(), ADD_INPUT_STR));
		}
	}

	private void removeChar(String[] args) {
		try{
			handleChar(args, charMatcher::removeChar);
		} catch (IncorrectFormatException e) {
			System.out.println(String.format(e.getMessage(), REMOVE_INPUT_STR));
		}
	}
	private void handleChar(String[] args, Consumer<Character> characterConsumer)
													throws IncorrectFormatException{
		if (args.length <= CHAR_HANDLE_ARG) {
			throw new IncorrectFormatException(ADD_REMOVE_ERROR_MESSAGE);
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
			throw new IncorrectFormatException(ADD_REMOVE_ERROR_MESSAGE);
		}


	}

	public boolean isLegalCharRange(String arg) throws IncorrectFormatException{
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

	public void checkIfCharIsInRange(char c) throws IncorrectFormatException{
		if (c < FIRST_LEGAL_CHAR || c > LAST_LEGAL_CHAR){
			throw new IncorrectFormatException(ADD_REMOVE_ERROR_MESSAGE);
		}
	}

	private void handleCharRange(char char1, char char2,  Consumer<Character> characterConsumer) {
		for (char c = (char) Math.min(char1, char2); c <= (char) Math.max(char1, char2); c++) {
			characterConsumer.accept(c);
		}
	}

}
