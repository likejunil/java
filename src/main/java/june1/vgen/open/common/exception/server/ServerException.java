package june1.vgen.open.common.exception.server;

import june1.vgen.open.common.exception.CustomException;

public class ServerException extends CustomException {

    public ServerException() {
    }

    public ServerException(String message) {
        super(message);
    }

    public ServerException(String object, String field, String code, String rejectedValue, String message) {
        super(object, field, code, rejectedValue, message);
    }
}
