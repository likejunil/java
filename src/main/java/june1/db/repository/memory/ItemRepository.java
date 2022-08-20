package june1.db.repository.memory;

import june1.db.domain.Item;
import june1.db.repository.dto.SearchItemCond;
import june1.db.repository.dto.UpdateItemDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toList;

@Slf4j
public class ItemRepository implements june1.db.repository.ItemRepository {

    private static final Map<Long, Item> store = new ConcurrentHashMap<>();
    private static Long seq = 0L;

    @Override
    public Item save(Item item) {
        Long id = null;
        synchronized (this) {
            id = ++seq;
        }
        store.put(id, item.create(id));
        return item;
    }

    @Override
    public void update(Long id, UpdateItemDto dto) {
        Item load = store.get(id);
        if (load == null) {
            log.error("아이템[{}]이 존재하지 않습니다.", id);
            throw new NoSuchElementException(id + ": 해당 아이템이 존재하지 않습니다.");
        }

        store.put(id, load.name(dto.getName())
                .price(dto.getPrice())
                .quantity(dto.getQuantity()));
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Item> findAll(SearchItemCond cond) {
        return store.values()
                .stream()
                .filter(m -> {
                    if (ObjectUtils.isEmpty(cond.getName())) return true;
                    return m.getName().contains(cond.getName());
                }).filter(m -> {
                    if (cond.getMaxPrice() == null) return true;
                    return m.getPrice() <= cond.getMaxPrice();
                }).collect(toList());
    }
}
