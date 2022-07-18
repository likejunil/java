package june1.aop.v0.proxy;

import june1.aop.trace.LogTrace;
import june1.aop.trace.TraceStatus;
import june1.aop.v0.BasicRepository;
import june1.aop.v0.BasicService;

public class ServiceProxy extends BasicService {

    private final LogTrace logTrace;
    private final BasicRepository basicRepository;

    public ServiceProxy(BasicRepository basicRepository, LogTrace logTrace) {
        super(basicRepository);
        this.logTrace = logTrace;
        this.basicRepository = basicRepository;
    }

    @Override
    public void save(String hello) {
        TraceStatus status = null;
        try {
            status = logTrace.begin(BasicService.title);
            //-----------------------------
            super.save(hello);
            //-----------------------------
            logTrace.end(status);
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }
}
