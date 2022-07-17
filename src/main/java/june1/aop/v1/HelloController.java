package june1.aop.v1;

import june1.aop.trace.LogTrace;
import june1.aop.trace.TraceStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class HelloController {

    private final static String title = "HelloController.hello()";
    private final static String hello = "/hello";
    private final HelloService helloService;
    private final LogTrace trace;

    @GetMapping(hello)
    public String hello(@RequestParam String name) {
        TraceStatus status = null;
        try {
            status = trace.begin(title);
            helloService.save(name);
            trace.end(status);
            return "ok";

        } catch (Exception e) {
            trace.exception(status, e);
            throw e;
        }
    }
}
