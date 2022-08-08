package june1.vgen.open.common.filter;

import june1.vgen.open.common.exception.auth.ExpiredTokenException;
import june1.vgen.open.common.exception.auth.IllegalTokenException;
import june1.vgen.open.common.jwt.JwtUserInfo;
import june1.vgen.open.common.jwt.TokenProvider;
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

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String token = resolveToken(req);
        //---------------------------------
        // 토큰이 존재할 때..
        //---------------------------------
        if (StringUtils.hasText(token)) {
            //---------------------------------
            // 토큰이 유효할 때..
            //---------------------------------
            if (tokenProvider.isValidToken(token)) {
                Authentication auth = null;
                try {
                    auth = tokenProvider.getAuthentication(token);
                }
                //redis 서버에 해당 토큰과 관련된 정보가 없음..
                catch (ExpiredTokenException e) {
                    if (!req.getRequestURI().equals(URI_AUTH + URI_LOGOUT)) throw e;
                    log.error("redis 서버에 토큰 정보가 없음에도 로그아웃을 시도함");
                    //그냥 다음으로 정상 진행, logout 서비스..
                    chain.doFilter(request, response);
                    return;
                }
                //토큰에 필수로 포함되어야 할 정보가 누락되었음..
                catch (IllegalTokenException e) {
                    res.sendError(SC_NOT_ACCEPTABLE, "토큰이 유효하지 않습니다.");
                    return;
                }

                SecurityContextHolder.getContext().setAuthentication(auth);
                JwtUserInfo user = (JwtUserInfo) auth.getPrincipal();
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
                if (!uri.equals(URI_AUTH + URI_LOGIN)
                        && !uri.equals(URI_AUTH + URI_REISSUE)
                        && !uri.equals(URI_AUTH + URI_LOGOUT)) {

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
