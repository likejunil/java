package june1.quartz.executor;

import june1.batch.config.ColorBatchListConfig;
import june1.batch.config.FruitBatchListConfig;
import june1.quartz.config.ConstantConfig;
import june1.quartz.launcher.Launcher;
import june1.quartz.scheduler.CallBatchScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class Executor {

    private final Launcher launcher;

    @Value("${args.group}")
    private String group;

    @PostConstruct
    void start() throws SchedulerException {
        //환경변수로부터 쿼츠가 실행해야 할 배치의 그룹 정보를 읽는다.
        //실행 인자가 주어지면 환경변수를 무시하고 실행 인자를 적용한다.
        String batchGroup = System.getenv(ConstantConfig.BATCH_GROUP_ENV_KEY);
        if (group != null && !group.equals("NONE"))
            batchGroup = group;

        log.info("실행 인자: group=[{}]", group);
        if (batchGroup == null || batchGroup.equals("NONE")) {
            log.error("\n\n" +
                    ":::::::::::::::::::::::::::::::::::::::::::::::::\n" +
                    "::::: [배치 그룹에 대한 정보가 필요합니다.] :::::\n" +
                    "::::: [프로그램을 종료합니다.] :::::\n" +
                    ":::::::::::::::::::::::::::::::::::::::::::::::::\n" +
                    "\n\n");
            System.exit(-1);
        }

        int count = 0;
        for (String[] m : getBatchList(batchGroup)) {
            String name = m[0];
            String period = m[1];
            CallBatchScheduler.builder()
                    .launcher(launcher)
                    .group(group)
                    .batch(name)
                    .period(period)
                    .build()
                    .start();

            log.info("::::: [{}] 배치가 [{}] 주기로 실행되었습니다. :::::",
                    name, period);
            count++;
        }

        if (count == 0) {
            log.error("\n\n" +
                    ":::::::::::::::::::::::::::::::::::::::::::::::::\n" +
                    "::::: [실행 가능한 배치가 존재하지 않습니다.] :::::\n" +
                    "::::: [프로그램을 종료합니다.] :::::\n" +
                    ":::::::::::::::::::::::::::::::::::::::::::::::::\n" +
                    "\n\n");
            System.exit(-2);
        }

        log.info("\n\n" +
                ":::::::::::::::::::::::::::::::::::::::::::::::::\n" +
                "::::: [총 [{}]개의 배치를 실행하였습니다. :::::\n" +
                ":::::::::::::::::::::::::::::::::::::::::::::::::\n" +
                "\n\n", count);
    }

    private List<String[]> getBatchList(String batchGroup) {
        List<String[]> batchList = new LinkedList<>();

        if (batchGroup.equals(ConstantConfig.BATCH_GROUP_NAME_COLOR)) {
            batchList = Arrays.stream(ColorBatchListConfig.values())
                    .map(m -> new String[]{m.getName(), m.getPeriod()})
                    .collect(Collectors.toList());

        } else if (batchGroup.equals(ConstantConfig.BATCH_GROUP_NAME_FRUIT)) {
            batchList = Arrays.stream(FruitBatchListConfig.values())
                    .map(m -> new String[]{m.getName(), m.getPeriod()})
                    .collect(Collectors.toList());
        }

        return batchList;
    }
}
