package june1.open.common.exception.client;

import june1.open.common.exception.CustomException;

//400 코드 종류의 예외
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
