package winter.data;

import java.io.FileOutputStream;
import java.io.IOException;

import winter.exception.FileSaveException;
import winter.util.FileUtil;

public class File {
    private String filename;
    private byte[] bytes;

    /* ------------------------------ Constructors ------------------------------ */
    public File() {
    }

    public File(String filename, byte[] bytes) {
        this.setFilename(filename);
        this.setBytes(bytes);
    }

    /* --------------------------- Getters and setters -------------------------- */
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    /* ------------------------------ IO operations ----------------------------- */
    public void save(String path) throws FileSaveException {
        if (bytes == null || bytes.length == 0) {
            throw new FileSaveException("Bytes are invalid.");
        }

        String actualFilename = (filename == null || filename.isEmpty()) ? FileUtil.generateTimestampFilename()
                : filename;

        java.io.File file = new java.io.File(path, actualFilename);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(bytes);
        } catch (IOException e) {
            throw new FileSaveException("Error saving file: " + e.getMessage(), e);
        }
    }
}
