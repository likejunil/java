package june1.open.common.filter;

import june1.open.common.exception.auth.ExpiredTokenException;
import june1.open.common.jwt.JwtUserInfo;
import june1.open.common.jwt.TokenProvider;
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

import static june1.open.common.ConstantInfo.*;
import static june1.open.common.filter.FilterConstant.filterLogPrefix;

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
                } catch (ExpiredTokenException e) {
                    //redis 서버에 해당 토큰과 관련된 정보가 없음..
                    //로그인, 로그아웃, 재인증을 요청한 것이 아니라면 토큰을 다시 발급 받으라고 응답..
                    if (isBlockRequest(req, res)) {
                        return;
                    }

                    //로그인, 로그아웃, 토큰 재발급은..
                    //잘못된 토큰을 가져오더라도 통과해서 진행함..
                    chain.doFilter(request, response);
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
            // 로그인 요청도 아니고..
            // 리프레쉬 토큰으로 재발급을 요청하는 것이 아니라면..
            // 로그아웃을 하는 것도 아니라면..
            // 서비스로 전달하지 않는다.
            //---------------------------------
            else {
                log.info("{} {}=토큰이 유효하지 않습니다.", filterLogPrefix, object);
                if (isBlockRequest(req, res)) {
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

        chain.doFilter(req, res);
    }

    private boolean isBlockRequest(HttpServletRequest req, HttpServletResponse res) throws IOException {
        //로그인, 로그아웃, 재인증을 요청한 것이 아니라면 토큰을 다시 발급 받으라고 응답..
        String uri = req.getRequestURI();
        if (!(uri.equals(URI_AUTH + URI_LOGIN) ||
                uri.equals(URI_AUTH + URI_REISSUE) ||
                uri.equals(URI_AUTH + URI_LOGOUT))) {
            res.sendError(NEED_REISSUE_TOKEN, "토큰이 유효하지 않습니다.");
            return true;
        }

        return false;
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
