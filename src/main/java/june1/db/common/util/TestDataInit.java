package june1.db.common.util;

import june1.db.domain.Item;
import june1.db.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@Slf4j
@RequiredArgsConstructor
public class TestDataInit {

    private final ItemRepository itemRepository;

    //스프링 컨테이너가 완전히 초기화를 따 끝내고 실행 준비가 되었을 때 발생하는 이벤트이다.
    //@PostConstruct 의 경우 AOP 와 같은 부분이 아직 처리되지 않은 시점에 호출될 가능성이 있다.
    //예를 들면 @Transactional 과 같은..

    @EventListener(ApplicationReadyEvent.class)
    public void initData() {
        log.info("테스트 데이터를 초기화 합니다.");
        itemRepository.save(Item.builder().name("itemA").price(10_000).quantity(20).build());
        itemRepository.save(Item.builder().name("itemB").price(20_000).quantity(50).build());
    }
}
