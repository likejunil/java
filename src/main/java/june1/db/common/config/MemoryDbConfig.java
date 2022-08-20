package june1.db.common.config;

import june1.db.common.util.TestDataInit;
import june1.db.controller.HomeController;
import june1.db.controller.ItemController;
import june1.db.repository.ItemRepository;
import june1.db.repository.MemoryItemRepository;
import june1.db.service.ItemService;
import june1.db.service.MemoryItemService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class MemoryDbConfig {

    @Bean
    public ItemRepository memoryItemRepository() {
        return new MemoryItemRepository();
    }

    @Bean
    public ItemService memoryItemService() {
        return new MemoryItemService(memoryItemRepository());
    }

    @Bean
    public HomeController HomeController() {
        return new HomeController();
    }

    @Bean
    public ItemController itemController() {
        return new ItemController(memoryItemService());
    }

    @Bean
    @Profile("local")
    public TestDataInit testDataInit() {
        return new TestDataInit(memoryItemRepository());
    }
}
