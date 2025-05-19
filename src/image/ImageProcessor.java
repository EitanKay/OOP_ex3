package image;

import java.awt.*;
import java.util.ArrayList;

public class ImageProcessor {
    private static final int RGB_MAX_VAL = 255;
    private static ImageProcessor instance = null;

    private final Image sourceImage;
    private final String imagePath;
    private final int tileSize;
    Color[][] paddedImage;
    ArrayList<Image> tiles = new ArrayList<>();

    private ImageProcessor(Image image, String imagePath, int tilePower) {
        this.sourceImage = image;
        this.imagePath = imagePath;
        this.tileSize = tilePower;
        pad();
    }

    public static ImageProcessor getInstance(Image image, String imagePath, int tilePower) {
        if (instance == null ||
                !instance.imagePath.equals(imagePath) ||
                instance.tileSize != tilePower) {
            instance = new ImageProcessor(image, imagePath, tilePower);
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

    public void splitIntoTiles() {


        int height = paddedImage.length;
        int width = paddedImage[0].length;

        int tilesVertically = height / tileSize;
        int tilesHorizontally = width / tileSize;
        int totalTiles = tilesVertically * tilesHorizontally;

        //Color[][][] tiles = new Color[totalTiles][][];
        for (int row = 0; row < tilesVertically; row++) {
            for (int col = 0; col < tilesHorizontally; col++) {
                Color[][] tile = new Color[tileSize][tileSize];
                int startRow = row * tileSize;
                int startCol = col * tileSize;

                for (int i = 0; i < tileSize; i++) {
                    for (int j = 0; j < tileSize; j++) {
                        tile[i][j] = paddedImage[startRow + i][startCol + j];
                    }
                }

                tiles.add(new Image(tile, tileSize, tileSize));
            }
        }
    }

    public double getMeanGrayGrade(Image image) {
        double meanGrade = 0;
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                Color pixel = image.getPixel(i, j);
                double greyGradeOfPixel = pixel.getRed() * 0.2126 + pixel.getGreen() * 0.7152
                        + pixel.getBlue() * 0.0722;
                meanGrade +=  greyGradeOfPixel;
            }
        }
        return meanGrade/(image.getHeight() * image.getWidth() * RGB_MAX_VAL);
    }
}
