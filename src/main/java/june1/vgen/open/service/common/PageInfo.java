package june1.vgen.open.service.common;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Getter
@Builder
public class PageInfo {

    long totalElements;
    int totalPages;
    int size;
    int number;
    int numberOfElements;
    boolean first;
    boolean last;

    public static PageInfo by(Page page) {
        return PageInfo.builder()
                .first(page.isFirst())
                .last(page.isLast())
                .number(page.getNumber())
                .numberOfElements(page.getNumberOfElements())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    public static PageInfo empty(Pageable page) {
        return PageInfo.builder()
                .first(true)
                .last(true)
                .number(0)
                .numberOfElements(0)
                .size(page.getPageSize())
                .totalElements(0)
                .totalPages(0)
                .build();
    }
}
