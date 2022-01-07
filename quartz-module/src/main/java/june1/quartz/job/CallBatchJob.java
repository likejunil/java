package june1.quartz.job;

import june1.batch.launcher.Launcher;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Slf4j
@Setter
public class CallBatchJob extends QuartzJobBean {


    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap param = context.getJobDetail().getJobDataMap();
        String name = param.get("__name__").toString();

        log.info("111... 쿼츠에서 배치({})를 호출합니다.", name);
        Launcher launcher = (Launcher) param.get("__launcher__");
        launcher.executeBatch(name, param);
        log.info("555... 배치 호출을 마칩니다.");
    }
}
