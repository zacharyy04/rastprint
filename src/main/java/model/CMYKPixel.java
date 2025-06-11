package model;

public class CMYKPixel {

    public CMYKPixel(double c, double m, double y, double k) {
        this.cyan = c;
        this.magenta = m;
        this.yellow = y;
        this.black = k;
    }

    public double cyan = 0;
    public double magenta = 0;
    public double yellow = 0;
    public double black = 0;

    public double getC() {
        return cyan;
    }
    public double getM() {
        return magenta;
    }
    public double getY() {
        return yellow;
    }
    public double getK() {
        return black;
    }
}
