package june1.db.repository.mybatis;

import june1.db.domain.Item;
import june1.db.repository.dto.SearchItemCond;
import june1.db.repository.dto.UpdateItemDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ItemMapper {

    void save(Item item);

    void update(@Param("id") Long id, @Param("dto") UpdateItemDto dto);

    Optional<Item> findById(Long id);

    List<Item> findAll(SearchItemCond cond);
}
