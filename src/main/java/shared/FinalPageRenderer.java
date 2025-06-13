package shared;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import model.PrintParameters;
import model.CMYKPixel;

public class FinalPageRenderer {

    /**
     * Convertit la page CMYK finale en PNG RGB visualisable
     */
    public static void renderToPNG(CMYKPixel[][] page, PrintParameters params, String outputPath) throws Exception {
        int heightPx = page.length;
        int widthPx = page[0].length;

        BufferedImage image = new BufferedImage(widthPx, heightPx, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, widthPx, heightPx);

        ColorConverter converter = new ColorConverter();

        for (int y = 0; y < heightPx; y++) {
            for (int x = 0; x < widthPx; x++) {
                CMYKPixel p = page[y][x];
                Color rgb = converter.toRGB(p);
                image.setRGB(x, y, rgb.getRGB());
            }
        }

        g.dispose();
        ImageIO.write(image, "png", new File(outputPath));
        System.out.println("✅ PNG de la page généré : " + outputPath);
    }
}
