//package june1.quartz.config;
//
//import june1.quartz.job.LoggingTimeJob;
//import org.quartz.*;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class QuartzConfig {
//
//    @Bean
//    public JobDetail job() {
//        JobDetail jobDetail = JobBuilder
//                .newJob(LoggingTimeJob.class)
//                .storeDurably()
//                .build();
//
//        return jobDetail;
//    }
//
//    @Bean
//    public Trigger trigger() {
//        SimpleScheduleBuilder schedule = SimpleScheduleBuilder
//                .simpleSchedule()
//                .withIntervalInSeconds(1000)
//                .withRepeatCount(2);
//
//        SimpleTrigger trigger = TriggerBuilder.newTrigger()
//                .forJob(job())
//                .withSchedule(schedule)
//                .build();
//
//        return trigger;
//    }
//}
