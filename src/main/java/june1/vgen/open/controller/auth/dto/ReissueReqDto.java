package june1.vgen.open.controller.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
public class ReissueReqDto {

    @NotBlank
    private String token;
}
