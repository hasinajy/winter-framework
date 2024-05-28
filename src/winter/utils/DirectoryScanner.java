package winter.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class DirectoryScanner {

    public static ArrayList<String> listFiles(URL directory) throws IOException {
        ArrayList<String> fileNames = new ArrayList<>();
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
