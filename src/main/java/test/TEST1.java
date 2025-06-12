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

public class TEST1 {
    public static void main(String[] args) {
        try {
            // 🕒 Générer un identifiant unique avec date et heure
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String jobId = "job_" + timestamp;

            // 📁 Définir les chemins de fichier dynamiques
            String imagePath = "src/main/resources/images/100x100rouge.png";
            String bufferPath = "src/main/resources/jobs/" + jobId + "_buffer.bin";
            String jsonPath = "src/main/resources/jobs/" + jobId + ".json";

            // 🖼️ 1. Charger l'image PNG
            System.out.println("🔄 Chargement de l'image PNG...");
            BufferedImage image = ImageIO.read(new File(imagePath));

            // 🧾 2. Définir les paramètres d'impression
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

            // 🎨 3. Conversion RGB → CMYK
            ImageProcessor processor = new ImageProcessor();
            CMYKPixel[][] cmyk = processor.convertToCMYK(image, params);

            // 💾 4. Sauvegarder le buffer CMYK
            BitmapBufferHandler.writeBuffer(cmyk, bufferPath);

            // 📤 5. Générer le JSON de commande
            JsonCommandWriter.writePrintJobFile(jobId, params, bufferPath, jsonPath);

            System.out.println("✅ Fichiers générés : " + bufferPath + " + " + jsonPath);

            // 🖨️ 6. Lire la commande et lancer l'impression
            PrintJob job = JsonCommandReader.readPrintJob(jsonPath);
            JobMonitor monitor = new JobMonitor();
            PrintEngine engine = new PrintEngine(monitor);
            engine.getInkManager().resetInk();

            PrintController controller = new PrintController(engine, monitor);
            NotificationListener listener = new NotificationListener(controller);
            monitor.registerObserver(listener);

            controller.submitJob(job);

        } catch (Exception e) {
            System.err.println("❌ Erreur pipeline complet : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
