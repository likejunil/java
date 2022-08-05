package june1.vgen.open.service;

import june1.vgen.open.repository.RedisUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RedisUserService {

    private final RedisUserRepository redisUserRepository;
}
