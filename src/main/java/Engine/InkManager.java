package Engine;

import model.CMYKPixel;
import model.Enums.PrintQuality;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code InkManager} class manages the ink levels for each color print head
 * (cyan, magenta, yellow, black) in a simulated CMYK printing system.
 * <p>
 * It provides methods to consume ink based on pixel values and print quality,
 * check levels, estimate usage, and reset cartridges.
 */
public class InkManager {

    private final Map<String, PrintHead> printHeads;

    /**
     * Constructs a new {@code InkManager} with all cartridges initialized to 8.0 ml.
     */
    public InkManager() {
        this.printHeads = new HashMap<>();
        printHeads.put("cyan", new PrintHead("cyan", 8.0));
        printHeads.put("magenta", new PrintHead("magenta", 8.0));
        printHeads.put("yellow", new PrintHead("yellow", 8.0));
        printHeads.put("black", new PrintHead("black", 8.0));
    }

    /**
     * Consumes ink from each print head based on the CMYK pixel and print quality.
     *
     * @param pixel   the CMYK pixel to print
     * @param quality the print quality (affects consumption rate)
     */
    public void consume(CMYKPixel pixel, PrintQuality quality) {
        double multiplier = switch (quality) {
            case HIGH -> 1.0;
            case STANDARD -> 0.75;
            case DRAFT -> 0.5;
        };

        printHeads.get("cyan").consumeInk(pixel.getC() * multiplier * 0.00000001);
        printHeads.get("magenta").consumeInk(pixel.getM() * multiplier * 0.00000001);
        printHeads.get("yellow").consumeInk(pixel.getY() * multiplier * 0.00000001);
        printHeads.get("black").consumeInk(pixel.getK() * multiplier * 0.00000001);
    }

    /**
     * Checks if any cartridge is empty.
     *
     * @return {@code true} if at least one cartridge has zero ink
     */
    public boolean isAnyEmpty() {
        return printHeads.values().stream().anyMatch(PrintHead::isEmpty);
    }

    /**
     * Checks if a specific color cartridge is below 1.0 ml.
     *
     * @param color the color name (cyan, magenta, yellow, black)
     * @return {@code true} if the ink level is below 1.0 ml
     */
    public boolean isLow(String color) {
        return getLevel(color) < 1.0;
    }

    /**
     * Returns the current ink level of a specific color.
     *
     * @param color the color name
     * @return the ink level in milliliters
     */
    public double getLevel(String color) {
        return printHeads.get(color).getInkLevel();
    }

    /**
     * Returns the amount of ink consumed from each cartridge since the start.
     *
     * @return a map of used ink per color
     */
    public Map<String, Double> getInkUsage() {
        Map<String, Double> usage = new HashMap<>();
        for (Map.Entry<String, PrintHead> entry : printHeads.entrySet()) {
            String color = entry.getKey();
            double used = 8.0 - entry.getValue().getInkLevel(); // max = 8.0 ml
            usage.put(color, used);
        }
        return usage;
    }

    /**
     * Returns a list of colors where the ink is below 1.0 ml.
     *
     * @return a list of color names with low ink
     */
    public java.util.List<String> getLowColors() {
        return printHeads.entrySet().stream()
                .filter(e -> e.getValue().isLow())
                .map(Map.Entry::getKey)
                .toList();
    }

    /**
     * Checks if all cartridges have enough ink to print the given image once.
     *
     * @param image   the CMYK image matrix
     * @param quality the selected print quality
     * @return {@code true} if sufficient ink is available
     */
    public boolean hasSufficientInk(CMYKPixel[][] image, PrintQuality quality) {
        double multiplier = switch (quality) {
            case HIGH -> 1.0;
            case STANDARD -> 0.75;
            case DRAFT -> 0.5;
        };

        double neededC = 0, neededM = 0, neededY = 0, neededK = 0;
        for (CMYKPixel[] row : image) {
            for (CMYKPixel pixel : row) {
                neededC += pixel.getC() * multiplier * 1e-6;
                neededM += pixel.getM() * multiplier * 1e-6;
                neededY += pixel.getY() * multiplier * 1e-6;
                neededK += pixel.getK() * multiplier * 1e-6;
            }
        }

        return neededC <= getLevel("cyan") * 1_000_000 &&
                neededM <= getLevel("magenta") * 1_000_000 &&
                neededY <= getLevel("yellow") * 1_000_000 &&
                neededK <= getLevel("black") * 1_000_000;
    }

    /**
     * Resets all ink levels to the maximum of 8.0 ml.
     */
    public void resetInk() {
        for (PrintHead head : printHeads.values()) {
            head.setInkLevel(8.0);
        }
    }

    /**
     * Returns the current ink levels of all cartridges.
     *
     * @return a map with color names and remaining volume (in ml)
     */
    public Map<String, Double> getLevels() {
        Map<String, Double> usage = new HashMap<>();
        for (Map.Entry<String, PrintHead> entry : printHeads.entrySet()) {
            String color = entry.getKey();
            double volume = entry.getValue().getInkLevel();
            usage.put(color, volume);
        }
        return usage;
    }

    /**
     * Estimates the ink needed to print the given image once,
     * without consuming actual ink.
     *
     * @param image   the CMYK image matrix
     * @param quality the selected print quality
     * @return a map of estimated ink consumption per color (in ml)
     */
    public Map<String, Double> estimateInkUsage(CMYKPixel[][] image, PrintQuality quality) {
        double multiplier = switch (quality) {
            case HIGH -> 1.0;
            case STANDARD -> 0.75;
            case DRAFT -> 0.5;
        };

        double c = 0, m = 0, y = 0, k = 0;
        for (CMYKPixel[] row : image) {
            for (CMYKPixel pixel : row) {
                c += pixel.getC() * multiplier * 1e-6;
                m += pixel.getM() * multiplier * 1e-6;
                y += pixel.getY() * multiplier * 1e-6;
                k += pixel.getK() * multiplier * 1e-6;
            }
        }

        Map<String, Double> estimated = new HashMap<>();
        estimated.put("cyan", c);
        estimated.put("magenta", m);
        estimated.put("yellow", y);
        estimated.put("black", k);
        return estimated;
    }
}
