package Engine;

import model.Enums.PrintQuality;

public class HardwareSimulator {
    public void simulateLineDelay(PrintQuality quality) {
        try {
            long delay = switch (quality) {
                case DRAFT -> 1L;     // 1 ms par ligne
                case STANDARD -> 3L;  // 3 ms
                case HIGH -> 5L;      // 5 ms
            };
            Thread.sleep(delay);
        } catch (InterruptedException ignored) {
            // Dans un vrai système, on pourrait logger une interruption
        }
    }
    public boolean simulatePaperJamRisk() {
        // Placeholder : 1% de risque de bourrage
        return Math.random() < 0.01;
    }
}
