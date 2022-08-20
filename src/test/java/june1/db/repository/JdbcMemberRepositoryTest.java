package june1.db.repository;

import com.zaxxer.hikari.HikariDataSource;
import june1.db.domain.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.NoSuchElementException;

import static june1.db.common.ConstantInfo.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JdbcMemberRepositoryTest {

    private Member member;
    private MemberRepository memberRepository;

    private DataSource getHikari() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        return dataSource;
    }

    private DataSource getDriverManager() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        return dataSource;
    }

    @BeforeEach
    void beforeEach() {
        //DataSource 와 MemberRepository 를 선택..
        DataSource dataSource = getDriverManager();
        memberRepository = new JdbcMemberRepository(dataSource);
    }

    @AfterEach
    void afterEach() {
        if (member != null) {
            memberRepository.delete(member);
        }
    }

    @Test
    void 계정을_생성하고_불러오고_변경하고_삭제하기() {
        String name = "june1";
        long money = 10_000;

        //생성하기
        member = Member.builder().name(name).money(money).build();
        memberRepository.save(member);

        //조회하기
        Member load = memberRepository.findByName(name);
        assertThat(load).isNotNull();
        assertThat(load.getName()).isEqualTo(member.getName());
        assertThat(load.getMoney()).isEqualTo(member.getMoney());

        //변경하기
        memberRepository.update(load.getName(), money + 1000);
        long updated = memberRepository.findByName(load.getName()).getMoney();
        assertThat(money + 1000).isEqualTo(updated);

        //삭제하기
        memberRepository.delete(load);
        assertThatThrownBy(() -> memberRepository.findByName(load.getName()))
                .isInstanceOf(NoSuchElementException.class);
    }
}