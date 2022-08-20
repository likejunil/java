package june1.db;

import june1.db.basic.common.config.JdbcBasicConfig;
import june1.db.common.config.JdbcTemplateConfig;
import june1.db.common.config.MemoryDbConfig;
import june1.db.common.config.MybatisConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

//@Import(JdbcBasicConfig.class)
//@SpringBootApplication(scanBasePackages = {"june1.db.basic.controller"})

@Import(MybatisConfig.class)
//@Import(JdbcTemplateConfig.class)
//@Import(MemoryDbConfig.class)
@SpringBootApplication(scanBasePackages = {"june1.db.controller"})
public class DbApplication {

    public static void main(String[] args) {
        SpringApplication.run(DbApplication.class, args);
    }
}
