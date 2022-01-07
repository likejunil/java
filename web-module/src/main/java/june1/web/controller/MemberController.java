package june1.web.controller;

import june1.web.dto.MemberReqDto;
import june1.web.dto.MemberResDto;
import june1.web.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public MemberResDto add(@ModelAttribute MemberReqDto memberDto) {
        return memberService.saveOne(memberDto);
    }

    @GetMapping("/age/goe")
    public List<MemberResDto> getAgeGoe(@RequestParam Integer age) {
        return memberService.getAllAgeGoe(age);
    }
}
