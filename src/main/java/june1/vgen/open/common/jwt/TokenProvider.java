package june1.vgen.open.common.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import june1.vgen.open.common.exception.auth.ExpiredTokenException;
import june1.vgen.open.common.exception.auth.IllegalTokenException;
import june1.vgen.open.controller.auth.dto.Token;
import june1.vgen.open.domain.Member;
import june1.vgen.open.domain.RedisUser;
import june1.vgen.open.repository.MemberRepository;
import june1.vgen.open.repository.RedisUserRepository;
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
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static june1.vgen.open.common.ConstantInfo.CODE_AUTH;
import static june1.vgen.open.common.ConstantInfo.SEQ;

@Slf4j
@Component
public class TokenProvider {

    private final static String object = "TokenProvider";

    private final String secret;
    private final long accessValid;
    private final long refreshValid;
    private final RedisUserRepository redisUserRepository;
    private final MemberRepository memberRepository;

    private Key key;

    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.valid-seconds}") long validSeconds,
            @Value("${jwt.refresh-seconds}") long refreshSeconds,
            RedisUserRepository redisUserRepository,
            MemberRepository memberRepository) {

        this.secret = secret;
        this.accessValid = validSeconds * 1_000;
        this.refreshValid = refreshSeconds * 1_000;
        this.redisUserRepository = redisUserRepository;
        this.memberRepository = memberRepository;
    }

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 토큰을 생성할 때 액세스 토큰과 리프레쉬 토큰을 함께 생성한다.
     *
     * @param m
     * @return
     */
    public Token createToken(Member m) {
        Map<String, Object> payload = new HashMap<>();
        payload.put(SEQ, m.getId());

        Date iat = new Date();
        Date exp = new Date(iat.getTime() + accessValid);
        String accessToken = Jwts.builder()
                .setClaims(payload)
                .setIssuedAt(iat)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        Date refreshExp = new Date(iat.getTime() + refreshValid);
        String refreshToken = Jwts.builder()
                .setClaims(Map.of(SEQ, m.getId()))
                .setExpiration(refreshExp)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expired(exp.getTime())
                .build();
    }

    /**
     * 1. 토큰을 인자로 받아서 사용자의 고유번호를 추출한다.
     * 2. 추출한 사용자의 고유번호를 통해 redis 에서 관련 정보를 가져온다.
     * 3. 가져온 정보를 바탕으로 Authentication 을 생성 및 반환한다.
     *
     * @param token
     * @return
     */
    public Authentication getAuthentication(String token) {
        //토큰을 인자로 받아서 사용자의 고유번호를 추출한다.
        Long memberSeq = decryptToken(token);

        //redis 에서 사용자의 정보 가져와서 Authentication 생성하기
        //redis 에 사용자의 정보가 없다면 토큰을 재발급 받거나 로그인을 해야함..
        RedisUser u = redisUserRepository
                .findById(memberSeq)
                .orElseThrow(() -> {
                    log.error("[{}]사용자의 정보가 redis 에 존재하지 않음 (토큰 재발급 필요)", memberSeq);
                    throw ExpiredTokenException.builder()
                            .code(CODE_AUTH)
                            .message("토큰을 다시 발급받아야 합니다.")
                            .object(object)
                            .field("getAuthentication().token")
                            .rejectedValue(token)
                            .build();
                });

        //권한 정보 설정
        Collection<? extends GrantedAuthority> authorities =
                Stream.of(u.getRole(), u.getCompanyType())
                        .filter(Objects::nonNull)
                        .map(Enum::name)
                        .map(SimpleGrantedAuthority::new)
                        .collect(toList());

        //쓰레드 로컬에 저장할 사용자의 정보 생성
        JwtUserInfo user = JwtUserInfo.builder()
                .seq(u.getId())
                .userId(u.getUserId())
                .email(u.getEmail())
                .companySeq(u.getCompanySeq())
                .build();

        return new UsernamePasswordAuthenticationToken(user, "", authorities);
    }

    public Long decryptToken(String token) {
        //토큰 디크립트, 서명 유효 확인..
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        //토큰에서 필수 정보(사용자의 고유번호)를 꺼내기
        if (claims.get(SEQ) == null) {
            log.error("토큰에 사용자의 고유번호가 존재하지 않음");
            throw IllegalTokenException.builder()
                    .code(CODE_AUTH)
                    .message("토큰에 사용자의 고유번호가 존재하지 않습니다.")
                    .object(object)
                    .field("getAuthentication().token")
                    .rejectedValue(token)
                    .build();
        }

        //claims 에서 숫자 데이터를 꺼내면 실수 타입으로 추출된다.
        return Double.valueOf(claims.get(SEQ).toString()).longValue();
    }

    public boolean isValidToken(String token) {
        try {
            //서명이 잘못되었거나 (위변조..)
            //만료가 된 토큰의 경우 예외 발생..
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            //토큰에 사용자의 고유번호가 저장되어 있는지 확인
            if (claims.get(SEQ) == null) {
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
