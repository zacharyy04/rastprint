    package Engine;

    import model.CMYKPixel;
    import model.Enums.PrintQuality;
    import Controller.PrintJob;
    import model.PrintParameters;
    import shared.BitmapBufferHandler;
    import shared.FinalPageRenderer;
    import java.io.File;
    import java.util.Map;
    import Controller.ImageProcessor;




    /**
     * The {@code PrintEngine} class manages the full lifecycle of a print job.
     * It is responsible for verifying resources (ink, paper), triggering ink consumption,
     * rendering the image, and sending updates to the {@link JobMonitor}.
     * It simulates a raster-based print system, including multi-copy printing and hardware interaction.
     */
    public class PrintEngine {

        private final InkManager inkManager;
        private final PaperTray paperTray;
        private final HardwareSimulator hardwareSimulator;
        private final JobMonitor jobMonitor;
        private final ImageProcessor imageProcessor;

        /**
         * Constructs a new {@code PrintEngine} instance linked to a job monitor.
         *
         * @param jobMonitor the monitor used to notify observers of job events
         */
        public PrintEngine(JobMonitor jobMonitor) {
            this.inkManager = new InkManager();
            this.paperTray = new PaperTray();
            this.hardwareSimulator = new HardwareSimulator();
            this.jobMonitor = jobMonitor;
            this.imageProcessor = new ImageProcessor();
        }

        /**
         * Starts a print job by validating resources (ink and paper), converting the buffer to CMYK layout,
         * and simulating the print process for the specified number of copies.
         *
         * @param job the {@link PrintJob} to be processed
         */
        public void startJob(PrintJob job) {
            String jobId = job.getJobId();
            PrintParameters params = job.getParameters();
            String path = params.getBufferPath();
            PrintQuality quality = params.getQuality();
            int copies = params.getNbCopies();

            try {
                File file = new File(path);
                if (!file.exists()) throw new Exception("Buffer image not found: " + path);

                int dpi = switch (quality) {
                    case DRAFT -> 150;
                    case STANDARD -> 300;
                    case HIGH -> 600;
                };

                int width = params.getWidth();
                int height = params.getHeight();
                CMYKPixel[][] rawImage = BitmapBufferHandler.readBuffer(path, width, height);
                CMYKPixel[][] image = imageProcessor.applyLayout(params, rawImage);

                Map<String, Double> needed = inkManager.estimateInkUsage(image, quality);

                boolean enough = needed.entrySet().stream().allMatch(entry -> {
                    String color = entry.getKey();
                    double volumeNeeded = entry.getValue() * copies;
                    double available = inkManager.getLevel(color) * 1_000_000;
                    return volumeNeeded <= available;
                });

                if (!enough) {
                    jobMonitor.notifyObservers(new EngineEvent(
                            EngineEvent.Type.JOB_ERROR,
                            jobId,
                            "Insufficient ink for " + copies + " copies.",
                            null,
                            0
                    ));
                    return;
                }

                if (!paperTray.hasPaper()) {
                    jobMonitor.notifyObservers(new EngineEvent(
                            EngineEvent.Type.JOB_ERROR, jobId,
                            "Paper tray is empty.", null, 0
                    ));
                    return;
                }

                if (!paperTray.hasEnoughPaper(copies)) {
                    jobMonitor.notifyObservers(new EngineEvent(
                            EngineEvent.Type.JOB_ERROR,
                            jobId,
                            "Not enough paper for " + copies + " copies.",
                            null,
                            0
                    ));
                    return;
                }

                jobMonitor.notifyObservers(new EngineEvent(EngineEvent.Type.JOB_STARTED, jobId));

                for (int copy = 1; copy <= copies; copy++) {
                    System.out.println("🖨️ Printing copy " + copy + " / " + copies);
                    for (CMYKPixel[] row : image) {
                        for (CMYKPixel pixel : row) {
                            inkManager.consume(pixel, quality);
                        }
                    }

                    hardwareSimulator.simulateLineDelay(quality);
                    paperTray.consumeSheet();

                    jobMonitor.notifyObservers(new EngineEvent(
                            EngineEvent.Type.JOB_COMPLETED, jobId, inkManager.getInkUsage()
                    ));

                    String outputPath = "src/main/resources/output/" + jobId + "_copy" + copy + ".png";
                    FinalPageRenderer.renderToPNG(image, params, outputPath);
                }

            } catch (Exception e) {
                jobMonitor.notifyObservers(new EngineEvent(
                        EngineEvent.Type.JOB_ERROR, jobId, e.getMessage(), null, 0
                ));
            }

            inkManager.getLowColors().forEach(color -> {
                jobMonitor.notifyObservers(new EngineEvent(
                        EngineEvent.Type.INK_LOW, jobId, null, color,
                        inkManager.getLevel(color)
                ));
            });

            if (inkManager.isAnyEmpty()) {
                jobMonitor.notifyObservers(new EngineEvent(
                        EngineEvent.Type.JOB_ERROR, jobId, "Cartridge empty", null, 0
                ));
            }
        }

        /**
         * Returns the ink manager handling ink usage and level tracking.
         *
         * @return the {@link InkManager} instance
         */
        public InkManager getInkManager() {
            return inkManager;
        }

        /**
         * Returns the paper tray manager.
         *
         * @return the {@link PaperTray} instance
         */
        public PaperTray getPaperTray() {
            return paperTray;
        }
    }
