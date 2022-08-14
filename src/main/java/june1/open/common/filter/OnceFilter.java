package june1.open.common.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class OnceFilter extends OncePerRequestFilter {

    private static final String object = "OnceFilter";

    @Override
    protected void doFilterInternal(
            HttpServletRequest req,
            HttpServletResponse res,
            FilterChain filterChain) throws ServletException, IOException {

        String uuid = UUID.randomUUID().toString().substring(0, 8);
        log.info("{} {}=[{}][{}][{}]",
                FilterConstant.filterLogPrefix, object, uuid, req.getDispatcherType(), req.getRequestURI());

        //요청 body 데이터와 응답 body 데이터를 캐싱하여 재사용이 가능하도록 조치한다.
        //이를 통해 인터셉터에서 로그를 생성하도록 지원한다.
        ContentCachingRequestWrapper request = new ContentCachingRequestWrapper(req);
        ContentCachingResponseWrapper response = new ContentCachingResponseWrapper(res);
        filterChain.doFilter(request, response);

        //응답 데이터를 이미 인터셉터에서 읽어버렸으므로 스트림을 소비했다.
        //따라서 다시 응답 데이터를 복사하여 채운다.
        response.copyBodyToResponse();
    }
}
