    package Engine;

    import model.CMYKPixel;
    import model.Enums.PrintQuality;
    import Controller.PrintJob;
    import model.PrintParameters;
    import shared.BitmapBufferHandler;
    import shared.FinalPageRenderer;
    import shared.PrintDimensionHelper;

    import java.io.File;
    import Controller.ImageProcessor; // si ce n’est pas encore fait




    public class PrintEngine {

        private final InkManager inkManager;
        private final PaperTray paperTray;
        private final HardwareSimulator hardwareSimulator;
        private final JobMonitor jobMonitor;
        private final ImageProcessor imageProcessor;

        public PrintEngine(JobMonitor jobMonitor) {
            this.inkManager = new InkManager();
            this.paperTray = new PaperTray(); // bac rempli
            this.hardwareSimulator = new HardwareSimulator();
            this.jobMonitor = jobMonitor;
            this.imageProcessor = new ImageProcessor(); // ou injecté si tu préfères
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

                int width = params.getWidth();   // ✅ remplacer
                int height = params.getHeight();
                CMYKPixel[][] rawImage = BitmapBufferHandler.readBuffer(path, width, height);
                CMYKPixel[][] image = imageProcessor.applyLayout(params, rawImage);

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

                int copies = params.getNbCopies();
                for (int copy = 1; copy <= copies; copy++) {
                    System.out.println("🖨️ Impression de la copie " + copy + " / " + copies);
                    for (CMYKPixel[] row : image) {
                        for (CMYKPixel pixel : row) {
                            inkManager.consume(pixel, quality);
                        }
                    }
                    hardwareSimulator.simulateLineDelay(quality);
                    paperTray.consumeSheet();



                jobMonitor.notifyObservers(new EngineEvent(EngineEvent.Type.JOB_COMPLETED, jobId, inkManager.getInkUsage()));

                String outputPath = "src/main/resources/output/" + jobId + "_copy" + copy + ".png";
                FinalPageRenderer.renderToPNG(image, params, outputPath);
            }

            } catch (Exception e) {
                jobMonitor.notifyObservers(new EngineEvent(EngineEvent.Type.JOB_ERROR, jobId, e.getMessage(), null, 0));
            }

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
        public InkManager getInkManager() {
            return inkManager;
        }


    }
