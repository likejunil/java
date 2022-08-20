package june1.db.repository.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchItemCond {

    private String name;
    private Integer maxPrice;
}
