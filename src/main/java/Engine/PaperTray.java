package Engine;

public class PaperTray {
    private int sheets = 50;

    public boolean hasPaper() {
        return sheets > 0;
    }

    public void consumeSheet() {
        if (sheets > 0) sheets--;
    }

    public int getRemainingSheets() {
        return sheets;
    }
}

