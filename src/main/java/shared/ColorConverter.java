package shared;

import model.CMYKPixel;
import java.awt.Color;

public class ColorConverter {

    /**
     * Convertit un pixel CMYK en couleur RGB (java.awt.Color)
     */
    public Color toRGB(CMYKPixel pixel) {
        double c = pixel.getC();
        double m = pixel.getM();
        double y = pixel.getY();
        double k = pixel.getK();

        // Formule : RGB = 255 × (1 - C) × (1 - K)
        int r = (int) Math.round(255 * (1 - c) * (1 - k));
        int g = (int) Math.round(255 * (1 - m) * (1 - k));
        int b = (int) Math.round(255 * (1 - y) * (1 - k));

        // Clamp (au cas où)
        r = clamp(r);
        g = clamp(g);
        b = clamp(b);

        return new Color(r, g, b);
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}
