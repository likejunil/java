package june1.db.repository.mybatis;

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
public class MybatisItemRepository implements ItemRepository {

    private final ItemMapper mapper;

    @Override
    public Item save(Item item) {
        mapper.save(item);
        return item;
    }

    @Override
    public void update(Long id, UpdateItemDto dto) {
        mapper.update(id, dto);
    }

    @Override
    public Optional<Item> findById(Long id) {
        return mapper.findById(id);
    }

    @Override
    public List<Item> findAll(SearchItemCond cond) {
        return mapper.findAll(cond);
    }
}
