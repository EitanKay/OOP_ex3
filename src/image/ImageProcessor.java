package image;

import java.awt.*;
import java.util.ArrayList;

public class ImageProcessor {
    private static final int RGB_MAX_VAL = 255;
    private static final double RED_PORTION_OF_GRAY = 0.2126;
    private static final double GREEN_PORTION_OF_GRAY = 0.7152;
    private static final double BLUE_PORTION_OF_GRAY = 0.0722;
    private static ImageProcessor instance = null;

    private final Image sourceImage;
    private final int resolution;
    private final int tileSize;
    Color[][] paddedImage;
    ArrayList<ArrayList<Image>> tiles = new ArrayList<>();

    private ImageProcessor(Image sourceImage, int resolution) {
        this.sourceImage = sourceImage;
        this.resolution = resolution;
        pad();
        this.tileSize = paddedImage[0].length / resolution;
    }

    public static ImageProcessor getInstance(Image image, int resolution) {
        if (instance == null ||
                instance.resolution != resolution) {
            instance = new ImageProcessor(image, resolution);
        }
        return instance;
    }

    public static int nextPowerOfTwo(int n) {
        if (n <= 0) return 1;
        if ((n & (n - 1)) == 0) return n;
        return Integer.highestOneBit(n) << 1;
    }

    private void pad() {
        int newWidth = nextPowerOfTwo(sourceImage.getWidth());
        int newHeight = nextPowerOfTwo(sourceImage.getHeight());
        paddedImage = new Color[newHeight][newWidth];

        int horizontalPad = (newWidth - sourceImage.getWidth()) / 2;
        int verticalPad = (newHeight - sourceImage.getHeight()) / 2;

        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                paddedImage[i][j] = Color.WHITE;
            }
        }

        for (int i = 0; i < sourceImage.getHeight(); i++) {
            for (int j = 0; j < sourceImage.getWidth(); j++) {
                paddedImage[i + verticalPad][j + horizontalPad] = sourceImage.getPixel(i, j);
            }
        }
    }

    private void splitIntoTiles() {
        int height = paddedImage.length;
        int width = paddedImage[0].length;

        int tilesVertically = height / tileSize;
        int tilesHorizontally = width / tileSize;

        for (int row = 0; row < tilesVertically; row++) {
            tiles.add(new ArrayList<>());
            for (int col = 0; col < tilesHorizontally; col++) {
                Color[][] tile = new Color[tileSize][tileSize];
                int startRow = row * tileSize;
                int startCol = col * tileSize;

                for (int i = 0; i < tileSize; i++) {
                    for (int j = 0; j < tileSize; j++) {
                        tile[i][j] = paddedImage[startRow + i][startCol + j];
                    }
                }
                tiles.get(row).add(new Image(tile, tileSize, tileSize));
            }
        }
    }

    private double getMeanGrayGrade(Image image) {
        double meanGrade = 0;
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                Color pixel = image.getPixel(i, j);
                double greyGradeOfPixel =
                        pixel.getRed() * RED_PORTION_OF_GRAY +
                        pixel.getGreen() * GREEN_PORTION_OF_GRAY +
                        pixel.getBlue() * BLUE_PORTION_OF_GRAY;
                meanGrade +=  greyGradeOfPixel;
            }
        }
        return meanGrade/(image.getHeight() * image.getWidth() * RGB_MAX_VAL);
    }

    public double[][] getMeanGrayGrades() {
        splitIntoTiles();
        double[][] meanGrayGrades = new double[tiles.size()][tiles.get(0).size()];
        for (int i = 0; i < tiles.size(); i++) {
            for (int j = 0; j < tiles.get(i).size(); j++) {
                meanGrayGrades[i][j] = getMeanGrayGrade(tiles.get(i).get(j));
            }
        }
        return meanGrayGrades;
    }
}
