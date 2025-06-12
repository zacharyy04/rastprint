package test;

import Controller.NotificationListener;
import Controller.PrintController;
import Engine.JobMonitor;
import Engine.PrintEngine;
import Controller.PrintJob;
import shared.JsonCommandReader;

public class TestPartTwo {
    public static void main(String[] args) {
        try {
            System.out.println("🖨️  Démarrage de l'impression...");

            // 1. Lire le fichier JSON de commande
            String jsonPath = "src/main/resources/jobs/print-job.json";
            PrintJob job = JsonCommandReader.readPrintJob(jsonPath);

            // 2. Instancier les composants
            JobMonitor monitor = new JobMonitor();
            PrintEngine engine = new PrintEngine(monitor);
            engine.getInkManager().resetInk();

            PrintController controller = new PrintController(engine, monitor);
            NotificationListener listener = new NotificationListener(controller);

            // 3. Enregistrer l'observateur
            monitor.registerObserver(listener);

            // 4. Soumettre le job d'impression
            controller.submitJob(job);

        } catch (Exception e) {
            System.err.println("❌ Erreur dans Main : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
