package Controller;

import model.CMYKPixel;
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
        // TODO : centrer, découper, dupliquer selon imagesPerPage, orientation, format, etc.
        // Pour l’instant, on retourne l’image telle quelle.
        return source;
    }
}
