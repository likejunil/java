package june1.vgen.open.service;

import june1.vgen.open.common.exception.auth.*;
import june1.vgen.open.common.exception.client.MemberExistException;
import june1.vgen.open.common.jwt.JwtUserInfo;
import june1.vgen.open.common.jwt.TokenProvider;
import june1.vgen.open.controller.auth.dto.MemberResDto;
import june1.vgen.open.controller.auth.dto.RegisterMemberReqDto;
import june1.vgen.open.controller.auth.dto.TokenResDto;
import june1.vgen.open.domain.Company;
import june1.vgen.open.domain.Member;
import june1.vgen.open.domain.RedisRefreshToken;
import june1.vgen.open.domain.RefreshToken;
import june1.vgen.open.domain.enumeration.Role;
import june1.vgen.open.repository.CompanyRepository;
import june1.vgen.open.repository.MemberRepository;
import june1.vgen.open.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
    private final RefreshTokenRepository refreshTokenRepository;
    private final RedisService redisService;

    /**
     * << 회원 생성하기 >>
     * 1. 해당 아이디의 회원이 이미 존재하는지 확인
     * 2. 이미 존재하는 회사에 소속할 경우 해당 회사가의 정보 조회
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
        Company company = null;
        if (dto.getCompanySeq() != null) {
            company = companyRepository
                    .findById(dto.getCompanySeq())
                    .orElse(null);
        }

        //3.회원을 생성하고 저장
        Member m = memberRepository.save(Member.builder()
                .memberId(dto.getMemberId())
                .password(passwordEncoder.encode(dto.getPassword()))
                .memberName(dto.getMemberName())
                .email(dto.getEmail())
                .phoneNum(dto.getPhoneNum())
                .role(Role.valueOf(dto.getRole().name()))
                .company(company)
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
     * 4.인증 과정을 통과하면, 이미 토큰이 발급된 사용자가 아닌지 검사한다.
     * - 토큰이 존재하지 않거나 redis 서버에 접속할 수 없으면 null 반환..
     * - redis 가 다운되면 중복 로그인을 막을 수 없다.
     * 5. 토큰을 발급하여 redis 에 저장한다.
     * 6. 리프레쉬 토큰을 갱신한다.
     * 7. 토큰 데이터로 응답한다.
     *
     * @param userId
     * @param password
     * @return
     */
    @Transactional
    public TokenResDto login(String userId, String password) {

        //1.존재하는 회원인지 확인한다.
        Member member = memberRepository
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
        if (!member.getInUse()) {
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
        if (!passwordEncoder.matches(password, member.getPassword())) {
            log.error("사용자=[{}] 로그인 요청, 잘못된 패스워드=[{}]", userId, password);
            throw WrongPasswordException.builder()
                    .code(CODE_AUTH)
                    .message("잘못된 패스워드입니다.")
                    .object(object)
                    .field("login().password")
                    .rejectedValue(password)
                    .build();
        }

        //4.인증 과정을 통과하면, 이미 토큰이 발급된 사용자가 아닌지 검사한다.
        //토큰이 존재하지 않거나 redis 서버에 접속할 수 없으면 null 반환..
        //redis 가 다운되면 중복 로그인을 막을 수 없다.
        String redisToken = redisService.getToken(member.getId());
        if (StringUtils.hasText(redisToken)) {
            log.error("사용자=[{}] 중복 로그인 요청", userId);
            throw DupLoginException.builder()
                    .code(CODE_AUTH)
                    .message("중복 로그인을 시도하였습니다.")
                    .object(object)
                    .field("login().userId")
                    .rejectedValue(userId)
                    .build();
        }

        //5.토큰을 발급하고 저장한다.
        //6.리프레쉬 토큰을 갱신한다.
        return tokenProc(member);
    }

    /**
     * << 토큰 재발급 >>
     * 1. 리프레쉬 토큰을 디크립트하여 회원 검증
     * 2. 토큰에서 얻은 사용자의 고유번호로.. redis 에서 리프레쉬 토큰 조회
     * 3. dto 로 전달된 리프레쉬 토큰과 사전에 등록된 리프레쉬 토큰이 일치하지 않으면 토큰 재발급 거부
     * (사전에 발급된 리프레쉬 토큰으로만 토큰 재발급 가능)
     * 4. 토큰 생성을 위해 DB 로부터 사용자 정보를 조회
     * 5. 사용 중지된 사용자가 아닌지 확인한다.
     * 6. 토큰을 발급하여 redis 에 저장한다.(덮어쓴다.)
     * 7. 리프레쉬 토큰을 갱신한다.
     *
     * @param token : 리프레쉬 토큰
     * @return
     */
    @Transactional
    public TokenResDto reissue(String token) {

        //1.리프레쉬 토큰을 디크립트하여 회원 검증 (회원 고유번호 정보 획득)
        //토큰이 올바르지 않다면 디크립트 과정에서 예외 발생..
        JwtUserInfo user = (JwtUserInfo) tokenProvider
                .getAuthentication(token)
                .getPrincipal();

        Long seq = user.getSeq();
        if (seq == null) {
            log.error("리프레쉬 토큰에 사용자의 고유번호 정보가 누락");
            throw WrongTokenException.builder()
                    .code(CODE_AUTH)
                    .message("토큰에 필요한 정보가 누락되었습니다.")
                    .object(object)
                    .field("reissue().token")
                    .rejectedValue(token)
                    .build();
        }

        //2.redis 에서 리프레쉬 토큰 조회
        //redis 가 다운되거나 리프레쉬 토큰이 없으면 DB 에서 조회
        //어디에도 리프레쉬 토큰이 존재하지 않으면 로그인 시도..
        String refresh = redisService.getRefreshToken(seq)
                .map(RedisRefreshToken::getToken)
                .orElseGet(() -> refreshTokenRepository.findByMember_Id(seq)
                        .map(RefreshToken::getToken)
                        .orElseThrow(() -> {
                            log.error("[{}]사용자의 리프레쉬 토큰이 존재하지 않음", seq);
                            throw WrongTokenException.builder()
                                    .code(CODE_AUTH)
                                    .message("로그인을 시도하여 주십시오.")
                                    .object(object)
                                    .field("reissue().token")
                                    .rejectedValue(token)
                                    .build();
                        }));


        //3.dto 로 전달된 리프레쉬 토큰과 사전에 등록된 리프레쉬 토큰 비교
        //일치하지 않으면 토큰 재발급 거부, 로그인 유도
        if (refresh == null || !refresh.equals(token)) {
            log.error("리프레쉬 토큰이 일치하지 않습니다.");
            throw WrongTokenException.builder()
                    .code(CODE_AUTH)
                    .message("리프레쉬 토큰이 일치하지 않습니다.")
                    .object(object)
                    .field("reissue().token")
                    .rejectedValue(token)
                    .build();
        }

        //4.토큰 생성을 위해 DB 로부터 사용자 정보를 조회
        Member member = memberRepository
                .findById(seq)
                .orElseThrow(() -> {
                    log.error("사용자 고유번호=[{}] 토큰 재발급 요청, 존재하지 않는 회원", seq);
                    return NoSuchMemberException.builder()
                            .code(CODE_AUTH)
                            .message("존재하지 않는 회원입니다.")
                            .object(object)
                            .field("reissue().token")
                            .rejectedValue(token)
                            .build();
                });

        //5.사용 중지된 사용자가 아닌지 확인한다.
        if (!member.getInUse()) {
            log.error("사용자 고유번호=[{}] 토큰 재발급 요청, 비활성화된 회원", seq);
            throw InactiveMemberException.builder()
                    .code(CODE_AUTH)
                    .message("비활성화된 회원입니다.")
                    .object(object)
                    .field("reissue().token")
                    .rejectedValue(token)
                    .build();
        }

        //6.토큰을 발급하고 저장한다.
        //7.리프레쉬 토큰을 갱신한다.
        return tokenProc(member);
    }

    /**
     * << 토큰 관련 정보 삭제 >>
     * 1. redis 에서 엑세스 토큰을 지운다.
     * 2. redis 에서 리프레쉬 토큰을 지운다.
     * 3. DB 에서 리프레쉬 토큰을 지운다.
     *
     * @param user
     */
    @Transactional
    public void logout(JwtUserInfo user) {
        //해당 계정의 정보를 모두 지운다.
        Long seq = user.getSeq();
        redisService.delToken(seq);
        redisService.delRefreshToken(seq);
        refreshTokenRepository.deleteByMember_Id(seq);
    }

    private TokenResDto tokenProc(Member member) {
        //토큰 생성
        TokenResDto resDto = tokenProvider.createToken(member);
        String accessToken = resDto.getAccessToken();
        String refreshToken = resDto.getRefreshToken();

        //액세스 토큰 저장
        redisService.setToken(member.getId(), accessToken);

        //리프레쉬 토큰 엔티티 생성
        //redis 에 리프레쉬 토큰 저장
        if (redisService.setRefreshToken(RedisRefreshToken.builder()
                .id(member.getId())
                .token(refreshToken)
                .build()).isEmpty()) {

            //redis 에 리프레쉬 토큰 저장 실패하면..
            //DB 에 리프레쉬 토큰 저장
            Long id = null;
            if (member.getRefreshToken() != null) {
                id = member.getRefreshToken().getId();
            }
            refreshTokenRepository.save(RefreshToken.builder()
                    .id(id)
                    .member(member)
                    .token(resDto.getRefreshToken())
                    .build());
        }

        return resDto;
    }
}
