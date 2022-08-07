package june1.vgen.open.service;

import june1.vgen.open.common.exception.auth.*;
import june1.vgen.open.common.exception.client.MemberExistException;
import june1.vgen.open.common.exception.client.NoSuchMemberException;
import june1.vgen.open.common.jwt.JwtUserInfo;
import june1.vgen.open.common.jwt.Token;
import june1.vgen.open.common.jwt.TokenProvider;
import june1.vgen.open.controller.auth.dto.LoginResDto;
import june1.vgen.open.controller.auth.dto.MemberResDto;
import june1.vgen.open.controller.auth.dto.RegisterMemberReqDto;
import june1.vgen.open.domain.Company;
import june1.vgen.open.domain.Member;
import june1.vgen.open.domain.RedisToken;
import june1.vgen.open.domain.RedisUser;
import june1.vgen.open.domain.enumeration.Role;
import june1.vgen.open.repository.CompanyRepository;
import june1.vgen.open.repository.MemberRepository;
import june1.vgen.open.repository.RedisTokenRepository;
import june1.vgen.open.repository.RedisUserRepository;
import june1.vgen.open.service.dto.TokenDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static june1.vgen.open.common.ConstantInfo.CODE_AUTH;
import static june1.vgen.open.common.ConstantInfo.CODE_MEMBER;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuthService {

    private final static String object = "AuthService";

    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final CompanyRepository companyRepository;
    private final RedisUserRepository redisUserRepository;
    private final RedisTokenRepository redisTokenRepository;

    /**
     * << 회원 생성하기 >>
     * 1. 해당 아이디의 회원이 이미 존재하는지 확인
     * 2. 이미 존재하는 회사에 소속할 경우 해당 회사가의 정보 조회
     * 3. 회원의 권한 설정 (소속 회사가 없는 경우 관리자, 소속 회사가 있는 경우 일반 유저)
     * 3. 회원을 생성하고 저장
     * 4. 저장된 회원의 고유번호를 반환
     *
     * @param dto
     * @return
     */
    @Transactional
    public MemberResDto register(RegisterMemberReqDto dto) {
        //1.해당 아이디의 회원이 이미 존재하는지 확인
        memberRepository.findByMemberId(dto.getMemberId())
                .ifPresent(m -> {
                    log.error("[{}] 아이디 사용자는 이미 존재합니다.", dto.getMemberId());
                    throw MemberExistException.builder()
                            .code(CODE_MEMBER)
                            .message("해당 아이디는 이미 사용 중입니다.")
                            .object(object)
                            .field("register().dto")
                            .rejectedValue(dto.getMemberId())
                            .build();
                });

        //2.이미 존재하는 회사에 소속할 경우 해당 회사의 정보 조회
        Company c = null;
        if (dto.getCompanySeq() != null) {
            c = companyRepository
                    .findById(dto.getCompanySeq())
                    .orElse(null);
        }

        //3.회원의 권한 설정
        //다른 회사에 소속될 경우 일반 유저
        Role role = Role.ROLE_ADMIN;
        if (c != null) role = Role.ROLE_USER;

        //4.회원을 생성하고 저장
        Member m = memberRepository.save(Member.builder()
                .memberId(dto.getMemberId())
                .password(passwordEncoder.encode(dto.getPassword()))
                .memberName(dto.getMemberName())
                .email(dto.getEmail())
                .phoneNum(dto.getPhoneNum())
                .role(role)
                .company(c)
                .inUse(true)
                .build());

        //4.저장된 회원의 고유번호를 반환
        return MemberResDto.builder()
                .seq(m.getId())
                .build();
    }

    /**
     * << 로그인: 토큰 발급 >>
     * 1. 존재하는 회원인지 확인
     * 2. 사용 중지된 사용자가 아닌지 확인한다.
     * 3. 활성화된 사용자라면 패스워드를 통해 인증 과정을 진행한다.
     * 4. 인증 과정을 통과하면, 이미 토큰이 발급된 사용자가 아닌지 검사한다.
     * 5. 토큰을 발급하여 redis 에 저장한다.
     * 6. 리프레쉬 토큰을 redis 에 저장하거나 갱신한다.
     * 7. 토큰 데이터로 응답한다.
     *
     * @param userId
     * @param password
     * @return
     */
    @Transactional
    public LoginResDto login(String userId, String password) {
        //1.존재하는 회원인지 확인한다.
        Member m = memberRepository
                .findByMemberId(userId)
                .orElseThrow(() -> {
                    log.error("사용자=[{}] 로그인 요청, 존재하지 않는 회원", userId);
                    return NoSuchMemberException.builder()
                            .code(CODE_AUTH)
                            .message("존재하지 않는 회원입니다.")
                            .object(object)
                            .field("Login().userId")
                            .rejectedValue(userId)
                            .build();
                });

        //2.중지된 사용자가 아닌지 확인한다.
        if (!m.getInUse()) {
            log.error("사용자=[{}] 로그인 요청, 비활성화된 회원", userId);
            throw InactiveMemberException.builder()
                    .code(CODE_AUTH)
                    .message("비활성화된 회원입니다.")
                    .object(object)
                    .field("login().userId")
                    .rejectedValue(userId)
                    .build();
        }

        //3.활성화된 사용자라면 패스워드를 통해 인증 과정을 진행한다.
        if (!passwordEncoder.matches(password, m.getPassword())) {
            log.error("사용자=[{}] 로그인 요청, 잘못된 패스워드=[{}]", userId, password);
            throw IllegalPasswordException.builder()
                    .code(CODE_AUTH)
                    .message("잘못된 패스워드입니다.")
                    .object(object)
                    .field("login().password")
                    .rejectedValue(password)
                    .build();
        }

        //4.인증 과정을 통과하면, 이미 토큰이 발급된 사용자가 아닌지 검사한다.
        //중복 로그인을 막는다.
        redisUserRepository
                .findById(m.getId())
                .ifPresent(m1 -> {
                    log.error("사용자=[{}] 중복 로그인 요청", m1.getUserId());
                    throw DuplicationLoginException.builder()
                            .code(CODE_AUTH)
                            .message("중복 로그인을 시도하였습니다.")
                            .object(object)
                            .field("login().userId")
                            .rejectedValue(m1.getUserId())
                            .build();
                });

        //5.토큰을 발급하고 유저정보를 생성하여 redis 에 저장한다.
        //6.리프레쉬 토큰을 redis 에 저장 혹은 갱신한다.
        TokenDto token = tokenProc(m);

        //7.응답 데이터를 생성 및 반환한다.
        return getLoginResDto(token);
    }

    /**
     * << 토큰 재발급 >>
     * 1. 리프레쉬 토큰을 디크립트하여 회원 검증
     * 2. 토큰에서 얻은 사용자의 고유번호로 redis 에서 리프레쉬 토큰 조회
     * 3. 토큰 생성을 위해 DB 로부터 사용자 정보를 조회
     * 4 .사용 중지된 사용자가 아닌지 확인한다.
     * 5. 토큰을 발급하고 유저정보를 생성하여 redis 에 저장한다.
     * 6. 리프레쉬 토큰을 redis 에 저장 혹은 갱신한다.
     * 7. 응답 데이터를 생성 및 반환한다.
     *
     * @param refresh : 리프레쉬 토큰
     * @return
     */
    @Transactional
    public LoginResDto reissue(String refresh) {
        //1.리프레쉬 토큰을 디크립트하여 회원 검증 (회원 고유번호 정보 획득)
        //토큰이 올바르지 않다면 디크립트 과정에서 서명 예외 발생..
        Long seq = tokenProvider.decryptToken(refresh);

        //2.redis 에서 리프레쉬 토큰 조회
        //리프레쉬 토큰이 존재하지 않는다면 재로그인 필요..
        redisTokenRepository
                .findById(seq)
                .map(RedisToken::getToken)
                .filter(m -> m.equals(refresh))
                .orElseThrow(() -> {
                    log.error("[{}]사용자의 리프레쉬 토큰이 존재하지 않음 (혹은 일치하지 않음)", seq);
                    throw ExpiredTokenException.builder()
                            .code(CODE_AUTH)
                            .message("로그인을 시도하여 주십시오.")
                            .object(object)
                            .field("reissue().token")
                            .rejectedValue(refresh)
                            .build();
                });

        //3.토큰 생성을 위해 DB 로부터 사용자 정보를 조회
        //토큰에 담긴 사용자 고유번호로 사용자 조회 실패 (삭제된 유저)
        //재로그인을 유도하기 위해 IllegalTokenException 예외를 사용..
        Member m = memberRepository
                .findById(seq)
                .orElseThrow(() -> {
                    log.error("[{}]사용자는 삭제되었음", seq);
                    return IllegalTokenException.builder()
                            .code(CODE_AUTH)
                            .message("해당 사용자는 삭제되었습니다.")
                            .object(object)
                            .field("reissue().token")
                            .rejectedValue(refresh)
                            .build();
                });

        //4.사용 중지된 사용자가 아닌지 확인한다.
        if (!m.getInUse()) {
            log.error("[{}]사용자는 비활성화된 회원", seq);
            throw InactiveMemberException.builder()
                    .code(CODE_AUTH)
                    .message("활동이 정지된 회원입니다.")
                    .object(object)
                    .field("reissue().token")
                    .rejectedValue(refresh)
                    .build();
        }

        //5.토큰을 발급하고 유저정보를 생성하여 redis 에 저장한다.
        //6.리프레쉬 토큰을 redis 에 저장 혹은 갱신한다.
        TokenDto token = tokenProc(m);

        //7.응답 데이터를 생성 및 반환한다.
        return getLoginResDto(token);
    }

    /**
     * << 토큰 관련 정보 삭제 >>
     * 1. redis 에서 엑세스 토큰을 지운다.
     * 2. redis 에서 리프레쉬 토큰을 지운다.
     *
     * @param user
     */
    @Transactional
    public void logout(JwtUserInfo user) {
        //해당 계정의 정보를 모두 지운다.
        Long seq = user.getSeq();
        redisUserRepository.deleteById(seq);
        redisTokenRepository.deleteById(seq);
    }

    private TokenDto tokenProc(Member m) {
        //토큰 생성
        Token token = tokenProvider.createToken(m);
        String access = token.getAccessToken();
        String refresh = token.getRefreshToken();

        //redis 에 유저정보 및 토큰 저장
        Company c = m.getCompany();
        redisUserRepository.save(RedisUser.builder()
                .id(m.getId())
                .userId(m.getMemberId())
                .email(m.getEmail())
                .role(m.getRole())
                .accessToken(access)
                .companySeq(c != null ? c.getId() : null)
                .companyType(c != null ? c.getCompanyType() : null)
                .build());

        //redis 에 리프레쉬 토큰 저장
        redisTokenRepository.save(RedisToken.builder()
                .id(m.getId())
                .token(refresh)
                .build());

        //응답 데이터 생성
        return TokenDto.builder()
                .member(m)
                .accessToken(access)
                .refreshToken(refresh)
                .build();
    }

    private LoginResDto getLoginResDto(TokenDto token) {
        //토큰 발급 응답 데이터 생성
        Company c = token.getMember().getCompany();
        return LoginResDto.builder()
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .role(token.getMember().getRole())
                .companyType(c != null ? c.getCompanyType() : null)
                .build();
    }
}
