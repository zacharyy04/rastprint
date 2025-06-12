package ui;

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
import shared.JsonCommandWriter;
import shared.JsonCommandReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class ConsoleInterface {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String jobId = "job_" + timestamp;

            System.out.print("📂 Chemin de l'image à imprimer (PNG/JPG/TIFF) : ");
            String imagePath = scanner.nextLine().trim();
            BufferedImage image = ImageIO.read(new File(imagePath));

            PrintParameters params = new PrintParameters();

            params.setPaperFormat(askEnum(scanner, "Format papier (A4/A3) :", PaperFormat.class));
            params.setOrientation(askEnum(scanner, "Orientation (PORTRAIT/LANDSCAPE) :", Orientation.class));
            params.setColorMode(askEnum(scanner, "Mode couleur (COLOR/GRAYSCALE) :", ColorMode.class));
            params.setQuality(askEnum(scanner, "Qualité (DRAFT/STANDARD/HIGH) :", PrintQuality.class));
            System.out.print("Nombre de copies (1 à 50) : ");
            params.setNbCopies(scanner.nextInt());
            scanner.nextLine(); // flush
            /*System.out.print("Nombre d’images par feuille (1, 2 ou 4) : ");
            params.setImagesPerPage(scanner.nextInt());
            scanner.nextLine(); // flush
            params.setAlignment(askEnum(scanner, "Alignement (TOP, BOTTOM, LEFT, RIGHT, CENTER) :", Alignment.class));
             */

            scanner.nextLine(); // flush

            params.setWidth(image.getWidth());
            params.setHeight(image.getHeight());

            String bufferPath = "src/main/resources/jobs/" + jobId + "_buffer.bin";
            String jsonPath = "src/main/resources/jobs/" + jobId + ".json";
            params.setBufferPath(bufferPath);

            ImageProcessor processor = new ImageProcessor();
            CMYKPixel[][] cmyk = processor.convertToCMYK(image, params);
            BitmapBufferHandler.writeBuffer(cmyk, bufferPath);
            JsonCommandWriter.writePrintJobFile(jobId, params, bufferPath, jsonPath);

            PrintJob job = JsonCommandReader.readPrintJob(jsonPath);
            JobMonitor monitor = new JobMonitor();
            PrintEngine engine = new PrintEngine(monitor);
            engine.getInkManager().resetInk();
            PrintController controller = new PrintController(engine, monitor);
            //NotificationListener listener = new NotificationListener(controller);
            //monitor.registerObserver(listener);

            controller.submitJob(job);

        } catch (Exception e) {
            System.err.println("❌ Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static <T extends Enum<T>> T askEnum(Scanner scanner, String prompt, Class<T> enumClass) {
        while (true) {
            try {
                System.out.print(prompt + " ");
                String input = scanner.nextLine().trim().toUpperCase();
                return Enum.valueOf(enumClass, input);
            } catch (IllegalArgumentException e) {
                System.out.println("⛔ Entrée invalide. Essayez encore.");
            }
        }
    }
}
