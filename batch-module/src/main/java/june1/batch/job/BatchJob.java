package june1.batch.job;

import june1.batch.job.tasklet.SampleTasklet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final SampleTasklet sampleTasklet;

    @Bean(name = "tomato")
    public Job jobByBatch() {
        return jobBuilderFactory
                .get("배치_잡")
                .start(stepByBatch_2(null, null))
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    @JobScope
    public Step stepByBatch_1(@Value("#{jobParameters[id]}") String id,
                              @Value("#{jobParameters[date]}") Long age) {
        return stepBuilderFactory
                .get("배치_스텝_1")
                .tasklet((contribution, chunkContext) -> {
                    log.info("-- 스텝 실행 완료 id:[{}], age=[{}] --", id, age);
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    @JobScope
    public Step stepByBatch_2(@Value("#{jobParameters[id]}") String id,
                              @Value("#{jobParameters[date]}") Long age) {
        return stepBuilderFactory
                .get("배치_스텝_2")
                .tasklet(sampleTasklet)
                .build();
    }
}
