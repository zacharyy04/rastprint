package shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;

public class JsonCommandWriter {

    public static void writePrintJobFile(String jobId, PrintParameters params, String bufferPath, String outputPath)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();

        root.put("command", "print_job");
        root.put("job_id", jobId);
        root.put("paper_format", params.getPaperFormat().name());
        root.put("orientation", params.getOrientation().name());
        root.put("copies", params.getNbCopies());
        root.put("color_mode", params.getColorMode().name().toLowerCase());
        root.put("quality", params.getQuality().name().toLowerCase());
        root.put("bitmap_buffer_path", bufferPath);

        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(outputPath), root);
    }
}
