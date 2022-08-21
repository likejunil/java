package june1.db.service;

import june1.db.domain.Item;
import june1.db.repository.ItemRepository;
import june1.db.repository.dto.SearchItemCond;
import june1.db.repository.dto.UpdateItemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Override
    public Item save(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public Optional<Item> findById(Long id) {
        Optional<Item> itemOpt = itemRepository.findById(id);
        return itemOpt;
    }

    @Override
    public void update(Long id, UpdateItemDto dto) {
        itemRepository.update(id, dto);
    }

    @Override
    public List<Item> findItems(SearchItemCond cond) {
        return itemRepository.findAll(cond);
    }
}
