package june1.vgen.open.controller.member;

import june1.vgen.open.common.jwt.JwtUserInfo;
import june1.vgen.open.controller.auth.dto.MemberResDto;
import june1.vgen.open.controller.common.Response;
import june1.vgen.open.controller.member.dto.*;
import june1.vgen.open.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    /**
     * 회사의 직원들 목록을 구함
     * 모든 회원들이 조회할 수 있음..
     *
     * @param pageable
     * @param user
     * @return
     */
    @GetMapping
    public Response<MemberListResDto> list(
            @PageableDefault(page = 0, size = 100, sort = {"id"}, direction = DESC) Pageable pageable,
            @AuthenticationPrincipal JwtUserInfo user) {

        return Response.ok(memberService.list(user, pageable));
    }

    /**
     * 특정 회원의 정보를 조회하기
     * 모든 회원들이 조회할 수 있음..
     *
     * @param id
     * @param user
     * @return
     */
    @GetMapping("/{id}")
    public Response<QueryMemberResDto> query(
            @PathVariable Long id,
            @AuthenticationPrincipal JwtUserInfo user) {

        return Response.ok(memberService.query(user, id));
    }

    /**
     * 자신의 정보를 수정하기
     * 등급은 수정할 수 없음..
     *
     * @param dto
     * @param user
     * @return
     */
    @PutMapping
    public Response<MemberResDto> modify(
            @Valid @RequestBody ModifyMemberReqDto dto,
            @AuthenticationPrincipal JwtUserInfo user) {

        return Response.ok(memberService.modify(user, dto));
    }

    /**
     * 관리자가 자신의 권한을 다른 회원에게 양도하기
     * 변경된 내용은 재로그인을 해야 적용됨..
     * 관리자만이 가능..
     *
     * @param id
     * @param user
     * @return
     */
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response<MemberResDto> handOver(
            @AuthenticationPrincipal JwtUserInfo user,
            @PathVariable Long id) {

        return Response.ok(memberService.handOver(user, id));
    }

    /**
     * 관리자 혹은 매니저가 관리자가 아닌 유저의 등급을 변경한다.
     * 변경된 내용은 재로그인을 해야 적용됨..
     *
     * @param user
     * @param dto
     * @return
     */
    @PutMapping("/manager")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public Response<ChangeGradeResDto> changeGrade(
            @AuthenticationPrincipal JwtUserInfo user,
            @Valid @RequestBody ChangeGradeReqDto dto) {

        return Response.ok(memberService.changeGrade(user, dto));
    }
}
