package Engine;

import java.util.HashMap;
import java.util.Map;
import model.CMYKPixel;
import model.PrintQuality;

public class InkManager {
    private final Map<String, PrintHead> printHeads;

    public InkManager() {
        printHeads = new HashMap<>();
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

        printHeads.get("cyan").consumeInk(pixel.getC() * multiplier * 1e-3);
        printHeads.get("magenta").consumeInk(pixel.getM() * multiplier * 1e-3);
        printHeads.get("yellow").consumeInk(pixel.getY() * multiplier * 1e-3);
        printHeads.get("black").consumeInk(pixel.getK() * multiplier * 1e-3);
    }

    public boolean isAnyEmpty() {
        return printHeads.values().stream().anyMatch(PrintHead::isEmpty);
    }

    public Map<String, Double> getLevels() {
        Map<String, Double> levels = new HashMap<>();
        for (Map.Entry<String, PrintHead> entry : printHeads.entrySet()) {
            levels.put(entry.getKey(), entry.getValue().getInkLevel());
        }
        return levels;
    }

