package june1.batch.job.tasklet;

import june1.batch.repository.MemberRepository;
import june1.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Slf4j
@JobScope
@Component
@RequiredArgsConstructor
public class SampleTasklet implements Tasklet {

    private final MemberRepository memberRepository;

    @Override
    public RepeatStatus execute(
            StepContribution contribution, ChunkContext chunkContext) throws Exception {

        Member saved = memberRepository.save(Member.builder().name("june1").age(48).build());
        Member load = memberRepository.findById(saved.getId())
                .orElseThrow(() -> {
                    log.error("{} 고유번호의 Member 가 존재하지 않음..", saved.getId());
                    return new RuntimeException(
                            saved.getId() + " 고유번호의 Member 가 존재하지 않습니다.");
                });

        log.info("생성된 Member 의 고유번호: [{}]", load.getId());
        return RepeatStatus.FINISHED;
    }
}