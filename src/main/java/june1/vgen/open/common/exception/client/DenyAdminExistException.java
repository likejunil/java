package june1.vgen.open.common.exception.client;

import lombok.Builder;

public class DenyAdminExistException extends ClientException {

    public DenyAdminExistException() {
    }

    public DenyAdminExistException(String message) {
        super(message);
    }

    @Builder
    public DenyAdminExistException(String object, String field, String code, String rejectedValue, String message) {
        super(object, field, code, rejectedValue, message);
    }
}
