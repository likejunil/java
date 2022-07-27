package june1.aop.v4.proxy;

import june1.aop.trace.LogTrace;
import june1.aop.trace.TraceStatus;
import june1.aop.v4.BasicController;
import june1.aop.v4.BasicService;
import org.springframework.web.bind.annotation.GetMapping;

public class ControllerProxy extends BasicController {

    private final LogTrace logTrace;
    private final BasicService basicService;

    public ControllerProxy(BasicService basicService, LogTrace logTrace) {
        super(basicService);
        this.logTrace = logTrace;
        this.basicService = basicService;
    }

    @Override
    @GetMapping(BasicController.hello)
    public String hello(String name) {
        TraceStatus status = null;
        try {
            status = logTrace.begin(BasicController.title);
            //-----------------------------
            String ret = super.hello(name);
            //-----------------------------
            logTrace.end(status);
            return ret;
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }

    @Override
    @GetMapping(BasicController.hi)
    public String hi() {
        return super.hi();
    }
}
