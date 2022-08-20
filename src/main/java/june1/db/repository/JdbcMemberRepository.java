package june1.db.repository;

import june1.db.common.DriverManagerUtil;
import june1.db.common.exception.DbException;
import june1.db.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
public class JdbcMemberRepository implements MemberRepository {

    private final DataSource dataSource;
    private final DriverManagerUtil driverManagerUtil = new DriverManagerUtil();

    //-------------------------------------------------
    //저장
    //-------------------------------------------------
    public Member save(Member m) {
        Connection conn = null;
        PreparedStatement stmt = null;
        String sql = "insert into member(name, money) values (?, ?)";

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, m.getName());
            stmt.setLong(2, m.getMoney());
            int count = stmt.executeUpdate();
            log.info("{} 건이 저장되었습니다.", count);
            return m;

        } catch (SQLException e) {
            log.error("저장 실패.. 메시지=[{}]", e.getMessage());
            throw new DbException(e);

        } finally {
            close(conn, stmt, null);
        }
    }

    //-------------------------------------------------
    //단건 조회
    //-------------------------------------------------
    public Member findByName(String name) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "select * from member where member.name = ?";

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return Member.builder()
                        .id(rs.getLong("id"))
                        .name(rs.getString("name"))
                        .money(rs.getLong("money"))
                        .build();
            } else {
                log.error("[{}]: 해당 회원은 존재하지 않습니다.", name);
                throw new NoSuchElementException(name + ": 해당 회원은 존재하지 않습니다.");
            }

        } catch (SQLException e) {
            log.error("조회 실패.. 메시지=[{}]", e.getMessage());
            throw new DbException(e);

        } finally {
            close(conn, stmt, rs);
        }
    }

    //-------------------------------------------------
    //단건 조회(트랜잭션 사용)
    //-------------------------------------------------
    public Member findByName(Connection conn, String name) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "select * from member where member.name = ?";

        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return Member.builder()
                        .id(rs.getLong("id"))
                        .name(rs.getString("name"))
                        .money(rs.getLong("money"))
                        .build();
            } else {
                log.error("[{}]: 해당 회원은 존재하지 않습니다.", name);
                throw new NoSuchElementException(name + ": 해당 회원은 존재하지 않습니다.");
            }

        } catch (SQLException e) {
            log.error("조회 실패.. 메시지=[{}]", e.getMessage());
            throw new DbException(e);

        } finally {
            close(null, stmt, rs);
        }
    }

    //-------------------------------------------------
    //모두 조회
    //-------------------------------------------------
    @Override
    public List<Member> findAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "select * from member";

        try {
            List<Member> ret = new ArrayList<>();
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                ret.add(Member.builder()
                        .id(rs.getLong("id"))
                        .name(rs.getString("name"))
                        .money(rs.getLong("money"))
                        .build());
            }
            return ret;

        } catch (SQLException e) {
            log.error("조회 실패.. 메시지=[{}]", e.getMessage());
            throw new DbException(e);

        } finally {
            close(conn, stmt, rs);
        }
    }

    //-------------------------------------------------
    //변경
    //-------------------------------------------------
    public int update(String name, Long money) {
        Connection conn = null;
        PreparedStatement stmt = null;
        String sql = "update member set money = ? where name = ?";

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, money);
            stmt.setString(2, name);
            int count = stmt.executeUpdate();
            log.info("{} 건이 변경되었습니다.", count);
            return count;

        } catch (SQLException e) {
            log.error("변경 실패.. 메시지=[{}]", e.getMessage());
            throw new DbException(e);

        } finally {
            close(conn, stmt, null);
        }
    }

    //-------------------------------------------------
    //변경(트랜잭션 사용)
    //-------------------------------------------------
    public int update(Connection conn, String name, Long money) {
        PreparedStatement stmt = null;
        String sql = "update member set money = ? where name = ?";

        try {
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, money);
            stmt.setString(2, name);
            int count = stmt.executeUpdate();
            log.info("{} 건이 변경되었습니다.", count);
            return count;

        } catch (SQLException e) {
            log.error("변경 실패.. 메시지=[{}]", e.getMessage());
            throw new DbException(e);

        } finally {
            close(null, stmt, null);
        }
    }

    //-------------------------------------------------
    //삭제
    //-------------------------------------------------
    public int delete(Member m) {
        Connection conn = null;
        PreparedStatement stmt = null;
        String sql = "delete from member where member.name = ?";

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, m.getName());
            int count = stmt.executeUpdate();
            log.info("{} 건이 삭제되었습니다.", count);
            return count;

        } catch (SQLException e) {
            log.error("삭제 실패.. 메시지=[{}]", e.getMessage());
            throw new DbException(e);

        } finally {
            close(conn, stmt, null);
        }
    }

    private Connection getConnection() throws SQLException {
        return getConnection(false);
    }

    private Connection getConnection(boolean create) throws SQLException {
        if (create || dataSource == null) return driverManagerUtil.getConnection();
        else return dataSource.getConnection();
    }

    private void close(Connection conn, PreparedStatement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(conn);
    }
}
