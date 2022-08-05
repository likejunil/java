package june1.vgen.open.controller.common;

import june1.vgen.open.common.exception.CustomException;
import june1.vgen.open.common.exception.auth.AuthException;
import june1.vgen.open.common.exception.client.ClientException;
import june1.vgen.open.common.exception.server.ServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

import static june1.vgen.open.common.ConstantInfo.CODE_DTO;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionController {

    private final MessageSource messageSource;

    //요청 json 을 dto 객체로 변환하는 과정에서 발생하는 에러 처리..
    //MessageSource 객체를 사용하여 에러 메시지를 얻을 수 있다.
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<Response> handleBindException(BindingResult bindingResult, HttpServletRequest req) {
        log.error("바인딩 예외가 발생했습니다.[{}]", req.getRequestURI());
        return ResponseEntity.badRequest()
                .body(Response.error(null, bindingResult, messageSource));
    }

    //요청 dto 에 enum 이 포함되어 있을 때..
    //해당 enum 의 데이터가 아닌 잘못된 데이터로 dto 에 담아 요청했을 경우..
    //다음과 같이 HttpMessageNotReadableException 이 발생한다.
    @ExceptionHandler
    protected ResponseEntity<Response> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e, HttpServletRequest req) {
        log.error("요청 DTO 의 정보를 읽지 못했습니다.[{}]", req.getRequestURI());
        String message = e.getMessage() != null ? e.getMessage().split(":")[0] : null;
        return ResponseEntity.badRequest()
                .body(Response.error(null, Error.builder()
                        .code(CODE_DTO)
                        .defaultMessage(message)
                        .build()));
    }

    //페이징 처리 요청을 받았을 때..
    //정렬을 요구하는 컬럼이 존재하지 않는 경우..
    //(존재하지 않는 컬럼으로 정렬을 요구한 경우)
    @ExceptionHandler
    protected ResponseEntity<Response> handlePropertyReferenceException(
            PropertyReferenceException e, HttpServletRequest req) {
        log.error("요청하신 데이터에 대한 정렬 기준이 명확하지 않습니다.[{}]", req.getRequestURI());
        String message = e.getMessage() != null ? e.getMessage().split(":")[0] : null;
        return ResponseEntity.badRequest()
                .body(Response.error(null, Error.builder()
                        .code(CODE_DTO)
                        .defaultMessage(message)
                        .build()));
    }

    @ExceptionHandler
    protected ResponseEntity<Response> handleAuthException(AuthException e, HttpServletRequest req) {
        log.error("인증 예외가 발생했습니다.[{}]", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Response.error(null, Error.by(e)));
    }

    @ExceptionHandler
    protected ResponseEntity<Response> handleClientException(ClientException e, HttpServletRequest req) {
        log.error("클라이언트 예외가 발생했습니다.[{}]", req.getRequestURI());
        return ResponseEntity.badRequest()
                .body(Response.error(null, Error.by(e)));
    }

    @ExceptionHandler
    protected ResponseEntity<Response> handleServerException(ServerException e, HttpServletRequest req) {
        log.error("서버 예외가 발생했습니다.[{}]", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error(null, Error.by(e)));
    }

    @ExceptionHandler
    protected ResponseEntity<Response> handleCustomException(CustomException e, HttpServletRequest req) {
        log.error("서비스 예외가 발생했습니다.[{}]", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error(null, Error.by(e)));
    }
}
