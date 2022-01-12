package june1;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class QuartzMain {

    public static void main(String[] args) {
        //SpringApplication.run(QuartzMain.class, args);
        System.exit(SpringApplication.exit(SpringApplication.run(QuartzMain.class, args)));
    }
}
