package winter.util;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class FileUtil extends Utility {
    public static String generateTimestampFilename(String originalFilename) {
        Instant now = Instant.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS").withZone(ZoneOffset.UTC);
        String timestamp = formatter.format(now);

        if (originalFilename != null && !originalFilename.isEmpty()) {
            int lastDotIndex = originalFilename.lastIndexOf(".");
            if (lastDotIndex != -1) {
                String extension = originalFilename.substring(lastDotIndex);
                return "file_" + timestamp + extension;
            }
        }
        return "file_" + timestamp + ".bin"; // Default to .bin if no extension
    }
}
