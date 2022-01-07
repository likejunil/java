package june1.quartz.scheduler;

import june1.batch.launcher.Launcher;
import june1.quartz.job.CallBatchJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class CallBatchScheduler {

    private final Launcher launcher;

    @PostConstruct
    void init() throws SchedulerException {
        log.info("론처를 알려주마... {}", launcher);
        this.start();
    }

    private void start() throws SchedulerException {
        //-----------------------
        // job 생성, 인자 사용
        //-----------------------
        JobDataMap params = new JobDataMap();
        params.put("__launcher__", launcher);
        params.put("__name__", "jobByQuartz");
        params.put("date", new Date());
        params.put("id", "june1");
        params.put("age", 48);
        params.put("weight", 175.6);

        JobDetail jobDetail = JobBuilder
                .newJob(CallBatchJob.class)
                .withIdentity("테스트-잡", "배치-그룹")
                .setJobData(params)
                .build();

        //-----------------------
        // trigger 생성
        //-----------------------
        SimpleTrigger simpleTrigger = TriggerBuilder.newTrigger()
                .withIdentity("테스트-트리거(1)", "배치-그룹")
                .withSchedule(SimpleScheduleBuilder
                        .simpleSchedule()
                        .withIntervalInSeconds(5)
                        .withRepeatCount(10))
                .forJob(jobDetail)
                .build();

        CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                .withIdentity("테스트-트리거(2)", "배치-그룹")
                .withSchedule(CronScheduleBuilder.cronSchedule("0/7 * * * * ?"))
                .forJob(jobDetail)
                .build();

        Set<Trigger> triggers = new HashSet<>();
        triggers.add(simpleTrigger);
        //triggers.add(cronTrigger);

        //-----------------------
        // scheduler 생성
        //-----------------------
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.scheduleJob(jobDetail, triggers, false);
        scheduler.start();
    }
}
