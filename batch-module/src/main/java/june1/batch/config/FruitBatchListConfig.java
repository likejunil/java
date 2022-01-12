package june1.batch.config;

import june1.config.PeriodConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FruitBatchListConfig {
    APPLE("apple", PeriodConstant.SECOND_30),
    TOMATO("tomato", PeriodConstant.MINUTE);

    private final String name;
    private final String period;
    }
