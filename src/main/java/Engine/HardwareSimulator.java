package Engine;

import model.Enums.PrintQuality;

public class HardwareSimulator {
    public void simulateLineDelay(PrintQuality quality) {
        try {
            long delay = switch (quality) {
                case DRAFT -> 1000;     // 1 ms par ligne
                case STANDARD -> 3000;  // 3 ms
                case HIGH -> 5000;      // 5 ms
            };
            Thread.sleep(delay);
        } catch (InterruptedException ignored) {
            // Dans un vrai système, on pourrait logger une interruption
        }
    }
    public boolean simulatePaperJamRisk() {
        // simulation erreur
        return Math.random() < 0.01;
    }
}
