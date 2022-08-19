package june1.open.controller.common;

import june1.open.common.annotation.MethodCond;
import june1.open.common.annotation.TypeCond;
import june1.open.common.exception.CustomException;
import june1.open.common.exception.auth.NeedReissueTokenException;
import june1.open.common.exception.auth.WrongAuthException;
import june1.open.common.exception.auth.WrongAuthenticationException;
import june1.open.common.exception.auth.WrongAuthoritiesException;
import june1.open.common.exception.client.ClientException;
import june1.open.common.exception.server.ServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.context.MessageSource;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;

import static june1.open.common.ConstantInfo.*;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
@TypeCond
public class ExceptionController {

    private final MessageSource messageSource;

    //요청 json 을 dto 객체로 변환하는 과정에서 발생하는 에러 처리..
    //MessageSource 객체를 사용하여 에러 메시지를 얻을 수 있다.
    @MethodCond
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

    //요청한 파일이 존재하지 않을 때
    @ExceptionHandler
    protected ResponseEntity<Response> handleFileNotFoundException(
            FileNotFoundException e, HttpServletRequest req) {
        log.error("파일을 찾을 수 없습니다.[{}]", req.getRequestURI());
        return ResponseEntity.badRequest()
                .body(Response.error(null, Error.builder()
                        .code(CODE_FILE)
                        .defaultMessage("요청하신 파일을 찾을 수 없습니다. 메시지=" + e.getMessage())
                        .build()));
    }

    //서버가 응답하려는 미디어 타입을 클라이언트가 수신할 수 없을 때..
    @ExceptionHandler
    protected ResponseEntity<Response> handleNotAcceptableException(
            HttpMediaTypeNotAcceptableException e, HttpServletRequest req) {
        log.error("요청하신 미디어 타입을 수신할 수 없습니다.[{}]", req.getRequestURI());
        return ResponseEntity.badRequest()
                .body(Response.error(null, Error.builder()
                        .code(CODE_FILE)
                        .defaultMessage("요청하신 미디어 타입을 수신할 수 없습니다. 메시지=" + e.getMessage())
                        .build()));
    }

    //인증과 관련한 예외가 발생했을 때.. (사용자 정의에 의해..)
    @ExceptionHandler
    protected ResponseEntity<Response> handleAuthenticationException_1(
            WrongAuthenticationException e, HttpServletRequest req) {
        log.error("인증 예외가 발생했습니다.[{}]", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Response.error(null, Error.by(e)));
    }

    //인증과 관련한 예외가 발생했을 때.. (스프링 시큐리티에 의해..)
    @ExceptionHandler
    protected ResponseEntity<Response> handleAuthenticationException_2(
            AuthenticationException e, HttpServletRequest req) {
        log.error("인증 예외가 발생했습니다.[{}]", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Response.error(null, Error.builder()
                        .code(CODE_AUTH)
                        .defaultMessage(e.getMessage())
                        .build()));
    }

    //인가와 관련된 예외가 발생했을 때.. (사용자 정의에 의해..)
    @ExceptionHandler
    protected ResponseEntity<Response> handleAuthoritiesException_1(
            WrongAuthoritiesException e, HttpServletRequest req) {
        log.error("인가 예외가 발생했습니다.[{}]", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Response.error(null, Error.by(e)));
    }

    //인가와 관련된 예외가 발생했을 때.. (스프링 시큐리티에 의해..)
    @ExceptionHandler
    protected ResponseEntity<Response> handleAuthoritiesException_2(
            AccessDeniedException e, HttpServletRequest req) {
        log.error("인가 예외가 발생했습니다.[{}]", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Response.error(null, Error.builder()
                        .code(CODE_AUTH)
                        .defaultMessage(e.getMessage())
                        .build()));
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
    protected ResponseEntity<Response> handleNeedReissueTokenException(
            NeedReissueTokenException e, HttpServletRequest req) {
        log.error("재인증이 필요합니다.[{}]", req.getRequestURI());
        return ResponseEntity.status(NEED_REISSUE_TOKEN)
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

    @ExceptionHandler
    protected ResponseEntity<Response> handleNullPointException(
            NullPointerException e, HttpServletRequest req) {
        log.error("서버 오류가 발생했습니다.(null 값 참조)[{}]", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error(null, Error.builder()
                        .code(CODE_SERVER)
                        .defaultMessage("잘못된 값을 참조하였습니다.")
                        .build()));
    }

    @ExceptionHandler
    protected ResponseEntity<Response> handleRunTimeException(
            RuntimeException e, HttpServletRequest req) {
        log.error("예상치 못한 서버 오류가 발생하였습니다.[{}]", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error(null, Error.builder()
                        .code(CODE_SERVER)
                        .defaultMessage("예상하지 못한 오류가 발생하였습니다. 메시지=" + e.getMessage())
                        .build()));
    }
}
