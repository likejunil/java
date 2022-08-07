package june1.vgen.open.controller.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class ReissueReqDto {

    @NotBlank
    private String token;
}
