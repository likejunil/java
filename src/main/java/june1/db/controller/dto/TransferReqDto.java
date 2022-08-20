package june1.db.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TransferReqDto {

    private String from;
    private String to;
    private Long amount;
}
