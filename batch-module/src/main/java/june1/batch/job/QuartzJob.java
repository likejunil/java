package june1.batch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class QuartzJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job jobByQuartz() {
        return jobBuilderFactory
                .get("쿼츠가_불러서_실행되는_잡")
                .start(stepByQuartz())
                .build();
    }

    @Bean
    @JobScope
    public Step stepByQuartz() {
        return stepBuilderFactory
                .get("쿼츠가_불러서_실행되는_스텝")
                .tasklet((contribution, chunkContext) -> {
                    log.info("333... 배치 작업을 완료했습니다.");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
