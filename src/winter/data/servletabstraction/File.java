package winter.data.servletabstraction;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

import jakarta.servlet.http.Part;
import winter.data.exception.internal.FileSaveException;
import winter.util.DataUtil;

/**
 * An abstraction for handling file uploads in the Winter framework.
 * <p>
 * This class wraps a file uploaded via a {@link Part} object, providing
 * functionality to
 * generate a timestamped filename, store file content as bytes, and save the
 * file to a
 * specified path. It serves as a bridge between servlet-based file uploads and
 * framework logic.
 * </p>
 *
 * @author Hasina JY
 * @version 1.0.0
 * @since 1.0.0
 */
public class File {

    /** The generated or assigned filename for the uploaded file. */
    private String filename;

    /** The raw byte content of the uploaded file. */
    private byte[] bytes;

    /* ------------------------------ Constructors ------------------------------ */

    /**
     * Default constructor for an empty file object.
     * <p>
     * Initializes filename and bytes to null; use setters or the parameterized
     * constructor
     * to populate the object.
     * </p>
     */
    public File() {
    }

    /**
     * Constructs a file object from a servlet {@link Part}.
     * <p>
     * Generates a timestamped filename using
     * {@link DataUtil#generateTimestampFilename}
     * and reads the file content into a byte array from the part’s input stream.
     * </p>
     *
     * @param part the servlet part containing the uploaded file
     * @throws IOException if an error occurs while reading the file content
     */
    public File(Part part) throws IOException {
        this.setFilename(
                DataUtil.generateTimestampFilename(Paths.get(part.getSubmittedFileName()).getFileName().toString()));

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

    /**
     * Gets the filename of the uploaded file.
     *
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets the filename of the uploaded file.
     *
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Gets the byte content of the uploaded file.
     *
     * @return the byte array containing the file content
     */
    public byte[] getBytes() {
        return bytes;
    }

    /**
     * Sets the byte content of the uploaded file.
     *
     * @param bytes the byte array to set
     */
    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    /* ------------------------------ IO operations ----------------------------- */

    /**
     * Saves the file content to the specified path.
     * <p>
     * Writes the byte content to a file at the given path using the stored
     * filename.
     * The path should be a directory; the filename is appended to it.
     * </p>
     *
     * @param path the directory path where the file should be saved
     * @throws FileSaveException if the bytes are invalid (null or empty) or if an
     *                           I/O error occurs
     */
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