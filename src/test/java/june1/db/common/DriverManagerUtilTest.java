package june1.db.common;

import june1.db.common.util.DriverManagerUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class DriverManagerUtilTest {

    private final DriverManagerUtil dm = new DriverManagerUtil();

    @Test
    void DriverManager_를_사용한_데이터베이스_연결() {
        Connection conn = dm.getConnection();
        assertThat(conn).isNotNull();
    }
}