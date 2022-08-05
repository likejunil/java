package june1.vgen.open.common.exception.client;

import june1.vgen.open.common.exception.CustomException;

public class ClientException extends CustomException {

    public ClientException() {
    }

    public ClientException(String message) {
        super(message);
    }

    public ClientException(String object, String field, String code, String rejectedValue, String message) {
        super(object, field, code, rejectedValue, message);
    }
}
