package june1.vgen.open.controller.company;

import june1.vgen.open.common.jwt.JwtUserInfo;
import june1.vgen.open.controller.common.Response;
import june1.vgen.open.controller.company.dto.*;
import june1.vgen.open.service.CompanyService;
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
@RequestMapping("/company")
public class CompanyController {

    private final CompanyService companyService;

    /**
     * 회사 목록 조회하기
     * 누구나 조회할 수 있음..
     *
     * @param pageable
     * @return
     */
    @GetMapping
    public Response<CompanyListResDto> list(
            @PageableDefault(page = 0, size = 100, sort = {"id"}, direction = DESC) Pageable pageable) {

        return Response.ok(companyService.list(pageable));
    }

    /**
     * 회사의 상세 정보를 조회하기
     * 누구나 조회할 수 있음..
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Response<QueryCompanyResDto> query(
            @PathVariable Long id) {

        return Response.ok(companyService.query(id));
    }

    /**
     * 회사를 생성하기
     * 관리자만이 회사를 생성할 수 있음..
     *
     * @param user
     * @param dto
     * @return
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response<CompanyResDto> register(
            @AuthenticationPrincipal JwtUserInfo user,
            @Valid @RequestBody RegisterCompanyReqDto dto) {

        return Response.ok(companyService.register(user, dto));
    }

    /**
     * 회사 정보 수정
     * 관리자만이 회사 정보를 수정할 수 있음..
     *
     * @param user
     * @param dto
     * @return
     */
    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response<CompanyResDto> modify(
            @AuthenticationPrincipal JwtUserInfo user,
            @Valid @RequestBody ModifyCompanyReqDto dto) {

        return Response.ok(companyService.modify(user, dto));
    }

    /**
     * 회사에 입사한다.
     * 아무런 회사에 가입되지 않은 사람만 가능하다.
     *
     * @param user
     * @param id
     * @return
     */
    @PostMapping("/{id}")
    public Response<CompanyResDto> join(
            @AuthenticationPrincipal JwtUserInfo user,
            @PathVariable Long id) {

        return Response.ok(companyService.join(user, id));
    }

    /**
     * 회사를 사임한다.
     * 회사에 소속된 회원만 가능하다.
     *
     * @param user
     * @return
     */
    @DeleteMapping("/member")
    public Response<CompanyResDto> resign(
            @AuthenticationPrincipal JwtUserInfo user) {

        return Response.ok(companyService.resign(user));
    }
}
