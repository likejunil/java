package june1.vgen.open.domain;

import lombok.*;

import javax.persistence.Embeddable;

@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class History {

    private Long originalSeq;
    private Long memberSeq;
    private Integer action;
}
