package june1.db.repository.template;

import june1.db.domain.Item;
import june1.db.repository.ItemRepository;
import june1.db.repository.dto.SearchItemCond;
import june1.db.repository.dto.UpdateItemDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class ItemRepositoryV2 implements ItemRepository {

    private final NamedParameterJdbcTemplate template;
    private final SimpleJdbcInsert jdbcInsert;

    public ItemRepositoryV2(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("item")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Item save(Item item) {
        Long id = null;
        //id = insertItem(item);
        id = simpleInsertItem(item);
        return item.create(id);
    }

    private Long simpleInsertItem(Item item) {
        SqlParameterSource params = new BeanPropertySqlParameterSource(item);
        Number key = jdbcInsert.executeAndReturnKey(params);
        return key.longValue();
    }

    private Long insertItem(Item item) {
        String sql = "insert into item (name, price, quantity) values (:name, :price, :quantity)";
        SqlParameterSource params = new BeanPropertySqlParameterSource(item);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(sql, params, keyHolder);
        return Optional.ofNullable(keyHolder.getKey())
                .map(Number::longValue)
                .orElse(null);
    }

    @Override
    public void update(Long id, UpdateItemDto dto) {
        String sql = "update item set name=:name, price=:price, quantity=:quantity where id=:id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id)
                .addValue("name", dto.getName())
                .addValue("price", dto.getPrice())
                .addValue("quantity", dto.getQuantity());

        template.update(sql, params);
    }

    @Override
    public Optional<Item> findById(Long id) {
        String sql = "select id, name, price, quantity from item where id=:id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        try {
            Item item = template.queryForObject(sql, params, itemRowMapper());
            return Optional.of(item);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Item> findAll(SearchItemCond cond) {
        String sql = "select id, name, price, quantity from item where 1 = 1";
        StringBuilder sb = new StringBuilder(sql);

        Map<String, Object> params = new HashMap<>();
        if (StringUtils.hasText(cond.getName())) {
            sb.append(" and name like concat('%', :name, '%')");
            params.put("name", cond.getName());
        }
        if (cond.getMaxPrice() != null) {
            sb.append(" and price <= :price");
            params.put("price", cond.getMaxPrice());
        }

        return template.query(sb.toString(), params, itemRowMapper());
    }

    private RowMapper<Item> itemRowMapper() {
        //return BeanPropertyRowMapper.newInstance(Item.class);
        return (rs, no) -> Item.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .price(rs.getInt("price"))
                .quantity(rs.getInt("quantity"))
                .build();
    }
}
