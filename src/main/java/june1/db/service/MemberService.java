package june1.db.service;

import june1.db.common.exception.DbException;
import june1.db.common.exception.TransferException;
import june1.db.controller.dto.*;
import june1.db.domain.Member;
import june1.db.repository.JdbcMemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.PatternMatchUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class MemberService {

    private final String[] block = new String[]{"june3"};
    private final DataSource dataSource;
    private final JdbcMemberRepository jdbcMemberRepository;

    public MemberService(@Qualifier("hikari") DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcMemberRepository = new JdbcMemberRepository(dataSource);
    }

    public MemberDto create(MemberReqDto dto) {
        return MemberDto.of(jdbcMemberRepository.save(Member.builder()
                .name(dto.getName())
                .money(dto.getMoney())
                .build()));
    }

    public MembersDto list() {
        return MembersDto.builder()
                .list(jdbcMemberRepository.findAll()
                        .stream()
                        .map(MemberDto::of)
                        .collect(toList()))
                .build();
    }

    public MemberDto query(String name) {
        return MemberDto.of(jdbcMemberRepository.findByName(name));
    }

    public TransferResDto transfer(TransferReqDto dto) throws SQLException {
        Connection conn = dataSource.getConnection();
        try {
            conn.setAutoCommit(false);
            TransferResDto ret = bizProc(conn, dto);
            conn.commit();
            return ret;

        } catch (Exception e) {
            conn.rollback();
            throw new DbException(e);

        } finally {
            release(conn);
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

    public TransferResDto bizProc(Connection conn, TransferReqDto dto) {
        Member from = jdbcMemberRepository.findByName(conn, dto.getFrom());
        Member to = jdbcMemberRepository.findByName(conn, dto.getTo());

        if (from.getMoney() < dto.getAmount()) {
            log.info("사용자[{}]의 돈[{}]이 부족하여 [{}]를 이체할 수 없습니다.",
                    from.getName(), from.getMoney(), dto.getAmount());
            return TransferResDto.builder().result(false).build();
        }

        //블랙리스트에게 이체하면.. 중간에 실패..
        //계좌 이체의 원자성이 깨지는 것을 테스트..
        jdbcMemberRepository.update(conn, from.getName(), from.getMoney() - dto.getAmount());
        checkBlackList(from.getName());
        jdbcMemberRepository.update(conn, to.getName(), to.getMoney() + dto.getAmount());
        return TransferResDto.builder().result(true).build();
    }

    private void checkBlackList(String name) {
        if (PatternMatchUtils.simpleMatch(block, name)) {
            throw new TransferException("요청 금액을 이체할 수 없습니다.");
        }
    }
}
