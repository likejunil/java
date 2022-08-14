package june1.open.common.exception.client;

import lombok.Builder;

public class NoSuchCompanyException extends ClientException {

    public NoSuchCompanyException() {
    }

    public NoSuchCompanyException(String message) {
        super(message);
    }

    @Builder
    public NoSuchCompanyException(String object, String field, String code, String rejectedValue, String message) {
        super(object, field, code, rejectedValue, message);
    }
}
