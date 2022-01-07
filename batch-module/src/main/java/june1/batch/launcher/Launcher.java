package june1.batch.launcher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class Launcher {

    private final ApplicationContext context;
    private final JobLauncher jobLauncher;

    public void executeBatch(String name, Map<String, Object> params) {

        //job 생성
        Job job = context.getBean(name, Job.class);
        log.info("222... [{}] 이름의 job 을 실행합니다.", name);

        //job parameter 생성
        JobParametersBuilder builder = new JobParametersBuilder();
        params.entrySet().forEach(e -> {
            Object v = e.getValue();
            if (v instanceof String) builder.addString(e.getKey(), (String) v);
            else if (v instanceof Long) builder.addLong(e.getKey(), (Long) v);
            else if (v instanceof Double) builder.addDouble(e.getKey(), (Double) v);
            else if (v instanceof Date) builder.addDate(e.getKey(), (Date) v);
        });

        //job 실행
        ExitStatus exitStatus = null;
        try {
            exitStatus = jobLauncher
                    .run(job, builder.toJobParameters())
                    .getExitStatus();
            log.info("444... job 을 실행하였습니다. 응답코드=[{}]", exitStatus);
        } catch (JobExecutionAlreadyRunningException
                | JobRestartException
                | JobInstanceAlreadyCompleteException
                | JobParametersInvalidException e) {
            log.error("job 실행 실패(1)=[{}]", e.getMessage());
        } catch (Exception e) {
            log.error("job 실행 실패(2)=[{}]", e.getMessage());
        }
    }
}
