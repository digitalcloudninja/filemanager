package ninja.digitalcloud.cloud.filemanager.exception;

public class FileNotFoundError extends RuntimeException {

    private static final String FILE_NOT_FOUND = "File ID: '%s' was not found.";

    public FileNotFoundError(String uuid) {
        super(String.format(FILE_NOT_FOUND, uuid));
    }
}
