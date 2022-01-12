package june1.batch.config;

import june1.config.PeriodConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ColorBatchListConfig {
    RED("red", PeriodConstant.SECOND),
    GREEN("green", PeriodConstant.MINUTE),
    BLACK("black", PeriodConstant.MINUTE_3);

    private final String name;
    private final String period;
}
