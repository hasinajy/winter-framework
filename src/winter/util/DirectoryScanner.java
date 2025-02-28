package winter.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DirectoryScanner extends Utility {
    public static List<String> listFiles(URL directory) throws IOException {
        List<String> fileNames = new ArrayList<>();

        try (var in = directory.openStream();
                var reader = new BufferedReader(new InputStreamReader(in))) {

            String line;

            while ((line = reader.readLine()) != null) {
                fileNames.add(line);
            }
        }

        fileNames.sort((a, b) -> {
            boolean aIsFile = !a.endsWith("/");
            boolean bIsFile = !b.endsWith("/");

            if (aIsFile && !bIsFile) {
                return -1; // a is a file, b is a directory, so a comes first
            } else if (!aIsFile && bIsFile) {
                return 1; // a is a directory, b is a file, so b comes first
            } else {
                return a.compareTo(b); // Both are the same type, sort alphabetically
            }
        });

        return fileNames;
    }
}
