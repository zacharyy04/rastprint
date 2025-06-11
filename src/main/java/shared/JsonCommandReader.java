package shared;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import Controller.PrintJob;
import model.Enums.*;
import model.PrintParameters;

import java.io.File;
import java.io.IOException;

public class JsonCommandReader {

    public static PrintJob readPrintJob(String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(path));

        String jobId = root.get("job_id").asText();
        String bufferPath = root.get("bitmap_buffer_path").asText();

        PrintParameters params = new PrintParameters();
        params.setPaperFormat(PaperFormat.valueOf(root.get("paper_format").asText()));
        params.setOrientation(Orientation.valueOf(root.get("orientation").asText()));
        params.setNbCopies(root.get("copies").asInt());
        params.setColorMode(ColorMode.valueOf(root.get("color_mode").asText().toUpperCase()));
        params.setQuality(PrintQuality.valueOf(root.get("quality").asText().toUpperCase()));
        params.setBufferPath(bufferPath);

        return new PrintJob(jobId, params);
    }
}
