package june1.aop.v4.proxy;

import june1.aop.trace.LogTrace;
import june1.aop.trace.TraceStatus;
import june1.aop.v4.BasicRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RepositoryProxy extends BasicRepository {

    private final LogTrace logTrace;

    @Override
    public void save(String hello) {
        TraceStatus status = null;
        try {
            status = logTrace.begin(BasicRepository.title);
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
