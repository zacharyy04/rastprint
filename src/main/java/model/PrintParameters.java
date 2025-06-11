package model;

import model.Enums.*;

public class PrintParameters {
    private PaperFormat paperFormat;
    private Orientation orientation;
    private int nbCopies;
    private ColorMode colorMode;
    private PrintQuality quality;
    private Alignment alignment;
    private int imagesPerPage;
    private String bufferPath;
    private String align;
    private int dpi;

    private int width;
    private int height;

    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }


    // Getters et Setters
    public PaperFormat getPaperFormat() { return paperFormat; }
    public void setPaperFormat(PaperFormat paperFormat) { this.paperFormat = paperFormat; }

    public Orientation getOrientation() { return orientation; }
    public void setOrientation(Orientation orientation) { this.orientation = orientation; }

    public int getNbCopies() { return nbCopies; }
    public void setNbCopies(int nbCopies) { this.nbCopies = nbCopies; }

    public ColorMode getColorMode() { return colorMode; }
    public void setColorMode(ColorMode colorMode) { this.colorMode = colorMode; }

    public PrintQuality getQuality() { return quality; }
    public void setQuality(PrintQuality quality) { this.quality = quality; }

    public Alignment getAlignment() { return alignment; }
    public void setAlignment(Alignment alignment) { this.alignment = alignment; }

    public int getImagesPerPage() { return imagesPerPage; }
    public void setImagesPerPage(int imagesPerPage) { this.imagesPerPage = imagesPerPage; }

    public String getBufferPath() { return bufferPath; }
    public void setBufferPath(String bufferPath) { this.bufferPath = bufferPath; }

    public int getDpi() {
        return dpi;
    }

    public void setDpi(int dpi) {
        this.dpi = dpi;
    }

    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align;
    }
}
