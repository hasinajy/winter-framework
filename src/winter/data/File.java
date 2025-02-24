package winter.data;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

import jakarta.servlet.http.Part;
import winter.exception.FileSaveException;
import winter.util.FileUtil;

public class File {
    private String filename;
    private byte[] bytes;

    /* ------------------------------ Constructors ------------------------------ */
    public File() {
    }

    public File(Part part) throws IOException {
        this.setFilename(
                FileUtil.generateTimestampFilename(Paths.get(part.getSubmittedFileName()).getFileName().toString()));

        try (InputStream fileContent = part.getInputStream();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileContent.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            this.setBytes(byteArrayOutputStream.toByteArray());
        }
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

        java.io.File file = new java.io.File(path, this.getFilename());

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(bytes);
        } catch (IOException e) {
            throw new FileSaveException("Error saving file: " + e.getMessage(), e);
        }
    }
}
