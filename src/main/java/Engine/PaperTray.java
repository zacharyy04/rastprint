package Engine;

public class PaperTray {
    private int sheets = 500;

    public boolean hasPaper() {
        return sheets > 0;
    }

    public void consumeSheet() {
        if (sheets > 0) sheets--;
    }

    public int getRemainingSheets() {
        return sheets;
    }

    public boolean hasEnoughPaper(int copies) {
        return sheets >= copies;
    }

}


