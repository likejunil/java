package june1.db.repository.jpa;

import june1.db.domain.Item;
import june1.db.repository.ItemRepository;
import june1.db.repository.dto.SearchItemCond;
import june1.db.repository.dto.UpdateItemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
//@Repository 애노테이션 덕분에 Jpa 예외를 Spring 예외로 변환해 준다.(프록시 사용)
//물론 @Repository 가 @Component 를 포함하기 때문에 Spring bean 으로 등록된다.
@Repository
//@Transactional 애노테이션 덕분에 프록시가 생성되어 트랜잭션을 해결해준다.
//물론 Service 계층에서 @Transactional 을 사용해도 상관 없다.
@Transactional
public class JpaItemRepository implements ItemRepository {

    private final EntityManager em;

    @Override
    public Item save(Item item) {
        em.persist(item);
        return item;
    }

    @Override
    public void update(Long id, UpdateItemDto dto) {
        Item item = em.find(Item.class, id);
        item.name(dto.getName())
                .price(dto.getPrice())
                .quantity(dto.getQuantity());

        //별도의 갱신 메서드가 존재하지 않는다.
        //캐시에 저장되었다가 트랜잭션 커밋의 순간에 데이터베이스에 반영된다.
    }

    @Override
    public Optional<Item> findById(Long id) {
        Item item = em.find(Item.class, id);
        //em.find() 는 조회 데이터가 존재하지 않을 경우 null 을 반환한다.
        return Optional.ofNullable(item);
    }

    @Override
    public List<Item> findAll(SearchItemCond cond) {
        StringBuilder jpql = new StringBuilder("select i from Item i where 1 = 1");

        Map<String, Object> params = new HashMap<>();
        if (StringUtils.hasText(cond.getName())) {
            jpql.append(" and name like concat('%', :name, '%')");
            params.put("name", cond.getName());
        }
        if (cond.getMaxPrice() != null) {
            jpql.append(" and price <= :price");
            params.put("price", cond.getMaxPrice());
        }

        TypedQuery<Item> query = em.createQuery(jpql.toString(), Item.class);
        params.forEach(query::setParameter);

        return query.getResultList();
    }
}
