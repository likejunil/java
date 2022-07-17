package june1.aop.v2;

import june1.aop.trace.LogTrace;
import june1.aop.trace.TemplateMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v2")
@RequiredArgsConstructor
public class TemplateMethodController {

    private final static String title = "TemplateMethodController.hello()";
    private final static String hello = "/hello";
    private final TemplateMethodService templateMethodService;
    private final LogTrace trace;

    @GetMapping(hello)
    public String hello(@RequestParam String name) {
        TemplateMethod<String> template = new TemplateMethod<>(trace) {
            @Override
            public String call() {
                templateMethodService.save(name);
                return "ok";
            }
        };

        return template.execute(title);
    }
}
