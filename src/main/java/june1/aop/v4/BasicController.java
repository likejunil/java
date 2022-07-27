package june1.aop.v4;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@ResponseBody
@RequestMapping("/v4")
@RequiredArgsConstructor
public class BasicController {

    protected final static String title = "BasicController.hello()";
    protected final static String hello = "/hello";
    protected final static String hi = "/hi";
    private final BasicService basicService;

    @GetMapping(hello)
    public String hello(@RequestParam String name) {
        basicService.save(name);
        return "ok";
    }

    @GetMapping(hi)
    public String hi() {
        return "hi";
    }
}
