package shared;

import model.Enums.*;

public class PrintDimensionHelper {

    public static int[] getPixelDimensions(PaperFormat format, Orientation orientation, int dpi) {
        // Format en pouces (source officielle)
        double widthInch = (format == PaperFormat.A4) ? 8.27 : 11.69;
        double heightInch = (format == PaperFormat.A4) ? 11.69 : 16.54;

        if (orientation == Orientation.LANDSCAPE) {
            double temp = widthInch;
            widthInch = heightInch;
            heightInch = temp;
        }

        int widthPx = (int) Math.round(widthInch * dpi);
        int heightPx = (int) Math.round(heightInch * dpi);
        return new int[]{ widthPx, heightPx };
    }
}
