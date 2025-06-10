package Controller;

import model.PrintParameter;
import model.PrintJobStatus;
import java.util.UUID;

public class PrintJob {
    private final String jobId;
    private final PrintParameters parameters;
    private PrintJobState state;
    private PrintJobStatus status;

    public PrintJob(PrintParameters parameters) {
        this.jobId = "job_" + UUID.randomUUID();
        this.parameters = parameters;
        this.state = new states.NewState();
        this.status = PrintJobStatus.INACTIVE;
    }

    public String getJobId() {
        return jobId;
    }

    public PrintParameters getParameters() {
        return parameters;
    }

    public void setState(PrintJobState state) {
        this.state = state;
    }

    public PrintJobState getState() {
        return state;
    }

    public PrintJobStatus getStatus() {
        return status;
    }

    public void transition() {
        state.handle(this);
    }
}
