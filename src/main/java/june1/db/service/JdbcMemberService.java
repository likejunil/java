package june1.db.service;

import june1.db.common.exception.DbException;
import june1.db.common.exception.TransferException;
import june1.db.controller.dto.*;
import june1.db.domain.Member;
import june1.db.repository.JdbcMemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static java.util.stream.Collectors.toList;

@Slf4j
public class JdbcMemberService implements MemberService {

    private final String[] block = new String[]{"june3"};

    private final DataSource dataSource;
    private final JdbcMemberRepository jdbcMemberRepository;

    public JdbcMemberService(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcMemberRepository = new JdbcMemberRepository(dataSource);
    }

    /**
     * 계좌 생성
     *
     * @param dto
     * @return
     */
    public MemberDto create(MemberReqDto dto) {
        return MemberDto.of(jdbcMemberRepository.save(Member.builder()
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
                .list(jdbcMemberRepository.findAll()
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
        return MemberDto.of(jdbcMemberRepository.findByName(name));
    }

    /**
     * 계좌 이체
     *
     * @param dto
     * @return
     */
    public TransferResDto transfer(TransferReqDto dto) {
        try {
            Connection conn = dataSource.getConnection();
            try {
                conn.setAutoCommit(false);
                TransferResDto ret = bizProc(conn, dto);
                conn.commit();
                log.info("계좌 이체 서비스가 성공적으로 진행되었습니다.");
                return ret;

            } catch (Exception e) {
                conn.rollback();
                log.error("계좌 이체 서비스가 취소되었습니다.");
                throw new DbException(e);

            } finally {
                release(conn);
            }

        } catch (Exception e) {
            //커넥션을 얻고, 커밋 변경하고, 커넥션 반환할 때..
            //예외가 발생할 가능성 존재..
            throw new DbException(e);
        }
    }

    private void release(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                log.info("어떻게 할 수가 없네.. ㅜㅠ");
            }
        }
    }

    /**
     * 하나의 커넥션에 하나의 세션이 만들어진다.
     * 트랜잭션은 세션 안에서 이루어진다.
     * 따라서 트랜잭션을 사용하려면 반드시 같은 커넥션을 사용해야한다.
     */
    private TransferResDto bizProc(Connection conn, TransferReqDto dto) {
        Member from = jdbcMemberRepository.findByName(conn, dto.getFrom());
        Member to = jdbcMemberRepository.findByName(conn, dto.getTo());

        if (from.getMoney() < dto.getAmount()) {
            log.info("사용자[{}]의 돈[{}]이 부족하여 [{}]를 이체할 수 없습니다.",
                    from.getName(), from.getMoney(), dto.getAmount());
            return TransferResDto.builder().result(false).build();
        }

        //블랙리스트에게 이체하면.. 중간에 실패..
        //계좌 이체의 원자성이 깨지는 것을 트랜잭션을 통해 방지해야 함.
        jdbcMemberRepository.update(conn, from.getName(), from.getMoney() - dto.getAmount());
        checkBlackList(from.getName());
        jdbcMemberRepository.update(conn, to.getName(), to.getMoney() + dto.getAmount());
        return TransferResDto.builder().result(true).build();
    }

    private void checkBlackList(String name) {
        if (PatternMatchUtils.simpleMatch(block, name)) {
            throw new TransferException(name + ": 요청 금액을 출금할 수 없습니다.");
        }
    }
}
