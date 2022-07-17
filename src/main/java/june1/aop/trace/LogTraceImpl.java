package june1.aop.trace;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogTraceImpl implements LogTrace {

    private static final String START_PREFIX = "--->";
    private static final String COMPLETE_PREFIX = "<---";
    private static final String EX_PREFIX = "<-X-";
    private static final String EMPTY_SPACE = "    ";

    public ThreadLocal<TraceId> traceIdHolder = new ThreadLocal<>();

    public TraceStatus begin(String message) {
        syncTraceId();
        TraceId traceId = traceIdHolder.get();
        long startTime = System.currentTimeMillis();
        log.info("[{}] {}{}", traceId.getId(), addSpace(START_PREFIX, traceId.getLevel()), message);
        return TraceStatus.builder()
                .startTimeMilliSec(startTime)
                .message(message)
                .build();
    }

    public void end(TraceStatus status) {
        complete(status, null);
    }

    public void exception(TraceStatus status, Exception e) {
        complete(status, e);
    }

    private void syncTraceId() {
        TraceId traceId = traceIdHolder.get();
        if (traceId == null) {
            traceId = new TraceId();
        } else {
            traceId = traceId.createNextId();
        }

        traceIdHolder.set(traceId);
    }

    private void complete(TraceStatus status, Exception e) {
        long stopTime = System.currentTimeMillis();
        TraceId traceId = traceIdHolder.get();
        String prefix = (e == null ? COMPLETE_PREFIX : EX_PREFIX);
        String exceptionMsg = (e == null ? "" : "ex=" + e.getMessage());
        log.info("[{}] {}{} time={}ms {}",
                traceId.getId(),
                addSpace(prefix, traceId.getLevel()),
                status.getMessage(),
                stopTime - status.getStartTimeMilliSec(),
                exceptionMsg);

        releaseTraceId();
    }

    private void releaseTraceId() {
        TraceId traceId = traceIdHolder.get();
        if (traceId.isFirstLevel()) {
            traceIdHolder.remove();
        } else {
            traceId = traceId.createPreviousId();
            traceIdHolder.set(traceId);
        }
    }

    private String addSpace(String prefix, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            String tmp = i == (level - 1)
                    ? "|" + prefix
                    : "|" + EMPTY_SPACE;
            sb.append(tmp);
        }

        return sb.toString();
    }
}
