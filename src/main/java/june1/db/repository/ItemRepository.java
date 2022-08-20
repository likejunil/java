package june1.db.repository;

import june1.db.domain.Item;
import june1.db.repository.dto.SearchItemCond;
import june1.db.repository.dto.UpdateItemDto;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item save(Item item);

    void update(Long id, UpdateItemDto dto);

    Optional<Item> findById(Long id);

    List<Item> findAll(SearchItemCond cond);
}
