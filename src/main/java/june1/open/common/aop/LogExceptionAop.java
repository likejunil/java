package june1.open.common.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import june1.open.common.util.It;
import june1.open.controller.common.Error;
import june1.open.controller.common.Response;
import june1.open.domain.common.Log;
import june1.open.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static june1.open.common.ConstantInfo.LOG_KEY;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogExceptionAop {

    private final ObjectMapper mapper;
    private final LogRepository logRepository;

    //1.구체적으로 하나하나 지정할 수 있는 포인트 컷..
    //지정한 타입의 자식 타입들도 자동으로 선택된다.
    //따라서 인터페이스 지정도 가능하다.
    //단, 파라미터의 경우 정확한 해당 타입을 지정해야 한다.
    @Pointcut("execution(* june1.open.controller.common.ExceptionController.*(..))")
    private void all() {
    }

    //2.특정 타입(클래스)을 지정하여 포함된 모든 메서드에 적용..
    //부모 타입을 지정하면 실패, 꼭 정확히 해당 타입을 지정해야만 한다. (execution 과의 차이)
    @Pointcut("within(june1.open.controller.common.ExceptionController)")
    private void it() {
    }

    //3.파라미터(인자)를 조건으로 적용한다.
    //부모 타입으로 파라미터 타입을 지정할 수 있다. (execution 과의 차이)
    //결코 단독으로 사용해서는 안된다. (모든 곳에 적용하려고 난리친다.)
    //다른 포인트 컷과 함께 사용한다.
    @Pointcut("args(*, Exception)")
    private void args() {
    }

    //4.애노테이션이 적용된 클래스의 모든 메소드를 대상으로 한다.
    //부모로부터 상속받은 메서드를 적용되지 않는다.
    @Pointcut("@within(june1.open.common.annotation.TypeCond)")
    private void itType() {
    }

    //5.애노테이션이 적용된 클래스의 모든 메소드를 대상으로 한다.
    //부모로부터 상속받은 메서드까지 모두 지원한다.
    @Pointcut("@target(june1.open.common.annotation.TypeCond)")
    private void allType() {
    }

    //6.애노테이션이 적용된 메서드들을 대상으로 한다.
    @Pointcut("@annotation(june1.open.common.annotation.MethodCond)")
    private void itMethod() {
    }

    //7.애노테이션이 적용된 인자를 대상으로 한다.
    //여기서 인자는 클래스나 인터페이스를 대상으로 한다.
    @Pointcut("@args(june1.open.common.annotation.ParamCond)")
    private void itParam() {
    }

    //@Around(value = "all() && args(e) && target(obj) && this(proxy)", argNames = "jp,e,obj,proxy")
    //public Object doLog(ProceedingJoinPoint jp, Exception e, ExceptionController obj, ExceptionController proxy)
    @Around(value = "all() && args(*, req)", argNames = "jp,req")
    public Object doLog(ProceedingJoinPoint jp, HttpServletRequest req)
            throws Throwable {

        //일단 실행하여 클라이언트에게 보낼 예외를 생성하도록 한다.
        Object ret = jp.proceed();

        //실행을 마친 후 클라이언트에게 보낼 응답 데이터를 가로채서 로그를 나긴다.
        if (!(ret instanceof ResponseEntity)) {
            log.error("응답 데이터가 ResponseEntity 타입이 아닙니다.");
            return ret;
        }

        //응답 메시지가 정상일 때..
        Response<?> body = (Response<?>) (((ResponseEntity<?>) ret).getBody());
        if (body == null) {
            log.error("응답 데이터에 body 가 존재하지 않습니다.");
            return ret;
        }

        //응답 메시지 안에 에러 내용이 존재할 때..
        List<Error> errors = body.getErrors();
        if (errors == null || errors.isEmpty() || errors.get(0).getCode() == null) {
            log.error("에러 데이터가 존재하지 않습니다.");
            return ret;
        }

        //예외 내용들을 문자열로 변환하여 로그 객체에 저장한다.
        String error = error2String(errors);

        //응답 코드 구하기
        int responseCode = ((ResponseEntity<?>) ret).getStatusCode().value();

        //로그 데이터가 존재할 때..
        Log log = (Log) It.getIt(LOG_KEY);
        //406 과 같이 "인터셉터를 호출하지 않고 발생하는 예외"는..
        //직접 Log 엔티티를 생성하고 db 에 저장한다.
        if (log == null) {
            log = Log.of(req);
            if (log != null) {
                logRepository.save(log
                        .requestBody(req)
                        .responseCode(responseCode)
                        //ExceptionController 에서는 아직 response 로부터 body 의 내용을 읽을 수 없다.
                        //Controller 에서 예외를 던지는 순간 ExceptionController 가 호출되었고..
                        //response body 는 아직 생성되지 않았기 때문이다.
                        .responseBody(mapper.writeValueAsString(body))
                        .exception(error)
                        .end());
            }
        } else {
            //인터셉터에서 저장..
            log.responseCode(responseCode)
                    .exception(error);
        }

        //끝..
        return ret;
    }

    @NotNull
    private String error2String(List<Error> errors) throws JsonProcessingException {
        //예외 내용들을 문자열로 변환하여 로그 객체에 저장한다.
        StringBuilder sb = new StringBuilder();
        for (Error error : errors) {
            String message = mapper.writeValueAsString(error);
            sb.append(message).append(",");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }
}
