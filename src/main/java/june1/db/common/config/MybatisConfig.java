package june1.db.common.config;

import june1.db.repository.mybatis.ItemMapper;
import june1.db.repository.mybatis.MybatisItemRepository;
import june1.db.service.ItemService;
import june1.db.service.ItemServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MybatisConfig {

    private final ItemMapper mapper;

    @Bean
    public MybatisItemRepository itemRepository() {
        return new MybatisItemRepository(mapper);
    }

    @Bean
    public ItemService itemService() {
        return new ItemServiceImpl(itemRepository());
    }
}
