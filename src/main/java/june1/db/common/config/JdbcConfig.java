package june1.db.common.config;

import com.zaxxer.hikari.HikariDataSource;
import june1.db.repository.JdbcMemberRepository;
import june1.db.repository.MemberRepository;
import june1.db.service.JdbcMemberService;
import june1.db.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import static june1.db.common.ConstantInfo.*;

@Configuration
@RequiredArgsConstructor
public class JdbcConfig {

    //사용자가 직접 만든 DataSource 가 존재할 때..
    //스프링부트가 알아서 주입해주는 DataSource 는 사라진다.
    //트랜잭션 매니저 역시 사용자가 등록하면..
    //스프링부트가 자동으로 등록해 주는 트랜잭션 매니저는 사라진다.
    //private final DataSource dataSource;
    //private final PlatformTransactionManager transactionManager;

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

    //어떤 데이터소스를 사용할 것인지 결정한다.
    //트랜잭션 매니저를 사용하는 서비스와, 데이터소스를 사용하는 리포지토리는..
    //반드시 같은 데이터소스를 사용해야 한다.
    //같은 데이터소스를 사용해야 같은 데이터베이스 같은 커넥션을 사용할 수 있고..
    //그래야만 트랜잭션을 사용할 수 있다.
    private DataSource dataSource() {
        return hikari();
    }

    //트랜잭션 매니저 역시 사용자가 등록하면..
    //스프링부트가 자동으로 등록해 주는 트랜잭션 매니저는 사라진다.
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public MemberRepository jdbcMemberRepository() {
        return new JdbcMemberRepository(dataSource());
    }

    @Bean
    public MemberService jdbcMemberService() {
        return new JdbcMemberService(jdbcMemberRepository(), transactionManager());
    }
}
