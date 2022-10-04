package june1.study.java.jsp_mybatis_oracle.repository.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ItemUpdateDto {

    private String itemName;
    private Integer price;
    private Integer quantity;
}
