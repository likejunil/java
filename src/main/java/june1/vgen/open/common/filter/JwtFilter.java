package june1.vgen.open.common.filter;

import june1.vgen.open.common.jwt.JwtUserInfo;
import june1.vgen.open.common.jwt.TokenProvider;
import june1.vgen.open.service.RedisUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

import static javax.servlet.http.HttpServletResponse.SC_NOT_ACCEPTABLE;
import static june1.vgen.open.common.ConstantInfo.*;
import static june1.vgen.open.common.filter.FilterConstant.filterLogPrefix;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    private static final String object = "JwtFilter";

    private final TokenProvider tokenProvider;
    private final RedisUserService redisUserService;

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String token = resolveToken(req);
        //---------------------------------
        // 토큰이 존재할 때..
        //---------------------------------
        if (StringUtils.hasText(token)) {
            //---------------------------------
            // 토큰이 유효할 때..
            //---------------------------------
            if (tokenProvider.isValidToken(token)) {
                Authentication auth = tokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
                JwtUserInfo user = (JwtUserInfo) auth.getPrincipal();

                //redis 에 저장된 액세스 토큰을 검사..
                //1.redis 가 다운되었거나 엑세스 토큰을 얻을 수 없다면 그냥 통과..
                //2.redis 에서 얻은 토큰이 사용 불가로 변경되었다면 재로그인 유도..
                if (!redisUserService.isValid(user.getSeq())) {
                    log.info("{} {}=[{}]사용자의 토큰이 사용 불가 상태",
                            filterLogPrefix, object, user.getUserId());
                    HttpServletResponse res = (HttpServletResponse) response;
                    res.sendError(SC_NOT_ACCEPTABLE, "토큰이 유효하지 않습니다.");
                    return;
                }

                log.info("{} {}=Security Context 에 인증 정보가 저장되었음.", filterLogPrefix, object);
                log.info("{} {}=[{}]님의 인가 권한={}",
                        filterLogPrefix, object, user.getUserId(),
                        auth.getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()));
            }
            //---------------------------------
            // 토큰이 유효하지 않을 때..
            // 로그인 요청도 아니고 리프레쉬 토큰으로 재발급을 요청하는 것이 아니라면..
            // 서비스로 전달하지 않는다.
            //---------------------------------
            else {
                log.info("{} {}=토큰이 유효하지 않습니다.", filterLogPrefix, object);
                String uri = req.getRequestURI();
                if (!uri.equals(URI_AUTH + URI_REISSUE) && !uri.equals(URI_AUTH + URI_LOGIN)) {
                    HttpServletResponse res = (HttpServletResponse) response;
                    res.sendError(SC_NOT_ACCEPTABLE, "토큰이 유효하지 않습니다.");
                    return;
                }
            }
        }
        //---------------------------------
        // 토큰이 존재하지 않을 때..
        //---------------------------------
        else {
            log.info("{} {}=[{}]에 대한 토큰 없는 접근",
                    filterLogPrefix, object, req.getRequestURI());
        }

        chain.doFilter(request, response);
    }

    /**
     * Authorization: Bearer ${token-value}
     */
    private String resolveToken(HttpServletRequest req) {
        String token = req.getHeader(AUTHORIZATION);
        String bearer = BEARER + " ";

        if (StringUtils.hasText(token)) {
            String prefix = token.substring(0, bearer.length());
            if (prefix.equalsIgnoreCase(bearer)) {
                return token.substring(bearer.length());
            }
        }

        return null;
    }
}
