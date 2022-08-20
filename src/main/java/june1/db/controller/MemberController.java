package june1.db.controller;

import june1.db.controller.dto.*;
import june1.db.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1")
public class MemberController {

    private final MemberService memberService;

    /**
     * 사용자 생성
     *
     * @param dto
     * @return
     * @throws SQLException
     */
    @PostMapping
    public MemberDto create(@RequestBody MemberReqDto dto) {
        return memberService.create(dto);
    }

    /**
     * 사용자 모두 조회
     *
     * @return
     */
    @GetMapping
    public MembersDto list() {
        return memberService.list();
    }

    /**
     * 사용자 단건 조회
     *
     * @param name
     * @return
     */
    @GetMapping("/{name}")
    public MemberDto query(@PathVariable String name) {
        return memberService.query(name);
    }

    /**
     * 계좌이체
     *
     * @param dto
     * @return
     */
    @PutMapping
    public TransferResDto transfer(@RequestBody TransferReqDto dto) throws SQLException {
        return memberService.transfer(dto);
    }
}
