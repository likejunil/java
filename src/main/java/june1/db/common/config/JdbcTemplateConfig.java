package june1.db.common.config;

import june1.db.repository.ItemRepository;
import june1.db.repository.template.JdbcItemRepositoryV2;
import june1.db.service.ItemService;
import june1.db.service.ItemServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class JdbcTemplateConfig {

    //사용자가 등록한 dataSource 가 없으므로..
    //스프링부트가 자동으로 dataSource 와 transaction manager 를 생성하여 등록해준다.
    private final DataSource dataSource;

    @Bean
    public ItemRepository itemRepository() {
        //return new JdbcItemRepositoryV1(dataSource);
        return new JdbcItemRepositoryV2(dataSource);
    }

    @Bean
    public ItemService itemService() {
        return new ItemServiceImpl(itemRepository());
    }
}
