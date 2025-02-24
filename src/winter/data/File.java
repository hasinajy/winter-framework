package winter.data;

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
}
