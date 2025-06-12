package Controller;

import model.CMYKPixel;
import model.Enums;
import model.PrintParameters;


import java.awt.image.BufferedImage;

public class ImageProcessor {

    public CMYKPixel[][] convertToCMYK(BufferedImage image, PrintParameters params) {
        int width = image.getWidth();
        int height = image.getHeight();
        CMYKPixel[][] cmykData = new CMYKPixel[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                cmykData[y][x] = rgbToCmyk(r, g, b, params);
            }
        }

        return cmykData;
    }

    private CMYKPixel rgbToCmyk(int r, int g, int b, PrintParameters params) {
        double rp = r / 255.0;
        double gp = g / 255.0;
        double bp = b / 255.0;

        double k = 1.0 - Math.max(rp, Math.max(gp, bp));
        double c = (k == 1.0) ? 0.0 : (1.0 - rp - k) / (1.0 - k);
        double m = (k == 1.0) ? 0.0 : (1.0 - gp - k) / (1.0 - k);
        double y = (k == 1.0) ? 0.0 : (1.0 - bp - k) / (1.0 - k);

        if (params.getColorMode() == model.Enums.ColorMode.GRAYSCALE) {
            // Mettre uniquement dans K le niveau de gris
            double gray = (r + g + b) / 3.0 / 255.0;
            return new CMYKPixel(0.0, 0.0, 0.0, 1.0 - gray);
        }

        return new CMYKPixel(c, m, y, k);
    }

    public CMYKPixel[][] applyLayout(PrintParameters params, CMYKPixel[][] source) {

        int dpi = switch (params.getQuality()) {
            case DRAFT -> 150;
            case STANDARD -> 300;
            case HIGH -> 600;
        };

        // Dimensions en pouces : A4 = 8.27 × 11.69, A3 = 11.69 × 16.54
        double widthInch = params.getPaperFormat().equals("A3") ? 11.69 : 8.27;
        double heightInch = params.getPaperFormat().equals("A3") ? 16.54 : 11.69;

        if (params.getOrientation() == Enums.Orientation.LANDSCAPE) {
            source = rotate90(source);
            double temp = widthInch;
            widthInch = heightInch;
            heightInch = temp;
        }

        int pageWidth = (int) Math.round(widthInch * dpi);
        int pageHeight = (int) Math.round(heightInch * dpi);

        int imagesPerPage = params.getImagesPerPage();
        int cols = (imagesPerPage == 4) ? 2 : 1;
        int rows = (imagesPerPage == 2) ? 2 : (imagesPerPage == 4 ? 2 : 1);

        int targetWidth = pageWidth;
        int targetHeight = pageHeight;

        CMYKPixel[][] canvas = new CMYKPixel[targetHeight][targetWidth];
        for (int y = 0; y < targetHeight; y++) {
            for (int x = 0; x < targetWidth; x++) {
                canvas[y][x] = new CMYKPixel(0, 0, 0, 0); // fond blanc (0 en encre)
            }
        }

        int slotWidth = targetWidth / cols;
        int slotHeight = targetHeight / rows;

        int imageWidth = source[0].length;
        int imageHeight = source.length;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int offsetX = col * slotWidth + (slotWidth - imageWidth) / 2;
                int offsetY = row * slotHeight + (slotHeight - imageHeight) / 2;

                for (int y = 0; y < imageHeight; y++) {
                    for (int x = 0; x < imageWidth; x++) {
                        int canvasY = offsetY + y;
                        int canvasX = offsetX + x;
                        if (canvasY < targetHeight && canvasX < targetWidth) {
                            canvas[canvasY][canvasX] = source[y][x];
                        }
                    }
                }
            }
        }

        return canvas;
    }


    public CMYKPixel[][] rotate90(CMYKPixel[][] input) {
        int height = input.length;
        int width = input[0].length;

        CMYKPixel[][] rotated = new CMYKPixel[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                rotated[x][height - 1 - y] = input[y][x];
            }
        }
        return rotated;
    }

}
