package june1.quartz.job;

import june1.quartz.config.ConstantConfig;
import june1.quartz.launcher.Launcher;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;

@Slf4j
public class CallBatchJob extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap param = context.getJobDetail().getJobDataMap();
        param.put(ConstantConfig.ARG_DATE, new Date());

        Launcher launcher = (Launcher) param.get(ConstantConfig.ARG_LAUNCHER);
        String batch = param.get(ConstantConfig.ARG_BATCH).toString();
        launcher.executeBatch(batch, param);
    }
}
