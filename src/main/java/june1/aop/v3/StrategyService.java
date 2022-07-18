package june1.aop.v3;

import june1.aop.trace.Context;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StrategyService {

    private final static String title = "StrategyService.save()";
    private final StrategyRepository strategyRepository;
    private final Context<Void> context;

    public void save(String hello) {
        context.execute(title, () -> {
            strategyRepository.save(hello);
            return null;
        });
    }
}
