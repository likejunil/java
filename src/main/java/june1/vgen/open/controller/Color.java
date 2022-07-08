package june1.vgen.open.controller;

import june1.vgen.open.common.exception.MondayException;
import june1.vgen.open.controller.common.Response;
import june1.vgen.open.controller.dto.ColorReqDto;
import june1.vgen.open.controller.dto.ColorResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/color")
public class Color {

    @GetMapping("/hello")
    public Response<ColorResDto> hello(@ModelAttribute ColorReqDto reqDto) {
        log.info("hello 도착했습니다.");
        return Response.ok(ColorResDto.builder().color(reqDto.getColor()).build());
    }

    @GetMapping("/bad")
    public Response<ColorResDto> bad(@ModelAttribute ColorReqDto reqDto) {
        log.info("bad 도착했습니다.");
        throw new MondayException("월요일 나빠요..");
    }

    @GetMapping("/redirect")
    public Response<ColorResDto> redirect(
            HttpServletResponse response,
            @ModelAttribute ColorReqDto reqDto) throws IOException {

        log.info("redirect 도착했습니다.");
        response.sendRedirect("/color/hello");
        return Response.ok(ColorResDto.builder()
                .color(reqDto.getColor())
                .build());
    }

    @GetMapping("/forward")
    public Response<ColorResDto> forward(
            HttpServletRequest request,
            HttpServletResponse response,
            @ModelAttribute ColorReqDto reqDto) throws IOException, ServletException {

        log.info("forward 도착했습니다.");
        request.getRequestDispatcher("/color/hello").forward(request, response);

        log.info("여기에 올 수 있나요?");
        return Response.ok(ColorResDto.builder()
                .color("white")
                .build());
    }
}
