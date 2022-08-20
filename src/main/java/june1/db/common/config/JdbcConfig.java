package june1.db.common.config;

import com.zaxxer.hikari.HikariDataSource;
import june1.db.repository.JdbcMemberRepository;
import june1.db.repository.MemberRepository;
import june1.db.service.JdbcMemberService;
import june1.db.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

import static june1.db.common.ConstantInfo.*;

@Configuration
@RequiredArgsConstructor
public class JdbcConfig {

    //스프링이 알아서 만들어서 등록해 준 dataSource
    //사용자가 직접 만든 DataSource 가 존재할 때..
    //스프링이 알아서 주입해주는 DataSource 는 사라진다.
    //private final DataSource dataSource;

    @Bean
    public DataSource hikari() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10);
        return dataSource;
    }

    @Bean
    public DataSource driverManager() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        return dataSource;
    }

    @Bean
    public MemberRepository jdbcMemberRepository() {
        //어떤 DataSource 를 선택하여 memberRepository 를 생성할 것인가?
        return new JdbcMemberRepository(hikari());
    }

    @Bean
    public MemberService jdbcMemberService() {
        //어떤 DataSource 를 선택하여 memberService 를 생성할 것인가?
        return new JdbcMemberService(hikari());
    }
}