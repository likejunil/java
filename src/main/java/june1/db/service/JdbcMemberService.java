package june1.db.service;

import june1.db.common.exception.DbException;
import june1.db.common.exception.TransferException;
import june1.db.controller.dto.*;
import june1.db.domain.Member;
import june1.db.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.PatternMatchUtils;

import static java.util.stream.Collectors.toList;

@Slf4j
public class JdbcMemberService implements MemberService {

    private final String[] block = new String[]{"june3"};

    private final MemberRepository memberRepository;
    private final PlatformTransactionManager transactionManager;

    public JdbcMemberService(
            MemberRepository memberRepository,
            PlatformTransactionManager transactionManager) {
        this.memberRepository = memberRepository;
        this.transactionManager = transactionManager;
    }

    /**
     * 계좌 생성
     *
     * @param dto
     * @return
     */
    public MemberDto create(MemberReqDto dto) {
        return MemberDto.of(memberRepository.save(Member.builder()
                .name(dto.getName())
                .money(dto.getMoney())
                .build()));
    }

    /**
     * 모든 계좌 조회
     *
     * @return
     */
    public MembersDto list() {
        return MembersDto.builder()
                .list(memberRepository.findAll()
                        .stream()
                        .map(MemberDto::of)
                        .collect(toList()))
                .build();
    }

    /**
     * 특정 계좌 조회
     *
     * @param name
     * @return
     */
    public MemberDto query(String name) {
        return MemberDto.of(memberRepository.findByName(name));
    }

    /**
     * 계좌 이체
     *
     * @param dto
     * @return
     */
    public TransferResDto transfer(TransferReqDto dto) {
        TransactionStatus status = transactionManager.getTransaction(
                new DefaultTransactionDefinition());
        try {
            TransferResDto ret = bizProc(dto);
            transactionManager.commit(status);
            return ret;

        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new DbException(e);
        }
    }

    /**
     * 하나의 커넥션에 하나의 세션이 만들어진다.
     * 트랜잭션은 세션 안에서 이루어진다.
     * 따라서 트랜잭션을 사용하려면 반드시 같은 커넥션을 사용해야한다.
     */
    private TransferResDto bizProc(TransferReqDto dto) {
        Member from = memberRepository.findByName(dto.getFrom());
        Member to = memberRepository.findByName(dto.getTo());

        if (from.getMoney() < dto.getAmount()) {
            log.info("사용자[{}]의 돈[{}]이 부족하여 [{}]를 이체할 수 없습니다.",
                    from.getName(), from.getMoney(), dto.getAmount());
            return TransferResDto.builder()
                    .result(false)
                    .build();
        }

        //블랙리스트에게 이체하면.. 중간에 실패..
        //계좌 이체의 원자성이 깨지는 것을 트랜잭션을 통해 방지해야 함.
        memberRepository.update(from.getName(), from.getMoney() - dto.getAmount());
        checkBlackList(from.getName());
        memberRepository.update(to.getName(), to.getMoney() + dto.getAmount());

        return TransferResDto.builder()
                .result(true)
                .build();
    }

    private void checkBlackList(String name) {
        if (PatternMatchUtils.simpleMatch(block, name)) {
            throw new TransferException(name + ": 요청 금액을 출금할 수 없습니다.");
        }
    }
}
