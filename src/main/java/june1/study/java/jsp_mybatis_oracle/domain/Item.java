package june1.study.java.jsp_mybatis_oracle.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    private Long id;
    private String itemName;
    private int price;
    private int quantity;
}
