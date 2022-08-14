package june1.open.service;

import june1.open.common.ConstantInfo;
import june1.open.common.exception.client.NoSuchMemberException;
import june1.open.domain.Company;
import june1.open.domain.Member;
import june1.open.domain.redis.RedisUser;
import june1.open.repository.RedisUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RedisUserService {

    private final static String object = "RedisUserService";
    private final RedisUserRepository redisUserRepository;

    @Transactional
    public RedisUser changeCompany(Member member, Company company) {
        //해당 사용자를 조회한다.
        RedisUser u = redisUserRepository.findById(member.getId()).orElseThrow(() -> {
            log.error("[{}]사용자의 정보가 redis 서버에 존재하지 않음", member.getMemberId());
            throw NoSuchMemberException.builder()
                    .code(ConstantInfo.CODE_REDIS)
                    .message(object)
                    .object(object)
                    .field("changeCompany().member")
                    .rejectedValue(member.getMemberId())
                    .build();
        });

        //변경된 회사 정보를 반영한다.
        return redisUserRepository.save(u.company(
                company != null ? company.getId() : null,
                company != null ? company.getCompanyType() : null));
    }
}
