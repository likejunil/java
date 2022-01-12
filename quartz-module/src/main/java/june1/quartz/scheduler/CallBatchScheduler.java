package june1.quartz.scheduler;

import june1.quartz.config.ConstantConfig;
import june1.quartz.job.CallBatchJob;
import june1.quartz.launcher.Launcher;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@AllArgsConstructor
@Builder
public class CallBatchScheduler {

    private Launcher launcher;
    private String group;
    private String batch;
    private String period;

    public void start() throws SchedulerException {
        //-----------------------
        // job 생성, 인자 사용
        //-----------------------
        JobDataMap params = new JobDataMap();
        params.put(ConstantConfig.ARG_LAUNCHER, launcher);
        params.put(ConstantConfig.ARG_GROUP, group);
        params.put(ConstantConfig.ARG_BATCH, batch);
        params.put(ConstantConfig.ARG_PERIOD, period);

        JobDetail jobDetail = JobBuilder
                .newJob(CallBatchJob.class)
                .withIdentity(batch, group)
                .setJobData(params)
                .build();

        //-----------------------
        // trigger 생성
        //-----------------------
        Set<Trigger> triggers = new HashSet<>();
        triggers.add(TriggerBuilder.newTrigger()
                //.startAt()
                .withIdentity(batch, group)
                .withSchedule(CronScheduleBuilder.cronSchedule(period))
                .forJob(jobDetail)
                .build());

        //-----------------------
        // scheduler 생성
        //-----------------------
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.scheduleJob(jobDetail, triggers, false);
        scheduler.start();
    }
}
