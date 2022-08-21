package june1.db.common.config;

import june1.db.repository.ItemRepository;
import june1.db.repository.jpa.JpaItemRepository;
import june1.db.service.ItemService;
import june1.db.service.ItemServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@Configuration
@RequiredArgsConstructor
public class JpaConfig {

    private final EntityManager em;

    @Bean
    public ItemRepository itemRepository() {
        return new JpaItemRepository(em);
    }

    @Bean
    public ItemService itemService() {
        return new ItemServiceImpl(itemRepository());
    }
}
