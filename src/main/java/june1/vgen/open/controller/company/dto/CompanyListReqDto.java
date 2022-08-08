package june1.vgen.open.controller.company.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Setter
@Getter
@NoArgsConstructor
public class CompanyListReqDto {

    @Size(min = 1, max = 32)
    @Pattern(regexp = "^[^ ][a-zA-Z0-9ㄱ-ㅎ가-힣 \\-()]*[^ ]$")
    private String name;
}
