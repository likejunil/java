package june1.vgen.open.controller.auth;

import june1.vgen.open.common.jwt.JwtUserInfo;
import june1.vgen.open.controller.auth.dto.*;
import june1.vgen.open.controller.common.Response;
import june1.vgen.open.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

import static june1.vgen.open.common.ConstantInfo.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(URI_AUTH)
public class AuthController {

    private final AuthService authService;

    /**
     * 회원 가입
     *
     * @param dto
     * @return
     */
    @PostMapping(URI_JOIN)
    public Response<MemberResDto> register(
            @Valid @ModelAttribute RegisterMemberReqDto dto) throws IOException {

        return Response.ok(authService.register(dto));
    }

    /**
     * 로그인 (토큰 발급)
     *
     * @param dto
     * @return
     */
    @PostMapping(URI_LOGIN)
    public Response<LoginResDto> login(
            @Valid @RequestBody LoginReqDto dto) {

        return Response.ok(authService.login(dto.getUserId(), dto.getPassword()));
    }

    /**
     * 토큰 재발급 (리프레쉬 토큰 사용)
     *
     * @param dto
     * @return
     */
    @PostMapping(URI_REISSUE)
    public Response<LoginResDto> reissue(
            @Valid @RequestBody ReissueReqDto dto) {

        return Response.ok(authService.reissue(dto.getToken()));
    }

    /**
     * 로그 아웃 (해당 계정의 모든 토큰 관련 정보를 삭제)
     *
     * @param user
     * @return
     */
    @GetMapping(URI_LOGOUT)
    public Response<MemberResDto> logout(
            @AuthenticationPrincipal JwtUserInfo user) {

        return Response.ok(authService.logout(user));
    }
}
