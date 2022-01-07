package june1.batch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job batchJob() {
        return jobBuilderFactory
                .get("simple-job")
                .start(batchStep(null, null))
                .build();
    }

    @Bean
    @JobScope
    public Step batchStep(@Value("#{jobParameters[id]}") String id,
                     @Value("#{jobParameters[date]}") Long age) {
        return stepBuilderFactory
                .get("simple-step")
                .tasklet((contribution, chunkContext) -> {
                    log.info("작업 완료 id:[{}], age=[{}]", id, age);
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
