package june1.vgen.open.controller.test;

import june1.vgen.open.controller.common.Response;
import june1.vgen.open.controller.test.dto.AnimalReqDto;
import june1.vgen.open.controller.test.dto.AnimalResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/animal")
public class AnimalController {

    @GetMapping("/cat")
    public Response<AnimalResDto> cat(@Valid @ModelAttribute AnimalReqDto reqDto) {
        return Response.ok(AnimalResDto.builder()
                .name(reqDto.getName())
                .age(reqDto.getAge())
                .build());
    }

    //다음 애노테이션은 효과를 발휘하지 못한다.
    //uri 에 대한 인가 정보가 우선 순위가 더 높기 때문이다.
    //@PreAuthorize("hasRole('CORP_AGGREGATOR')")
    @GetMapping("/dog")
    public Response<AnimalResDto> dog(@Valid @ModelAttribute AnimalReqDto reqDto) {
        return Response.ok(AnimalResDto.builder()
                .name(reqDto.getName())
                .age(reqDto.getAge())
                .build());
    }
}
