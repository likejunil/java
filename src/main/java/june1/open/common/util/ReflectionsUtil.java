package june1.open.common.util;

import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toList;
import static june1.open.common.ConstantInfo.BASE_PACKAGE;

@Slf4j
public class ReflectionsUtil {

    private static final Map<String, Map<Class<? extends Annotation>, Class<?>>> cache = new ConcurrentHashMap<>();

    public static Class<?> getClass(String name, Class<? extends Annotation> clazz) {

        Class<?> target = cache.get(name) != null ? cache.get(name).get(clazz) : null;

        if (target == null) {
            Reflections reflector = new Reflections(BASE_PACKAGE);
            List<Class<?>> list = reflector.getTypesAnnotatedWith(clazz)
                    .stream()
                    .filter(m -> m.getSimpleName().equals(name))
                    .collect(toList());

            //인자로 전달된 애노테이션이 적용된 클래스가 존재하지 않음
            //백업 대상 엔티티가 아니라는 뜻..
            if (list.isEmpty()) {
                return null;
            }

            //같은 이름의 클래스에 같은 애노테이션을 적용한 경우..
            //어떤 클래스를 대상으로 백업을 해야할지 결정할 수 없음..
            if (list.size() > 1) {
                log.error("class=[{}] annotation=[{}] 조건의 클래스 개수=[{}] 너무 많아요. ㅜㅠ",
                        name, clazz.getSimpleName(), list.size());
                throw new RuntimeException("조건을 만족하는 클래스가 너무 많아요.");
            }

            target = list.get(0);
            cache.put(name, Map.of(clazz, target));
        } else {
            log.debug("class=[{}] annotation=[{}] 조건의 클래스를 캐쉬에서 읽었습니다.",
                    name, clazz.getSimpleName());
        }

        return target;
    }
}
