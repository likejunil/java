package june1.study.java.jsp_mybatis_oracle.service;

import june1.study.java.jsp_mybatis_oracle.domain.Item;
import june1.study.java.jsp_mybatis_oracle.repository.ItemRepository;
import june1.study.java.jsp_mybatis_oracle.repository.dto.ItemSearchCond;
import june1.study.java.jsp_mybatis_oracle.repository.dto.ItemUpdateDto;
import june1.study.java.jsp_mybatis_oracle.repository.mybatis.ItemMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    public Item findOne(Long id) {
        return itemRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.error("존재하지 않는 아이디={}", id);
                    return new NoSuchElementException("존재하지 않는 아이디");
                });
    }

    public int saveOne(Item item) {
        return itemRepository.save(item);
    }

    public List<Item> getList(ItemSearchCond cond) {
        return itemRepository.findAll(cond);
    }

    public Item updateOne(Long id, ItemUpdateDto item) {
        int count = itemRepository.update(id, item);
        if (count == 0) return null;
        return Item.builder()
                .id(id)
                .itemName(item.getItemName())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .build();
    }

    public int delete(Long id) {
        return itemRepository.deleteById(id);
    }

    public int deleteFreeItem() {
        return itemMapper.deleteFreeItem();
    }

    public List<Item> querySoldOut() {
        return itemMapper.querySoldOut();
    }

    public int buyItem(Long id, Integer quantity) {
        return itemMapper.buyItem(id, quantity);
    }
}
