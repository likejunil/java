package june1.open.domain.common;

import june1.open.common.jwt.JwtUserInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.MediaType.*;

@Slf4j
@Getter
@Entity
@Table(name = "be_log")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Log extends BaseEntity {

    @Transient
    private static final String HEADER_REFERER = "referer";
    @Transient
    private static final String HEADER_USER_AGENT = "user-agent";
    @Transient
    private static final String[] contentTypes = {
            TEXT_PLAIN_VALUE,
            TEXT_HTML_VALUE,
            TEXT_XML_VALUE,
            APPLICATION_JSON_VALUE};
    @Transient
    private long start;
    @Transient
    private long end;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long id;

    private String memberId;
    private String trId;
    private long milliSec;

    private String method;
    private String url;
    private String params;
    private String referer;
    private String userAgent;
    private String authorization;
    private String contentType;
    private int contentLength;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String requestBody;

    private Integer responseCode;
    private Integer responseLength;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String responseBody;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String exception;

    public static Log of(HttpServletRequest req) {
        //시큐리티 컨텍스트를 통해 인증 객체를 불러옴..
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            log.error("인증 객체(Authentication)가 존재하지 않습니다. dispatcher=[{}] uri=[{}]",
                    req.getDispatcherType(), req.getRequestURI());
            //인증 객체가 없는 경우 서비스를 제공하지 않는다.
            return null;
        }

        String userId;
        String token = auth.getCredentials() != null ? auth.getCredentials().toString() : null;
        Object principal = auth.getPrincipal();
        //인증 객체가 JwtUserInfo 타입인 경우..
        if (principal instanceof JwtUserInfo) {
            JwtUserInfo user = (JwtUserInfo) principal;
            userId = user.getUserId();
        }
        //토큰 없이 요청한 사용자는 JwtUserInfo 타입의 객체를 얻을 수 없음..
        else {
            userId = auth.getPrincipal().toString();
        }

        return Log.builder()
                .memberId(userId)
                .trId(UUID.randomUUID().toString())
                .authorization(token)
                .method(req.getMethod())
                .url(req.getRequestURI())
                .referer(req.getHeader(HEADER_REFERER))
                .userAgent(req.getHeader(HEADER_USER_AGENT))
                .contentLength(req.getContentLength())
                .contentType(req.getContentType())
                .build()
                .params(req.getParameterMap())
                .start();
    }

    private Log params(Map<String, String[]> map) {
        //쓰레드별로 독립적으로 실행되기 때문에..
        //더 속도가 빠른 StringBuilder() 사용..
        StringBuilder sb = new StringBuilder();
        map.forEach((key, value) -> sb
                .append(key)
                .append("=")
                .append(Arrays.toString(value))
                .append(","));
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        this.params = sb.toString();
        return this;
    }

    private Log start() {
        this.start = System.currentTimeMillis();
        return this;
    }

    public Log end() {
        this.end = System.currentTimeMillis();
        this.milliSec = this.end - this.start;
        return this;
    }

    public Log requestBody(HttpServletRequest req) {
        this.requestBody = null;
        if (req == null)
            return this;

        //content-type 의 종류에 따라 로그 기록 여부를 결정..
        if (!PatternMatchUtils.simpleMatch(contentTypes, req.getContentType())) {
            //_log("request body 내용을 기록하지 않습니다.[{}]", req.getContentType());
            return this;
        }

        //request body 내용 유무를 확인한다.
        if (req.getContentLength() > 0) {
            //inputStream 을 읽으면 데이터가 소비되어 버린다.
            //즉, request body 를 두 번 읽을 수 없다.
            //따라서 반드시 캐싱 객체를 사용해야 한다.
            ContentCachingRequestWrapper request =
                    WebUtils.getNativeRequest(req, ContentCachingRequestWrapper.class);
            if (request != null) {
                byte[] body = request.getContentAsByteArray();
                if (body.length > 0) {
                    this.requestBody = new String(body, StandardCharsets.UTF_8);
                }
            }
        }

        return this;
    }

    public Log responseCode(Integer code) {
        this.responseCode = code;
        return this;
    }

    public Log responseBody(HttpServletResponse res) {
        this.responseBody = null;
        this.responseLength = 0;
        if (res == null)
            return this;

        //outputStream 을 읽으면 데이터가 소비되어 버린다.
        //즉, response body 를 두 번 읽을 수 없다.
        //따라서 반드시 캐싱 객체를 사용해야 한다.
        ContentCachingResponseWrapper response =
                WebUtils.getNativeResponse(res, ContentCachingResponseWrapper.class);
        if (response != null) {
            byte[] bytes = response.getContentAsByteArray();
            this.responseLength = bytes.length;
            if (bytes.length > 0) {
                //파일 다운로드의 경우 response body 를 로그에 담지 않는다.
                if (response.getHeader(CONTENT_DISPOSITION) == null)
                    this.responseBody = new String(bytes, StandardCharsets.UTF_8);
                return this;
            }
        }

        return this;
    }

    public Log responseBody(String body) {
        this.responseBody = body;
        this.responseLength = body != null ? body.length() : null;
        return this;
    }

    public Log exception(String e) {
        this.exception = e;
        return this;
    }
}