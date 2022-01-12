package june1.batch.job;

import june1.batch.config.ColorBatchListConfig;
import june1.batch.config.FruitBatchListConfig;
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

import java.util.Date;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class QuartzJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final SampleTasklet sampleTasklet;

    @Bean(name = "apple")
    public Job jobByQuartz() {
        return jobBuilderFactory
                .get("쿼츠_잡")
                .incrementer(new RunIdIncrementer())
                .start(stepByQuartz_2(null, null, null, null, null))
                .build();
    }

    @Bean
    @JobScope
    public Step stepByQuartz_1(@Value("#{jobParameters[birthday]}") Date birthday,
                               @Value("#{jobParameters[id]}") String id,
                               @Value("#{jobParameters[age]}") Long age,
                               @Value("#{jobParameters[weight]}") Double weight,
                               @Value("#{jobParameters[period]}") String period) {

        return stepBuilderFactory
                .get("쿼츠_스텝_1")
                .tasklet((contribution, chunkContext) -> {
                    log.info("-- 스텝 실행 완료 date:[{}], id:[{}], age=[{}], weight=[{}], period=[{}] --",
                            birthday, id, age, weight, period);
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    @JobScope
    public Step stepByQuartz_2(@Value("#{jobParameters[birthday]}") Date birthday,
                               @Value("#{jobParameters[id]}") String id,
                               @Value("#{jobParameters[age]}") Long age,
                               @Value("#{jobParameters[weight]}") Double weight,
                               @Value("#{jobParameters[period]}") String period) {

        return stepBuilderFactory
                .get("쿼츠_스텝_2")
                .tasklet(sampleTasklet)
                .build();
    }
}
