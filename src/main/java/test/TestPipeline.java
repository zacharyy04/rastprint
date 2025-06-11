package test;

import Controller.ImageProcessor;
import model.CMYKPixel;
import model.Enums.*;
import model.PrintParameters;
import shared.BitmapBufferHandler;
import shared.JsonCommandWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class TestPipeline {
    public static void main(String[] args) {
        try {
            // 1. Définir les paramètres d'impression
            PrintParameters params = new PrintParameters();
            params.setPaperFormat(PaperFormat.A4);
            params.setOrientation(Orientation.PORTRAIT);
            params.setColorMode(ColorMode.COLOR);
            params.setQuality(PrintQuality.STANDARD);
            params.setNbCopies(1);
            params.setImagesPerPage(1);
            String jobId = "job_test_001";

            // 2. Charger l'image PNG
            System.out.println(" Chargement de l'image PNG...");
            BufferedImage image = ImageIO.read(new File("src/main/resources/images/100x100rouge.png"));
            params.setWidth(image.getWidth());
            params.setHeight(image.getHeight());

            // 3. Convertir en pixels CMYK
            ImageProcessor processor = new ImageProcessor();
            CMYKPixel[][] cmyk = processor.convertToCMYK(image, params);

            // 4. Sauvegarder le buffer
            String bufferPath = "src/main/resources/jobs/job_test_buffer.bin";
            BitmapBufferHandler.writeBuffer(cmyk, bufferPath);
            params.setBufferPath(bufferPath);

            // 5. Générer le fichier JSON de commande
            String jsonPath = "src/main/resources/jobs/print-job.json";
            JsonCommandWriter.writePrintJobFile(jobId, params, bufferPath, jsonPath);

            System.out.println("✅ PNG traité : bin + json générés");

        } catch (Exception e) {
            System.err.println("❌ Erreur pipeline PNG : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
