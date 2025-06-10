package Engine;

public class PrintHead {
    private final String color;
    private double inkLevel; // in milliliters

    public PrintHead(String color, double initialLevel) {
        this.color = color;
        this.inkLevel = initialLevel;
    }

    public String getColor() {
        return color;
    }

    public double getInkLevel() {
        return inkLevel;
    }

    public boolean isLow() {
        return inkLevel < 1.0;
    }

    public boolean isEmpty() {
        return inkLevel <= 0.0;
    }

    public void consumeInk(double amount) {
        this.inkLevel = Math.max(0.0, inkLevel - amount);
    }
}
