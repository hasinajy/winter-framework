package winter.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for scanning and listing files from a directory URL in the
 * Winter framework.
 * <p>
 * This class extends {@link Utility} and provides static methods to retrieve
 * and sort file
 * names from a specified directory URL. It is not intended to be instantiated,
 * and all
 * functionality is accessed statically.
 * </p>
 *
 * @author Hasina JY
 * @version 1.0.0
 * @since 1.0.0
 */
public class DirectoryScanner extends Utility {

    /**
     * Lists files and directories from a given URL resource.
     * <p>
     * Retrieves all entries from the specified directory URL, sorts them with files
     * appearing
     * before directories, and then alphabetically within each category. Entries
     * ending with "/"
     * are treated as directories, while others are treated as files.
     * </p>
     *
     * @param directory the URL pointing to the directory to scan
     * @return a sorted list of file and directory names
     * @throws IOException          if an I/O error occurs while reading the
     *                              directory
     * @throws NullPointerException if the directory parameter is null
     */
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