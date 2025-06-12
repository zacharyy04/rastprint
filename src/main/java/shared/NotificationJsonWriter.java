package shared;

import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class NotificationJsonWriter {

    private static final String NOTIFICATION_DIR = "src/main/resources/notifications/";

    public static void writeNotification(JSONObject jsonObject, String fileName) {
        try {
            Files.createDirectories(Paths.get(NOTIFICATION_DIR));
            FileWriter writer = new FileWriter(NOTIFICATION_DIR + fileName);
            writer.write(jsonObject.toString(2)); // indent = 2
            writer.close();
            //System.out.println("Notification JSON générée : " + fileName);
        } catch (IOException e) {
            System.err.println("Erreur lors de l’écriture du fichier JSON : " + e.getMessage());
        }
    }
}
