package june1.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.LocalDateTime;

@Slf4j
public class SayHelloJob extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        //실행 시각 출력하기
        log.info("job 이 실행되었습니다. => {}", LocalDateTime.now());

        //Job 에게 전달된 파라미터 얻어오기
        JobDetail jobDetail = context.getJobDetail();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        String id = jobDataMap.get("id").toString();
        log.info("id 가 전달되었습니다. => {}", id);

        //Trigger 에서 정보를 얻어오기
        Trigger trigger = context.getTrigger();
        TriggerKey triggerKey = trigger.getKey();
        JobKey jobKey = trigger.getJobKey();

        //인사하기
        log.info("안녕하세요~!! 트리거키=[{}], 잡키=[{}]",
                triggerKey.toString(), jobKey.toString());
    }
}
