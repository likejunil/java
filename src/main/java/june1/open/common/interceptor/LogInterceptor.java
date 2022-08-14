package june1.open.common.interceptor;

import june1.open.common.util.It;
import june1.open.domain.common.Log;
import june1.open.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static june1.open.common.ConstantInfo.LOG_KEY;
import static june1.open.common.interceptor.InterceptorConstant.interceptorLogPrefix;

@Slf4j
@RequiredArgsConstructor
public class LogInterceptor implements HandlerInterceptor {

    private static final String object = "LogInterceptor";
    private final LogRepository logRepository;

    @Override
    public boolean preHandle(
            HttpServletRequest req,
            HttpServletResponse res,
            Object handler) {

        //log 객체를 저장
        //req.getSession(true).setAttribute(SESSION_LOG_KEY, Log.of(req));
        It.setIt(LOG_KEY, Log.of(req));
        return true;
    }

    //예외가 발생하면 호출되지 않음..
    @Override
    public void postHandle(
            HttpServletRequest req,
            HttpServletResponse res,
            Object handler,
            ModelAndView modelAndView) {
    }

    //예외 발생과 상관 없이 항상 호출..
    @Override
    public void afterCompletion(
            HttpServletRequest req,
            HttpServletResponse res,
            Object handler,
            Exception e) {

        //로그 기록을 불러옴..
        Log log = (Log) It.getIt(LOG_KEY);
        if (log != null) {
            //요청 본문과 응답 본문을 셋팅
            Integer responseCode = log.getResponseCode() != null ? log.getResponseCode() : HttpStatus.OK.value();
            log.requestBody(req).responseCode(responseCode).responseBody(res);

            //ControllerAdvice 에서 처리하지 못한 예외는 여기에 도착한다.
            //예외가 발생했다면 예외 메시지를 기록..
            if (e != null) {
                _log("예외가 발생했습니다.[{}]", e.getMessage());
                log.responseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .exception(e.getMessage());
            }

            //클라이언트의 요청 및 응답 내역을 데이터베이스에 기록한다.
            logRepository.save(log.end());
        }
    }

    private void _log(String format, Object... args) {
        log.error("{} {}=" + format, interceptorLogPrefix, object, args);
    }
}
