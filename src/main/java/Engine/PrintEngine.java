package Engine;

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
        this.paperTray = new PaperTray(); // bac rempli
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

            int dpi = switch (quality) {
                case DRAFT -> 150;
                case STANDARD -> 300;
                case HIGH -> 600;
            };

            int[] dims = PrintDimensionHelper.getPixelDimensions(params.getPaperFormat(), params.getOrientation(), dpi);
            int width = dims[0];
            int height = dims[1];

            CMYKPixel[][] image = BitmapBufferHandler.readBuffer(path, width, height);

            if (!inkManager.hasSufficientInk(image, quality)) {
                jobMonitor.notifyObservers(new EngineEvent(
                        EngineEvent.Type.JOB_ERROR, jobId,
                        "Niveau d'encre insuffisant pour imprimer l'image.", null, 0
                ));
                return;
            }

            if (!paperTray.hasPaper()) {
                jobMonitor.notifyObservers(new EngineEvent(
                        EngineEvent.Type.JOB_ERROR, jobId,
                        "Bac à papier vide.", null, 0
                ));
                return;
            }

            paperTray.consumeSheet();
            jobMonitor.notifyObservers(new EngineEvent(EngineEvent.Type.JOB_STARTED, jobId));

            for (CMYKPixel[] row : image) {
                for (CMYKPixel pixel : row) {
                    inkManager.consume(pixel, quality);

                    inkManager.getLowColors().forEach(color -> {
                        jobMonitor.notifyObservers(new EngineEvent(
                                EngineEvent.Type.INK_LOW, jobId, null, color,
                                inkManager.getLevel(color)
                        ));
                    });

                    if (inkManager.isAnyEmpty()) {
                        jobMonitor.notifyObservers(new EngineEvent(
                                EngineEvent.Type.JOB_ERROR, jobId, "Cartouche vide", null, 0
                        ));
                        return;
                    }
                }
                hardwareSimulator.simulateLineDelay(quality);
            }

            jobMonitor.notifyObservers(new EngineEvent(EngineEvent.Type.JOB_COMPLETED, jobId, inkManager.getInkUsage()));

        } catch (Exception e) {
            jobMonitor.notifyObservers(new EngineEvent(EngineEvent.Type.JOB_ERROR, jobId, e.getMessage(), null, 0));
        }
    }
}
