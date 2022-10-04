package june1.study.java.jsp_mybatis_oracle.repository.mybatis;

import june1.study.java.jsp_mybatis_oracle.domain.Item;
import june1.study.java.jsp_mybatis_oracle.repository.dto.ItemSearchCond;
import june1.study.java.jsp_mybatis_oracle.repository.dto.ItemUpdateDto;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

/**
 * MyBatis Mapping XML 을 호출해주는 interface 이다.
 * 즉, 해당 interface 와 mapping 되는 xml 을 생성해 주어야 한다.
 */
@Mapper
public interface ItemMapper {

    int save(Item item);

    /* 인자가 2개 이상이면 반드시 @Param() 을 사용해야 한다. */
    int update(@Param("id") Long id, @Param("updateParam") ItemUpdateDto updateParam);

    Optional<Item> findById(Long id);

    List<Item> findAll(ItemSearchCond itemSearch);

    int deleteById(Long id);

    @Insert("INSERT INTO item (id, item_name, price, quantity)"
            + " VALUES (seq_item.nextval, #{itemName}, 0, #{quantity})")
    int insertFreeItem(Item item);

    @Update("UPDATE item SET"
            + " quantity = quantity - #{quantity}"
            + " WHERE id = #{id}"
            + " AND quantity >= #{quantity}")
    int buyItem(Long id, Integer quantity);

    @Select("SELECT * FROM item WHERE quantity = 0")
    List<Item> querySoldOut();

    @Delete("DELETE FROM item WHERE price = 0")
    int deleteFreeItem();
}
