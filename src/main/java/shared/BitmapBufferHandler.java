package shared;

import model.CMYKPixel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapBufferHandler {

    /**
     * Écrit un tableau 2D de pixels CMYK dans un fichier binaire (ligne par ligne, 4 octets/pixel)
     */
    public static void writeBuffer(CMYKPixel[][] data, String path) throws IOException {
        try (FileOutputStream out = new FileOutputStream(path)) {
            for (CMYKPixel[] row : data) {
                for (CMYKPixel pixel : row) {
                    out.write((int) (pixel.getC() * 255));
                    out.write((int) (pixel.getM() * 255));
                    out.write((int) (pixel.getY() * 255));
                    out.write((int) (pixel.getK() * 255));
                }
            }
        }
    }

    /**
     * Lit un fichier binaire contenant des pixels CMYK (4 octets/pixel) et retourne un tableau 2D
     */
    public static CMYKPixel[][] readBuffer(String path, int width, int height) throws IOException {
        CMYKPixel[][] data = new CMYKPixel[height][width];
        try (FileInputStream in = new FileInputStream(path)) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int c = in.read();
                    int m = in.read();
                    int yel = in.read();
                    int k = in.read();
                    if (k == -1) throw new IOException("Fichier corrompu ou incomplet.");

                    data[y][x] = new CMYKPixel(
                            c / 255.0,
                            m / 255.0,
                            yel / 255.0,
                            k / 255.0
                    );
                }
            }
        }
        return data;
    }
}
