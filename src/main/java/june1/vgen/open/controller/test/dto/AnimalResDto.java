package june1.vgen.open.controller.test.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AnimalResDto {

    private String name;
    private Integer age;
}
