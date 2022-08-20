package june1.db.common.exception;

public class DbConnFailException extends CustomException {

    public DbConnFailException(String message) {
        super(message);
    }

    public DbConnFailException(String message, Throwable cause) {
        super(message, cause);
    }
}
