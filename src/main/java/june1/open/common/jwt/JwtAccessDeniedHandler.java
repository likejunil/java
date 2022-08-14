package june1.open.common.jwt;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {

        //로그인은 했지만 인가되지 않은 자원에 접근했을 경우..
        response.sendError(HttpServletResponse.SC_FORBIDDEN,
                "해당 자원에 대한 접근 권한이 부족합니다.");
    }
}
