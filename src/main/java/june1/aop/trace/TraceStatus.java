package june1.aop.trace;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TraceStatus {

    private Long startTimeMilliSec;
    private String message;
}
