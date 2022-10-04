package june1.study.java.jsp_mybatis_oracle.repository.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ItemSearchCond {

    private String itemName;
    private Integer maxPrice;
}
