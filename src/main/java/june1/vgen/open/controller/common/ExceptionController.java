package june1.vgen.open.controller.common;

import june1.vgen.open.common.exception.CustomException;
import june1.vgen.open.common.exception.auth.NotAcceptableException;
import june1.vgen.open.common.exception.auth.WrongAuthException;
import june1.vgen.open.common.exception.auth.WrongAuthenticationException;
import june1.vgen.open.common.exception.auth.WrongAuthoritiesException;
import june1.vgen.open.common.exception.client.ClientException;
import june1.vgen.open.common.exception.server.ServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.context.MessageSource;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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
    protected ResponseEntity<Response> handleBindException(
            BindingResult bindingResult, HttpServletRequest req) {
        log.error("바인딩 예외가 발생했습니다.[{}]", req.getRequestURI());
        return ResponseEntity.badRequest()
                .body(Response.error(null, bindingResult, messageSource));
    }

    //요청 dto 에 enum 이 포함되어 있을 때, 잘못된 데이터로 enum 에 담으려는 경우..
    //또는 dto 에 담을 값이 타입이 맞지 않는 경우..
    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class})
    protected ResponseEntity<Response> handleHttpMessageNotReadableException(
            Exception e, HttpServletRequest req) {
        log.error("요청 DTO 의 정보를 읽지 못했습니다.[{}]", req.getRequestURI());
        String message = e.getMessage() != null ? e.getMessage().split("[:;,]")[0] : null;
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
        String message = e.getMessage() != null ? e.getMessage().split("[:;,]")[0] : null;
        return ResponseEntity.badRequest()
                .body(Response.error(null, Error.builder()
                        .code(CODE_DTO)
                        .defaultMessage(message)
                        .build()));
    }

    //파일 업로드의 경우 파일 사이즈 제한을 벗어났을 때..
    @ExceptionHandler
    protected ResponseEntity<Response> handleFileSizeLimitExceeded(
            FileSizeLimitExceededException e, HttpServletRequest req) {
        log.error("업로드 하려는 파일의 사이즈가 크기 제한을 초과하였습니다.[{}]", req.getRequestURI());
        return ResponseEntity.badRequest()
                .body(Response.error(null, Error.builder()
                        .code(CODE_DTO)
                        .defaultMessage("업로드 하려는 파일의 사이즈가 크기 제한을 초과하였습니다.")
                        .build()));
    }

    //서비스 요청의 전체 크기가 제한 조건을 넘었을 때..
    @ExceptionHandler
    protected ResponseEntity<Response> handleFileSizeLimitExceeded(
            SizeLimitExceededException e, HttpServletRequest req) {
        log.error("서비스 요청의 크기가 크기 제한을 초과하였습니다.[{}]", req.getRequestURI());
        return ResponseEntity.badRequest()
                .body(Response.error(null, Error.builder()
                        .code(CODE_DTO)
                        .defaultMessage("서비스 요청의 크기가 크기 제한을 초과하였습니다.")
                        .build()));
    }

    //인증과 관련한 예외가 발생했을 때.. (정체를 확인하지 못했을 때)
    @ExceptionHandler
    protected ResponseEntity<Response> handleAuthenticationException(
            WrongAuthenticationException e, HttpServletRequest req) {
        log.error("인증 예외가 발생했습니다.[{}]", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Response.error(null, Error.by(e)));
    }

    //인가와 관련된 예외가 발생했을 때.. (허가받지 못한 자원에 접근했을 때)
    @ExceptionHandler
    protected ResponseEntity<Response> handleAuthoritiesException(
            WrongAuthoritiesException e, HttpServletRequest req) {
        log.error("인가 예외가 발생했습니다.[{}]", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Response.error(null, Error.by(e)));
    }

    //보안에 관련된 일반적인 예외가 발생했을 때..
    @ExceptionHandler
    protected ResponseEntity<Response> handleAuthException(
            WrongAuthException e, HttpServletRequest req) {
        log.error("보안 예외가 발생했습니다.[{}]", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Response.error(null, Error.by(e)));
    }

    //리프레쉬 토큰으로 엑세스 토큰을 재발급 받거나, 다시 로그인을 해야만 할 때..
    @ExceptionHandler
    protected ResponseEntity<Response> handleNotAcceptException(
            NotAcceptableException e, HttpServletRequest req) {
        log.error("재인증이 필요합니다.[{}]", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body(Response.error(null, Error.by(e)));
    }

    @ExceptionHandler
    protected ResponseEntity<Response> handleClientException(
            ClientException e, HttpServletRequest req) {
        log.error("클라이언트 예외가 발생했습니다.[{}]", req.getRequestURI());
        return ResponseEntity.badRequest()
                .body(Response.error(null, Error.by(e)));
    }

    @ExceptionHandler
    protected ResponseEntity<Response> handleServerException(
            ServerException e, HttpServletRequest req) {
        log.error("서버 예외가 발생했습니다.[{}]", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error(null, Error.by(e)));
    }

    @ExceptionHandler
    protected ResponseEntity<Response> handleCustomException(
            CustomException e, HttpServletRequest req) {
        log.error("서비스 예외가 발생했습니다.[{}]", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error(null, Error.by(e)));
    }
}
