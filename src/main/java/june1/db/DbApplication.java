package june1.db;

import june1.db.common.config.JdbcConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import(JdbcConfig.class)
@SpringBootApplication(scanBasePackages = {
        "june1.db.service",
        "june1.db.controller"
})
public class DbApplication {

    public static void main(String[] args) {
        SpringApplication.run(DbApplication.class, args);
    }

}
