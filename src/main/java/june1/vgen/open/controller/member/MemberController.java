package june1.vgen.open.controller.member;

import june1.vgen.open.common.exception.auth.WrongAccessException;
import june1.vgen.open.common.jwt.JwtUserInfo;
import june1.vgen.open.controller.auth.dto.MemberResDto;
import june1.vgen.open.controller.common.Response;
import june1.vgen.open.controller.member.dto.MemberListResDto;
import june1.vgen.open.controller.member.dto.ModifyMemberReqDto;
import june1.vgen.open.controller.member.dto.QueryMemberResDto;
import june1.vgen.open.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static june1.vgen.open.common.ConstantInfo.AUTH;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final static String object = "MemberController";
    private final MemberService memberService;

    /**
     * 회사의 직원들 목록을 구함
     *
     * @param pageable
     * @param user
     * @return
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response<MemberListResDto> list(
            @PageableDefault(page = 0, size = 100, sort = {"id"}, direction = DESC) Pageable pageable,
            @AuthenticationPrincipal JwtUserInfo user) {

        MemberListResDto resDto = memberService.list(user, pageable);
        return Response.ok(resDto);
    }

    /**
     * 자신의 정보를 조회하기
     *
     * @param id
     * @param user
     * @return
     */
    @GetMapping("/{id}")
    public Response<QueryMemberResDto> query(
            @PathVariable Long id,
            @AuthenticationPrincipal JwtUserInfo user) {

        if (!user.getSeq().equals(id)) {
            log.error("[{}]사용자가 [{}]조회를 시도하였습니다.", user.getSeq(), id);
            throw WrongAccessException.builder()
                    .code(AUTH)
                    .message("해당 자원에 대한 접근 권한이 부족합니다.")
                    .object(object)
                    .field("query().id")
                    .rejectedValue(id.toString())
                    .build();
        }

        QueryMemberResDto resDto = memberService.query(user);
        return Response.ok(resDto);
    }

    /**
     * 자신의 정보를 수정하기
     *
     * @param dto
     * @param user
     * @return
     */
    @PutMapping
    public Response<MemberResDto> modify(
            @Valid @RequestBody ModifyMemberReqDto dto,
            @AuthenticationPrincipal JwtUserInfo user) {

        MemberResDto resDto = memberService.modify(user, dto);
        return Response.ok(resDto);
    }
}
