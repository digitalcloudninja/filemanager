package ninja.digitalcloud.cloud.filemanager.exception;

public class BadRequestError extends RuntimeException {
    public BadRequestError(String message) {
        super(message);
    }
}
