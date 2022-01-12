package june1.quartz.scheduler;

import june1.quartz.job.SayHelloJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
public class SayHelloScheduler {

    @PostConstruct
    void init() throws SchedulerException {
        //this.start();
    }

    private void start() throws SchedulerException {
        //-----------------------
        // job 생성, 인자 사용
        //-----------------------
        JobDataMap params = new JobDataMap();
        params.put("birthday", Date.from(LocalDate.of(1975, 4, 12)
                .atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        params.put("id", "june1");
        params.put("age", 48L);
        params.put("weight", 175.6);

        JobDataMap addParams = new JobDataMap();
        addParams.put("color", "orange");
        addParams.put("fruit", "apple");

        JobDetail jobDetail = JobBuilder
                .newJob(SayHelloJob.class)
                .withIdentity("강백호", "슬램덩크")
                .setJobData(params)
                .build();

        //-----------------------
        // trigger 생성
        //-----------------------
        LocalTime now = LocalTime.now();
        SimpleTrigger simpleTrigger = TriggerBuilder.newTrigger()
                .withIdentity("루피", "원피스")
                .startAt(DateBuilder.dateOf(now.getHour(), now.getMinute(), now.getSecond()))
                //특정 시점을 지정할 수도 있고
                //실행 시점으로부터 특정한 간격을 지정할 수도 있다.
                .withSchedule(SimpleScheduleBuilder
                        .simpleSchedule()
                        .withIntervalInSeconds(5)
                        .withRepeatCount(2))
                //실행 주기와 반복 횟수(무한 가능)를 정할 수 있다.
                .forJob(jobDetail)
                .build();

        CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                .withIdentity("쵸파", "원피스")
                .withSchedule(CronScheduleBuilder.cronSchedule("0/7 * * * * ?"))
                //초(0-59) 분(0-59) 시(0-23) 일(0-31) 월(1-12) 요일(1:일요일-7:토요일)
                //'모든 요일'은 ? 으로 표기
                //요일이 특정되면 '모든 일'은 ? 으로 표기
                .forJob(jobDetail)
                .build();

        Set<Trigger> triggers = new HashSet<>();
        triggers.add(simpleTrigger);
        triggers.add(cronTrigger);

        //-----------------------
        // scheduler 생성
        //-----------------------
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.scheduleJob(jobDetail, triggers, false);
        //하나의 job 에 여러개의 trigger 를 연결하여 실행할 수 있음
        //job, trigger 의 아이덴티티를 통해 중복 등록을 방지
        scheduler.start();
    }
}