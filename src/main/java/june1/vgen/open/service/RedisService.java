package june1.vgen.open.service;

import june1.vgen.open.domain.RedisRefreshToken;
import june1.vgen.open.repository.RedisRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RedisService {

    private final static String VALID = "1";
    private final static String INVALID = "0";
    private final static String KEY_PREFIX = "member_seq:";
    private final RedisTemplate<String, Object> redis;
    private final RedisRefreshTokenRepository redisRefreshTokenRepository;

    @Value("${jwt.valid-seconds}")
    private long validSeconds;

    /**
     * 토큰을 새로 발급받아야 하는지 확인 (토큰 상태 확인)
     *
     * @param seq
     * @return false 를 응답할 경우 다시 로그인을 시도하여 토큰을 받아야 함
     */
    public Boolean isValid(Long seq) {
        try {
            //회원 고유번호를 사용하여 redis 에서 토큰 정보를 읽어옴
            String tmp = (String) redis.opsForValue().get(KEY_PREFIX + seq);
            if (tmp == null) {
                log.error("[{}]사용자의 토큰 정보가 redis 에 없음", seq);
                return true;
            }

            //토큰 본체와 토큰 유효성 정보를 분리
            String[] data = tmp.split("|");

            if (data.length != 2) {
                log.error("[{}]사용자의 토큰이 {}",
                        seq, data.length == 0 ? "존재하지 않습니다." : "손상되었습니다.");
                return true;
            }

            if (data[0].equals(INVALID)) {
                log.error("[{}]사용자의 토큰이 사용 불가 상태, 로그인 필요", seq);
                return false;
            }

            return true;

        } catch (Exception e) {
            //redis 서버에 접속할 수 없는 경우 토큰이 없는 것으로 처리
            log.error("redis 서버에 접속할 수 없습니다.");
            return true;
        }
    }

    /**
     * 토큰의 상태를 사용 불가 상태로 변경
     *
     * @param seq
     */
    public void dropToken(Long seq) {
        String token = getToken(seq);
        if (token != null) {
            setToken(seq, token, INVALID);
        }
    }

    /**
     * redis 에서 액세스 토큰을 가져온다.
     *
     * @param seq
     * @return
     */
    public String getToken(Long seq) {
        try {
            //회원 고유번호를 사용하여 redis 에서 토큰 정보를 읽어옴
            String tmp = (String) redis.opsForValue().get(KEY_PREFIX + seq);
            if (tmp == null) {
                log.error("[{}]사용자의 토큰 정보가 redis 에 없음", seq);
                return null;
            }

            //토큰 본체와 토큰 유효성 정보를 분리
            String[] data = tmp.split("|");
            if (data.length != 2 || data[0].equals(INVALID)) {
                log.error("[{}]사용자의 토큰이 {}", seq, data.length == 0 ? "존재하지 않습니다."
                        : data.length != 2 ? "손상되었습니다."
                        : "사용 불가 상태입니다.");
                return null;
            }

            //토큰 반환
            return data[1];

        } catch (Exception e) {
            //redis 서버에 접속할 수 없는 경우 토큰이 없는 것으로 처리
            log.error("redis 서버에 접속할 수 없습니다.");
            return null;
        }
    }

    /**
     * redis 에 액세스 토큰을 저장한다.
     *
     * @param seq
     * @param token
     */
    public void setToken(Long seq, String token) {
        setToken(seq, token, VALID);
    }

    public void setToken(Long seq, String token, String valid) {
        setToken(seq, token, valid, validSeconds, TimeUnit.SECONDS);
    }

    public void setToken(Long seq, String token, String valid, long sec, TimeUnit unit) {
        try {
            redis.opsForValue().set(KEY_PREFIX + seq, valid + "|" + token, sec, unit);
        } catch (Exception e) {
            log.error("redis 서버에 접속할 수 없습니다.");
        }
    }

    /**
     * redis 에서 엑세스 토큰을 삭제한다.
     *
     * @param seq
     */
    public void delToken(Long seq) {
        try {
            redis.delete(KEY_PREFIX + seq);
        } catch (Exception e) {
            log.error("redis 서버에 접속할 수 없습니다.");
        }
    }

    /**
     * redis 에서 리프레쉬 토큰을 가져온다.
     *
     * @param seq
     */
    public Optional<RedisRefreshToken> getRefreshToken(Long seq) {
        try {
            return redisRefreshTokenRepository.findById(seq);
        } catch (Exception e) {
            log.error("redis 서버에 접속할 수 없습니다.");
            return Optional.empty();
        }
    }

    /**
     * redis 에 리프레쉬 토큰을 저장한다.
     *
     * @param token
     */
    public Optional<RedisRefreshToken> setRefreshToken(RedisRefreshToken token) {
        try {
            return Optional.of(redisRefreshTokenRepository.save(token));
        } catch (Exception e) {
            log.error("redis 서버에 접속할 수 없습니다.");
            return Optional.empty();
        }
    }

    /**
     * redis 에서 리프레쉬 토큰을 삭제한다.
     *
     * @param seq
     */
    public void delRefreshToken(Long seq) {
        try {
            redisRefreshTokenRepository.deleteById(seq);
        } catch (Exception e) {
            log.error("redis 서버에 접속할 수 없습니다.");
        }
    }
}
