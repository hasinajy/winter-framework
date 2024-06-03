package winter.utils;

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

        return fileNames;
    }
}
