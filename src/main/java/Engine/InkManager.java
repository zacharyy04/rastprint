package Engine;

import model.CMYKPixel;
import model.Enums.PrintQuality;

import java.util.HashMap;
import java.util.Map;

public class InkManager {

    private final Map<String, PrintHead> printHeads;

    public InkManager() {
        this.printHeads = new HashMap<>();
        printHeads.put("cyan", new PrintHead("cyan", 8.0));
        printHeads.put("magenta", new PrintHead("magenta", 8.0));
        printHeads.put("yellow", new PrintHead("yellow", 8.0));
        printHeads.put("black", new PrintHead("black", 8.0));
    }

    public void consume(CMYKPixel pixel, PrintQuality quality) {
        double multiplier = switch (quality) {
            case HIGH -> 1.0;
            case STANDARD -> 0.75;
            case DRAFT -> 0.5;
        };

        printHeads.get("cyan").consumeInk(pixel.getC() * multiplier * 0.0000000000001);
        printHeads.get("magenta").consumeInk(pixel.getM() * multiplier * 0.0000000000001);
        printHeads.get("yellow").consumeInk(pixel.getY() * multiplier * 0.0000000000001);
        printHeads.get("black").consumeInk(pixel.getK() * multiplier * 0.0000000000001);
    }

    public boolean isAnyEmpty() {
        return printHeads.values().stream().anyMatch(PrintHead::isEmpty);
    }

    public boolean isLow(String color) {
        return getLevel(color) < 1.0;
    }

    public double getLevel(String color) {
        return printHeads.get(color).getInkLevel();
    }

    public Map<String, Double> getInkUsage() {
        Map<String, Double> usage = new HashMap<>();
        for (Map.Entry<String, PrintHead> entry : printHeads.entrySet()) {
            String color = entry.getKey();
            double used = 8.0 - entry.getValue().getInkLevel(); // 8.0 ml = plein
            usage.put(color, used);
        }
        return usage;
    }

    public java.util.List<String> getLowColors() {
        return printHeads.entrySet().stream()
                .filter(e -> e.getValue().isLow())
                .map(Map.Entry::getKey)
                .toList();
    }

    public boolean hasSufficientInk(CMYKPixel[][] image, PrintQuality quality) {
        double multiplier = switch (quality) {
            case HIGH -> 1.0;
            case STANDARD -> 0.75;
            case DRAFT -> 0.5;
        };

        double neededC = 0, neededM = 0, neededY = 0, neededK = 0;
        for (CMYKPixel[] row : image) {
            for (CMYKPixel pixel : row) {
                neededC += pixel.getC() * multiplier;
                neededM += pixel.getM() * multiplier;
                neededY += pixel.getY() * multiplier;
                neededK += pixel.getK() * multiplier;
            }
        }

        return neededC <= getLevel("cyan") * 1_000_000 &&
                neededM <= getLevel("magenta") * 1_000_000 &&
                neededY <= getLevel("yellow") * 1_000_000 &&
                neededK <= getLevel("black") * 1_000_000;
    }

    public void resetInk() {
        for (PrintHead head : printHeads.values()) {
            head.setInkLevel(8.0);
        }
    }


}
