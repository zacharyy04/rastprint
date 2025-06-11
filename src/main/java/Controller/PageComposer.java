package Controller;

import model.CMYKPixel;
import model.PrintParameters;

public class PageComposer {

    // 5 mm de marge = 0.5 cm = converti en pixels
    private static int marginPixels(int dpi) {
        return (int) Math.round(dpi * 0.5 / 2.54);  // 0.5 cm en pixels
    }

    public static CMYKPixel[][] composePage(CMYKPixel[][] image, PrintParameters params) {
        int dpi = params.getDpi();
        format = params.getPaperFormat(); // A4 ou A3
        orientation = params.getOrientation(); // portrait ou paysage
        String align = params.getAlign(); // top, bottom, left, right

        // Dimensions page (cm) avant rotation
        double widthCm = format.equalsIgnoreCase("A4") ? 21.0 : 29.7;
        double heightCm = format.equalsIgnoreCase("A4") ? 29.7 : 42.0;

        if (orientation.equalsIgnoreCase("paysage")) {
            double temp = widthCm;
            widthCm = heightCm;
            heightCm = temp;
        }

        int pageWidthPx = (int) Math.round(widthCm * dpi / 2.54);
        int pageHeightPx = (int) Math.round(heightCm * dpi / 2.54);
        int margin = marginPixels(dpi);

        // Créer une page blanche (CMYK 0,0,0,0)
        CMYKPixel[][] page = new CMYKPixel[pageHeightPx][pageWidthPx];
        for (int y = 0; y < pageHeightPx; y++) {
            for (int x = 0; x < pageWidthPx; x++) {
                page[y][x] = new CMYKPixel(0, 0, 0, 0);
            }
        }

        // Position de l'image selon l'alignement
        int imgHeight = image.length;
        int imgWidth = image[0].length;

        int posY = margin;
        int posX = margin;

        if (align.contains("bottom")) {
            posY = pageHeightPx - imgHeight - margin;
        }
        if (align.contains("right")) {
            posX = pageWidthPx - imgWidth - margin;
        }
        if (align.contains("center")) {
            posX = (pageWidthPx - imgWidth) / 2;
            posY = (pageHeightPx - imgHeight) / 2;
        }

        // Copier l'image dans la page
        for (int y = 0; y < imgHeight; y++) {
            for (int x = 0; x < imgWidth; x++) {
                int pageY = posY + y;
                int pageX = posX + x;

                if (pageY >= margin && pageY < pageHeightPx - margin &&
                        pageX >= margin && pageX < pageWidthPx - margin) {
                    page[pageY][pageX] = image[y][x];
                }
            }
        }

        return page;
    }
}
