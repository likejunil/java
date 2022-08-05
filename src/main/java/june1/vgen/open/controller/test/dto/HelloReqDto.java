package june1.vgen.open.controller.test.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
@AllArgsConstructor
public class HelloReqDto {

    @NotBlank
    @Size(min = 2, max = 10)
    private String name;

    @Positive
    private Integer age;
}
