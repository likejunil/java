package june1.study.java.jsp_mybatis_oracle.controller;

import june1.study.java.jsp_mybatis_oracle.domain.Item;
import june1.study.java.jsp_mybatis_oracle.repository.dto.ItemSearchCond;
import june1.study.java.jsp_mybatis_oracle.repository.dto.ItemUpdateDto;
import june1.study.java.jsp_mybatis_oracle.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/item")
@RestController
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/{id}")
    public Item query(@PathVariable Long id) {
        return itemService.findOne(id);
    }

    @PostMapping
    public Integer register(@RequestBody Item item) {
        return itemService.saveOne(item);
    }

    @GetMapping
    public List<Item> list(@ModelAttribute ItemSearchCond cond) {
        return itemService.getList(cond);
    }

    @PutMapping("/{id}")
    public Item update(@PathVariable Long id, @RequestBody ItemUpdateDto item) {
        return itemService.updateOne(id, item);
    }

    @DeleteMapping("/{id}")
    public Integer remove(@PathVariable Long id) {
        return itemService.delete(id);
    }

    @GetMapping("/sold-out")
    public List<Item> querySoldOut() {
        return itemService.querySoldOut();
    }

    @DeleteMapping("/free")
    public Integer removeFreeItem() {
        return itemService.deleteFreeItem();
    }

    @PutMapping("/buy/{id}")
    public Integer buyItem(@PathVariable Long id, Integer quantity) {
        return itemService.buyItem(id, quantity);
    }
}
