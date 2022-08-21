package june1.db.domain;

import lombok.*;

import javax.persistence.*;

@Setter
@Entity
@Table(name = "item")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 64)
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
