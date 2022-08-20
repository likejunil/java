package june1.db.repository.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateItemDto {

    private String name;
    private Integer price;
    private Integer quantity;
}
