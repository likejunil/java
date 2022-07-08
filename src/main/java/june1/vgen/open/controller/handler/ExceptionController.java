package june1.vgen.open.controller.handler;

import june1.vgen.open.common.exception.ClientException;
import june1.vgen.open.common.exception.ServerException;
import june1.vgen.open.controller.common.Error;
import june1.vgen.open.controller.common.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler
    public ResponseEntity<Response> handleClientException(ClientException e) {
        return ResponseEntity.badRequest()
                .body(Response.error(null, Error.by(e)));
    }

    @ExceptionHandler
    public ResponseEntity<Response> handleServerException(ServerException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error(null, Error.by(e)));
    }
}
