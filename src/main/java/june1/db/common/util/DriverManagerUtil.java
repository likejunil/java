package june1.db.common.util;

import june1.db.common.exception.DbConnFailException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static june1.db.common.ConstantInfo.*;

@Slf4j
public class DriverManagerUtil {

    public Connection getConnection() {
        try {
            //해당 함수가 호출될 때마다 새로 데이터베이스와 연결을 맺어서 돌려준다.
            //데이터베이스를 연결하는 과정은 자원을 많이 잡아 먹는다..
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            log.info("연결=[{}], 연결 클래스=[{}]", conn, conn.getClass());
            return conn;

        } catch (SQLException e) {
            log.error("데이터베이스 연결을 생성하지 못했습니다. 메시지=[{}]", e.getMessage());
            throw new DbConnFailException("데이터베이스 연결을 실패했습니다.", e);
        }
    }
}
