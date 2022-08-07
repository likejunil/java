package june1.vgen.open.common.exception.client;

import lombok.Builder;

public class CompanyExistException extends ClientException {

    public CompanyExistException() {
    }

    public CompanyExistException(String message) {
        super(message);
    }

    @Builder
    public CompanyExistException(String object, String field, String code, String rejectedValue, String message) {
        super(object, field, code, rejectedValue, message);
    }
}
