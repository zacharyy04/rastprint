package ui;

import Engine.InkManager;
import Engine.PaperTray;
import Engine.JobMonitor;
import Engine.PrintEngine;

import java.util.Map;
import java.util.Scanner;

public class PrinterStateTest {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        JobMonitor monitor = new JobMonitor();
        PrintEngine engine = new PrintEngine(monitor);
        InkManager inkManager = engine.getInkManager();
        PaperTray paperTray = engine.getPaperTray();

        System.out.println("=== TEST ÉTATS CRITIQUES DE L'IMPRIMANTE ===");
        System.out.println("1️⃣ Encre VIDE");
        System.out.println("2️⃣ Encre PRESQUE VIDE");
        System.out.println("3️⃣ PLUS DE PAPIER");
        System.out.println("4️⃣ RESTE 3 FEUILLES");
        System.out.print("Choisissez un test (1 à 4) : ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> {
                System.out.println("🔧 Simulation : Cartouche cyan VIDE");
                engine.getInkManager().getPrintHead("cyan").setInkLevel(0);
            }
            case 2 -> {
                System.out.println("🔧 Simulation : Cartouche magenta presque vide (5%)");
                engine.getInkManager().getPrintHead("magenta").setInkLevel(0.4);
            }
            case 3 -> {
                System.out.println("🔧 Simulation : Bac à papier VIDE");
                paperTray.setSheets(0);
            }
            case 4 -> {
                System.out.println("🔧 Simulation : Bac à papier avec 3 feuilles restantes");
                paperTray.setSheets(3);
            }
            default -> System.out.println("⛔ Choix invalide");
        }

        System.out.println("📊 Niveaux d'encre actuels :");
        for (Map.Entry<String, Double> entry : inkManager.getLevels().entrySet()) {
            System.out.printf("  %s : %.2f ml%n", entry.getKey(), entry.getValue());
        }

        System.out.println("📄 Feuilles restantes : " + paperTray.getRemainingSheets());
    }
}
