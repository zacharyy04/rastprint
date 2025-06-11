package Engine;

import Engine.EngineEvent.Type;
import Engine.JobMonitor;
import model.CMYKPixel;
import model.Enums.PrintQuality;
import Controller.PrintJob;
import model.PrintParameters;
import shared.BitmapBufferHandler;
import shared.PrintDimensionHelper;

import java.io.File;

public class PrintEngine {

    private final InkManager inkManager;
    private final PaperTray paperTray;
    private final HardwareSimulator hardwareSimulator;
    private final JobMonitor jobMonitor;

    public PrintEngine(JobMonitor jobMonitor) {
        this.inkManager = new InkManager();
        this.paperTray = new PaperTray(); // bac rempli avec 100 feuilles
        this.hardwareSimulator = new HardwareSimulator();
        this.jobMonitor = jobMonitor;
    }

    public void startJob(PrintJob job) {
        String jobId = job.getJobId();
        PrintParameters params = job.getParameters();
        String path = params.getBufferPath();
        PrintQuality quality = params.getQuality();

        try {
            File file = new File(path);
            if (!file.exists()) throw new Exception("Buffer image non trouvé : " + path);

            // Dimensions attendues en pixels
            int dpi = switch (quality) {
                case DRAFT -> 150;
                case STANDARD -> 300;
                case HIGH -> 600;
            };

            int[] dims = PrintDimensionHelper.getPixelDimensions(
                    params.getPaperFormat(),
                    params.getOrientation(),
                    dpi
            );
            int width = dims[0];
            int height = dims[1];

            // Lecture du buffer binaire
            CMYKPixel[][] image = BitmapBufferHandler.readBuffer(path, width, height);

            // Vérification papier
            if (!paperTray.hasPaper()) throw new Exception("Bac à papier vide.");
            paperTray.consumeSheet();

            // Notification début
            jobMonitor.notifyObservers(new EngineEvent(Type.JOB_STARTED, jobId));

            // Impression pixel par pixel
            for (CMYKPixel[] row : image) {
                for (CMYKPixel pixel : row) {
                    inkManager.consume(pixel, quality);

                    // Vérifier les niveaux
                    inkManager.getLowColors().forEach(color -> {
                        jobMonitor.notifyObservers(new EngineEvent(
                                Type.INK_LOW, jobId, null, color,
                                inkManager.getLevel(color)
                        ));
                    });

                    if (inkManager.isAnyEmpty()) {
                        jobMonitor.notifyObservers(new EngineEvent(
                                Type.JOB_ERROR, jobId, "Une cartouche est vide", null, 0
                        ));
                        return;
                    }
                }
                hardwareSimulator.simulateLineDelay(quality);
            }

            // Job terminé
            jobMonitor.notifyObservers(new EngineEvent(Type.JOB_COMPLETED, jobId, inkManager.getInkUsage()));

        } catch (Exception e) {
            jobMonitor.notifyObservers(new EngineEvent(Type.JOB_ERROR, job.getJobId(), e.getMessage(), null, 0));
        }
    }
}
