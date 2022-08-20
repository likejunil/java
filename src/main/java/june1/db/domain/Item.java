package june1.db.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Item {

    private Long id;
    private String name;
    private Integer price;
    private Integer quantity;

    public Item create(Long id) {
        this.id = id;
        return this;
    }

    public Item name(String name) {
        this.name = name;
        return this;
    }

    public Item price(Integer price) {
        this.price = price;
        return this;
    }

    public Item quantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }
}
