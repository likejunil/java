package june1.db.repository.template;

import june1.db.domain.Item;
import june1.db.repository.ItemRepository;
import june1.db.repository.dto.SearchItemCond;
import june1.db.repository.dto.UpdateItemDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class JdbcItemRepositoryV1 implements ItemRepository {

    private final JdbcTemplate template;

    public JdbcItemRepositoryV1(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    /**
     * @param item
     * @return
     */
    @Override
    public Item save(Item item) {
        String sql = "insert into item (name, price, quantity) values (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, item.getName());
            ps.setInt(2, item.getPrice());
            ps.setInt(3, item.getQuantity());
            return ps;
        }, keyHolder);

        long id = keyHolder.getKey().longValue();
        return item.create(id);
    }

    /**
     * @param id
     * @param dto
     */
    @Override
    public void update(Long id, UpdateItemDto dto) {
        String sql = "update item set name=?, price=?, quantity=? where id=?";
        template.update(sql,
                dto.getName(),
                dto.getPrice(),
                dto.getQuantity(),
                id);
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Optional<Item> findById(Long id) {
        String sql = "select id, name, price, quantity from item where id=?";
        try {
            Item item = template.queryForObject(sql, itemRowMapper(), id);
            return Optional.of(item);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * @param cond
     * @return
     */
    @Override
    public List<Item> findAll(SearchItemCond cond) {
        String sql = "select id, name, price, quantity from item where 1 = 1";
        StringBuilder sb = new StringBuilder(sql);

        List<Object> params = new ArrayList<>();
        if (StringUtils.hasText(cond.getName())) {
            sb.append(" and name like concat('%', ?, '%')");
            params.add(cond.getName());
        }
        if (cond.getMaxPrice() != null) {
            sb.append(" and price <= ?");
            params.add(cond.getMaxPrice());
        }

        return template.query(sb.toString(), itemRowMapper(), params.toArray());
    }

    private RowMapper<Item> itemRowMapper() {
        return (rs, no) -> Item.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .price(rs.getInt("price"))
                .quantity(rs.getInt("quantity"))
                .build();
    }
}
