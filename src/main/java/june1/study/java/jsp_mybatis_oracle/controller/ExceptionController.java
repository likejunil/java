package june1.study.java.jsp_mybatis_oracle.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(NoSuchElementException.class)
    protected ResponseEntity<ErrorResponse> handleNoSuchElement(
            Exception e, HttpServletRequest req) {
        log.error("url=[{}], NoSuchElement 예외 발생: [{}]",
                req.getRequestURL(), e.getMessage());
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.builder()
                        .exception(e.getClass().getSimpleName())
                        .message(e.getMessage())
                        .build());
    }

    @Getter
    @AllArgsConstructor
    @Builder
    static class ErrorResponse {
        private String exception;
        private String message;
    }
}
