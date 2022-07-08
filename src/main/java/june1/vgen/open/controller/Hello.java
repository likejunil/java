package june1.vgen.open.controller;

import june1.vgen.open.common.exception.MondayException;
import june1.vgen.open.controller.common.Response;
import june1.vgen.open.controller.dto.HelloReqDto;
import june1.vgen.open.controller.dto.HelloResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class Hello {

    @GetMapping("/hello")
    public Response<HelloResDto> hello(@ModelAttribute HelloReqDto reqDto) {
        log.info("hello 도착했습니다.");
        return Response.ok(HelloResDto.builder()
                .age(reqDto.getAge())
                .name(reqDto.getName())
                .build());
    }

    @GetMapping("/bad")
    public Response<HelloResDto> bad(@ModelAttribute HelloReqDto reqDto) {
        log.info("bad 도착했습니다.");
        throw MondayException.builder()
                .code("bad")
                .message("월요일은 나빠요..")
                .object("HelloReqDto")
                .field("name")
                .rejectedValue(reqDto.getName())
                .build();
    }

    @GetMapping("/redirect")
    public Response<HelloResDto> redirect(
            HttpServletResponse response,
            @ModelAttribute HelloReqDto reqDto) throws IOException {

        log.info("redirect 도착했습니다.");
        response.sendRedirect("/hello");
        return Response.ok(HelloResDto.builder()
                .age(reqDto.getAge())
                .name(reqDto.getName())
                .build());
    }

    @GetMapping("/forward")
    public Response<HelloResDto> forward(
            HttpServletRequest request,
            HttpServletResponse response,
            @ModelAttribute HelloReqDto reqDto) throws IOException, ServletException {

        log.info("forward 도착했습니다.");
        request.getRequestDispatcher("/hello").forward(request, response);

        log.info("여기에 올 수 있나요?");
        return Response.ok(HelloResDto.builder()
                .age(reqDto.getAge())
                .name(reqDto.getName())
                .build());
    }
}
