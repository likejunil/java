package june1.db.basic.repository;

import june1.db.basic.common.util.DriverManagerUtil;
import june1.db.basic.domain.Member;
import june1.db.basic.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
public class JdbcMemberRepository implements MemberRepository {

    private final DataSource dataSource;
    private final SQLExceptionTranslator translator;
    private final DriverManagerUtil driverManagerUtil = new DriverManagerUtil();

    public JdbcMemberRepository(DataSource dataSource) {
        this.dataSource = dataSource;
        this.translator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
    }

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
            throw translator.translate("save", sql, e);
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
            throw translator.translate("findByName", sql, e);

        } finally {
            close(conn, stmt, rs);
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
            throw translator.translate("findAll", sql, e);

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
            throw translator.translate("update", sql, e);

        } finally {
            close(conn, stmt, null);
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
            throw translator.translate("delete", sql, e);

        } finally {
            close(conn, stmt, null);
        }
    }

    private Connection getConnection() throws SQLException {
        return getConnection(false);
    }

    private Connection getConnection(boolean create) throws SQLException {
        if (create || dataSource == null) {
            //DriverManager 를 사용할 경우, 해당 리포지토리에서는 트랜잭션을 사용할 수 없다.
            return driverManagerUtil.getConnection();
        } else {
            //DataSourceUtils.getConnection() 에 의해서 트랜잭션을 위해 사용할 커넥션을 얻을 수 있다.
            //트랜잭션 동기화 매니저에 담겨 있는 커넥션을 얻어온다.
            return DataSourceUtils.getConnection(dataSource);
        }
    }

    private void close(Connection conn, PreparedStatement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        //트랜잭션을 유지하려면 conn 을 그냥 close() 해서는 안된다.
        //그냥 close() 를 해버리면 커넥션이 유지가 되지 않는다.
        //커넥션은 커밋 혹은 롤백까지 반드시 살아있어야 한다.
        DataSourceUtils.releaseConnection(conn, dataSource);
    }
}
