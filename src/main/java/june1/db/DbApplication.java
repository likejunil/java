package june1.db;

import june1.db.common.config.JpaConfig;
import june1.db.common.config.MybatisConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

//@Import(JdbcBasicConfig.class)
//@SpringBootApplication(scanBasePackages = {"june1.db.basic.controller"})

@Import(JpaConfig.class)
//@Import(MybatisConfig.class)
//@Import(JdbcTemplateConfig.class)
//@Import(MemoryDbConfig.class)
@SpringBootApplication(scanBasePackages = {"june1.db.controller"})
public class DbApplication {

    public static void main(String[] args) {
        SpringApplication.run(DbApplication.class, args);
    }
}
