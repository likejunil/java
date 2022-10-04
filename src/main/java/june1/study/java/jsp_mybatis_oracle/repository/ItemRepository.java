package june1.study.java.jsp_mybatis_oracle.repository;

import june1.study.java.jsp_mybatis_oracle.domain.Item;
import june1.study.java.jsp_mybatis_oracle.repository.dto.ItemSearchCond;
import june1.study.java.jsp_mybatis_oracle.repository.dto.ItemUpdateDto;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    int save(Item item);

    int update(Long id, ItemUpdateDto dto);

    Optional<Item> findById(Long id);

    List<Item> findAll(ItemSearchCond cond);

    int deleteById(Long id);
}
