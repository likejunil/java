package june1.study.java.jsp_mybatis_oracle.repository.mybatis;


import june1.study.java.jsp_mybatis_oracle.domain.Item;
import june1.study.java.jsp_mybatis_oracle.repository.ItemRepository;
import june1.study.java.jsp_mybatis_oracle.repository.dto.ItemSearchCond;
import june1.study.java.jsp_mybatis_oracle.repository.dto.ItemUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class MybatisItemRepository implements ItemRepository {

    private final ItemMapper itemMapper;

    @Override
    public int save(Item item) {
        if (item.getPrice() == 0) {
            log.info("무료 아이템 등록: [{}]", item.getItemName());
            return itemMapper.insertFreeItem(item);
        } else {
            return itemMapper.save(item);
        }
    }

    @Override
    public int update(Long id, ItemUpdateDto dto) {
        return itemMapper.update(id, dto);
    }

    @Override
    public Optional<Item> findById(Long id) {
        return itemMapper.findById(id);
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        return itemMapper.findAll(cond);
    }

    @Override
    public int deleteById(Long id) {
        return itemMapper.deleteById(id);
    }
}
