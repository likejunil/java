package june1.open.common.exception.server;

import june1.open.common.exception.CustomException;

//500 코드 종류의 예외
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
