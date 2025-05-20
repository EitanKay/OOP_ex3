package ascii_art;

import ascii_output.AsciiOutput;
import exceptions.EmptyCharSetException;
import image.ImageProcessor;
import image_char_matching.SubImgCharMatcher;
import image.Image;

public class AsciiArtAlgorithm {
    private final Image sourceImage;
    private final int resolution;
    private final SubImgCharMatcher charMatcher;

    AsciiArtAlgorithm(Image sourceImage, int resolution, SubImgCharMatcher charMatcher) {
        this.sourceImage = sourceImage;
        this.resolution = resolution;
        this.charMatcher = charMatcher;
    }

    public char[][] run() throws EmptyCharSetException {
        ImageProcessor processor = ImageProcessor.getInstance(sourceImage,resolution);
        double[][] brightnesses = processor.getMeanGrayGrades();
        char[][] asciiArt = new char[brightnesses.length][brightnesses[0].length];
        for (int i = 0; i < brightnesses.length; i++) {
            for (int j = 0; j < brightnesses[0].length; j++) {
                asciiArt[i][j] = charMatcher.getCharByImageBrightness(brightnesses[i][j]);
            }
        }
        return asciiArt;
    }
}
