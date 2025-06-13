package test;

import Controller.ImageProcessor;
import Controller.NotificationListener;
import Controller.PrintController;
import Controller.PrintJob;
import Engine.JobMonitor;
import Engine.PrintEngine;
import model.CMYKPixel;
import model.Enums.*;
import model.PrintParameters;
import shared.BitmapBufferHandler;
import shared.JsonCommandReader;
import shared.JsonCommandWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TEST2 {
    public static void main(String[] args) {
        int testCase = 2; // 👈 Changez cette valeur (1 à 4) selon le scénario souhaité

        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String jobId = "job_" + timestamp;
            String imagePath = "src/main/resources/images/100x100rouge.png";
            String bufferPath = "src/main/resources/jobs/" + jobId + "_buffer.bin";
            String jsonPath = "src/main/resources/jobs/" + jobId + ".json";

            BufferedImage image = ImageIO.read(new File(imagePath));

            PrintParameters params = new PrintParameters();
            params.setPaperFormat(PaperFormat.A4);
            params.setOrientation(Orientation.PORTRAIT);
            params.setColorMode(ColorMode.COLOR);
            params.setQuality(PrintQuality.STANDARD);
            params.setNbCopies(1);
            params.setImagesPerPage(1);
            params.setWidth(image.getWidth());
            params.setHeight(image.getHeight());
            params.setBufferPath(bufferPath);

            CMYKPixel[][] cmyk = new ImageProcessor().convertToCMYK(image, params);
            BitmapBufferHandler.writeBuffer(cmyk, bufferPath);
            JsonCommandWriter.writePrintJobFile(jobId, params, bufferPath, jsonPath);

            PrintJob job = JsonCommandReader.readPrintJob(jsonPath);
            JobMonitor monitor = new JobMonitor();
            PrintEngine engine = new PrintEngine(monitor);

            // 🧪 Scénarios de test
            switch (testCase) {
                case 1 -> { // Cartouche vide
                    engine.getInkManager().getPrintHead("cyan").setInkLevel(0);
                    System.out.println("🧪 Scénario : Cartouche CYAN vide");
                }
                case 2 -> { // Cartouche presque vide
                    engine.getInkManager().getPrintHead("magenta").setInkLevel(0.2);
                    System.out.println("🧪 Scénario : Cartouche MAGENTA presque vide");
                }
                case 3 -> { // Plus de papier
                    engine.getPaperTray().setSheets(0);
                    System.out.println("🧪 Scénario : Plus de papier dans le bac");
                }
                case 4 -> { // 3 feuilles restantes
                    engine.getPaperTray().setSheets(3);
                    System.out.println("🧪 Scénario : 3 feuilles restantes");
                }
                default -> System.out.println("🔁 Aucun scénario sélectionné");
            }

            PrintController controller = new PrintController(engine, monitor);
            NotificationListener listener = new NotificationListener(controller);
            monitor.registerObserver(listener);
            controller.submitJob(job);

        } catch (Exception e) {
            System.err.println("❌ Erreur test : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
