package june1.db.repository.mybatis;

import june1.db.domain.Item;
import june1.db.repository.dto.SearchItemCond;
import june1.db.repository.dto.UpdateItemDto;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ItemMapper {

    void save(Item item);

    void update(@Param("id") Long id, @Param("dto") UpdateItemDto dto);

    Optional<Item> findById(Long id);

    List<Item> findAll(SearchItemCond cond);

    //애노테이션은 xml 파일과 중복되어서는 안된다.
    @Insert("insert into item (name, price, quantity)"
            + "values (#{name}, 0, #{quantity})")
    void insertFreeItem(Item item);

    //애노테이션은 xml 파일과 중복되어서는 안된다.
    @Update("update item set price = #{price} where price <= 0")
    void updateNegativePrice(int price);

    //애노테이션은 xml 파일과 중복되어서는 안된다.
    //#{} 문법은 ? 를 넣고 파라미터를 바인딩하는 PrepareStatement 를 사용한다.
    //반면 ${} 문법은 파라미터 바인딩이 아니라 문자 그대로를 입력한다.
    //따라서 ${} 사용은 SQL Injection 공격에 취약하다.
    //가능하다면 사용하지 말아야 한다.
    @Select("select * from item where quantity = 0 order by ${column}")
    List<Item> findSoldOut(String column);

    //애노테이션은 xml 파일과 중복되어서는 안된다.
    @Delete("delete from item where quantity <= 0")
    void deleteSoldOut();
}
