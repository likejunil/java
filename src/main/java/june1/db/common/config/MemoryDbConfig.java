package june1.db.common.config;

import june1.db.common.util.TestDataInit;
import june1.db.repository.memory.MemoryItemRepository;
import june1.db.service.ItemService;
import june1.db.service.ItemServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class MemoryDbConfig {

    @Bean
    public june1.db.repository.ItemRepository itemRepository() {
        return new MemoryItemRepository();
    }

    @Bean
    public ItemService itemService() {
        return new ItemServiceImpl(itemRepository());
    }

    @Bean
    @Profile("local")
    public TestDataInit testDataInit() {
        return new TestDataInit(itemRepository());
    }
}
