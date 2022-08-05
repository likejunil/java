package june1.vgen.open.common.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import june1.vgen.open.controller.auth.dto.TokenResDto;
import june1.vgen.open.domain.Company;
import june1.vgen.open.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

import static june1.vgen.open.common.ConstantInfo.*;

@Slf4j
@Component
public class TokenProvider {

    private final String secret;
    private final long validMilliSeconds;
    private final long refreshMilliSeconds;

    private Key key;

    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.valid-seconds}") long validSeconds,
            @Value("${jwt.refresh-seconds}") long refreshSeconds) {
        this.secret = secret;
        this.validMilliSeconds = validSeconds * 1_000;
        this.refreshMilliSeconds = refreshSeconds * 1_000;
    }

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenResDto createToken(Member m) {
        Company c = m.getCompany();
        boolean b = c != null;

        Map<String, Object> payload = new HashMap<>();
        payload.put(SEQ, m.getId());
        payload.put(MEMBER_ID, m.getMemberId());
        payload.put(COMPANY_SEQ, b ? c.getId() : null);
        payload.put(AUTH, m.getRole() + (b ? COMMA + c.getCompanyType() : ""));

        Date iat = new Date();
        Date exp = new Date(iat.getTime() + validMilliSeconds);
        String accessToken = Jwts.builder()
                .setClaims(payload)
                .setIssuedAt(iat)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        Date refreshExp = new Date(iat.getTime() + refreshMilliSeconds);
        String refreshToken = Jwts.builder()
                .setClaims(Map.of(SEQ, m.getId(), MEMBER_ID, m.getMemberId()))
                .setExpiration(refreshExp)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return TokenResDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expired(exp.getTime())
                .build();
    }

    /**
     * 토큰을 인자로 받아서 Authentication 을 생성 반환한다.
     *
     * @param token
     * @return
     */
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Long memberSeq = null;
        String userId = null;
        Long companySeq = null;
        Collection<? extends GrantedAuthority> authorities = null;

        if (claims.get(SEQ) != null)
            memberSeq = Double.valueOf(claims.get(SEQ).toString()).longValue();
        if (claims.get(MEMBER_ID) != null)
            userId = claims.get(MEMBER_ID).toString();
        if (claims.get(COMPANY_SEQ) != null)
            companySeq = Double.valueOf(claims.get(COMPANY_SEQ).toString()).longValue();
        if (claims.get(AUTH) != null) {
            authorities = Arrays.stream(claims.get(AUTH).toString().split(COMMA))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }

        JwtUserInfo user = JwtUserInfo.builder()
                .seq(memberSeq)
                .userId(userId)
                .companySeq(companySeq)
                .build();

        return new UsernamePasswordAuthenticationToken(user, "", authorities);
    }

    public boolean isValidToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            if (claims.get(AUTH) == null || claims.get(SEQ) == null || claims.get(MEMBER_ID) == null) {
                log.error("토큰에 필수 정보가 없습니다.");
                return false;
            }

            return true;

        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못되었습니다.");
        }

        return false;
    }
}
