package june1.aop.v3;

import june1.aop.trace.Context;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v3")
@RequiredArgsConstructor
public class StrategyController {

    private final static String title = "StrategyController.hello()";
    private final static String hello = "/hello";
    private final StrategyService strategyService;
    private final Context<String> context;

    @GetMapping(hello)
    public String hello(@RequestParam String name) {
        return context.execute(title, () -> {
            strategyService.save(name);
            return "ok";
        });
    }
}
