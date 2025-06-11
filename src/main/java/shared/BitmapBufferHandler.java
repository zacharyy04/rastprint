package shared;

import model.CMYKPixel;

import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapBufferHandler {

    public static void writeBuffer(CMYKPixel[][] data, String path) throws IOException {
        try (FileOutputStream out = new FileOutputStream(path)) {
            for (CMYKPixel[] row : data) {
                for (CMYKPixel pixel : row) {
                    out.write((int)(pixel.getC() * 255));
                    out.write((int)(pixel.getM() * 255));
                    out.write((int)(pixel.getY() * 255));
                    out.write((int)(pixel.getK() * 255));
                }
            }
        }
    }
}
