package june1.db.service;

import june1.db.domain.Item;
import june1.db.repository.dto.SearchItemCond;
import june1.db.repository.dto.UpdateItemDto;

import java.util.List;
import java.util.Optional;

public interface ItemService {

    Item save(Item item);

    Optional<Item> findById(Long id);

    void update(Long id, UpdateItemDto dto);

    List<Item> findItems(SearchItemCond cond);
}
