package june1.vgen.open.controller.company;

import june1.vgen.open.common.jwt.JwtUserInfo;
import june1.vgen.open.controller.common.Response;
import june1.vgen.open.controller.company.dto.*;
import june1.vgen.open.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.data.domain.Sort.Direction.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/company")
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping
    public Response<CompanyListResDto> list(
            @PageableDefault(page = 0, size = 100, sort = {"id"}, direction = DESC) Pageable pageable) {
        return Response.ok(companyService.list(pageable));
    }

    /**
     * 회사를 생성하기
     *
     * @param dto
     * @return
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response<CompanyResDto> register(@Valid @RequestBody RegisterCompanyReqDto dto) {
        return Response.ok(companyService.register(dto));
    }

    /**
     * 회사를 조회하기
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Response<QueryCompanyResDto> query(@PathVariable Long id) {
        return Response.ok(companyService.query(id));
    }

    /**
     * 회사 정보 수정
     *
     * @param dto
     * @return
     */
    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response<CompanyResDto> modify(
            @Valid @RequestBody ModifyCompanyReqDto dto,
            @AuthenticationPrincipal JwtUserInfo user) {
        return Response.ok(companyService.modify(user, dto));
    }
}
