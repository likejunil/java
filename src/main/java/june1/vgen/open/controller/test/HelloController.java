package june1.vgen.open.controller.test;

import june1.vgen.open.common.exception.WhatTheFuckException;
import june1.vgen.open.common.exception.client.MondayException;
import june1.vgen.open.controller.common.Response;
import june1.vgen.open.controller.test.dto.HelloReqDto;
import june1.vgen.open.controller.test.dto.HelloResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class HelloController {

    private final MessageSource messageSource;

    @GetMapping("/hello")
    @PreAuthorize("hasRole('CORP_SMALL_POWER_PLANT')")
    public Response<HelloResDto> hello(
            @Valid @ModelAttribute HelloReqDto reqDto,
            BindingResult bindingResult) {

        log.info("hello 도착했습니다.");
        if (reqDto.getAge() == null || reqDto.getAge() < 0) {
            bindingResult.reject("Total", new Object[]{"하늘", "땅"}, null);
        }
        if (bindingResult.hasErrors()) {
            return Response.error(null, bindingResult, messageSource);
        }

        return Response.ok(HelloResDto.builder()
                .age(reqDto.getAge())
                .name(reqDto.getName())
                .build());
    }

    @GetMapping("/bad")
    public Response<HelloResDto> bad(@Valid @ModelAttribute HelloReqDto reqDto)
            throws WhatTheFuckException {

        log.info("bad 도착했습니다.");
        if (reqDto.getName().equals("monday")) {
            throw MondayException.builder()
                    .code("bad")
                    .message("월요일은 나빠요..")
                    .object("HelloReqDto")
                    .field("name")
                    .rejectedValue(reqDto.getName())
                    .build();
        }

        //핸들링하지 않는 예외의 경우 WAS 에게 전달되고..
        //결국 WAS 에 의해 /error 요청이 다시 발생한다.
        //이러한 /error 요청은 BasicErrorController 에 의해서 처리된다.
        throw new WhatTheFuckException("이게 도대체 무슨 일이고?");
    }

    @GetMapping("/redirect")
    public Response<HelloResDto> redirect(
            HttpServletResponse response,
            @Valid @ModelAttribute HelloReqDto reqDto) throws IOException {

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
            @Valid @ModelAttribute HelloReqDto reqDto) throws IOException, ServletException {

        log.info("forward 도착했습니다.");
        request.getRequestDispatcher("/hello")
                .forward(request, response);

        log.info("여기에 올 수 있나요?");
        return Response.ok(HelloResDto.builder()
                .age(reqDto.getAge())
                .name(reqDto.getName())
                .build());
    }
}
