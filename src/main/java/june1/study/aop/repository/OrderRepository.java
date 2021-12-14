package june1.study.aop.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
public class OrderRepository {

    private static Map<Long, String> store = new ConcurrentHashMap<>();

    public void save(String itemCd) {

    }
}
