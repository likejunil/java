package june1.vgen.open.service;

import june1.vgen.open.common.exception.client.NoSuchMemberException;
import june1.vgen.open.common.exception.client.NoSuchCompanyException;
import june1.vgen.open.common.jwt.JwtUserInfo;
import june1.vgen.open.common.util.IncreaseNoUtil;
import june1.vgen.open.controller.auth.dto.MemberResDto;
import june1.vgen.open.controller.member.dto.ModifyMemberReqDto;
import june1.vgen.open.controller.member.dto.QueryMemberResDto;
import june1.vgen.open.controller.member.dto.MemberListResDto;
import june1.vgen.open.domain.Company;
import june1.vgen.open.domain.Member;
import june1.vgen.open.domain.enumeration.Role;
import june1.vgen.open.repository.CompanyRepository;
import june1.vgen.open.repository.MemberRepository;
import june1.vgen.open.service.common.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static june1.vgen.open.common.ConstantInfo.CODE_COMPANY;
import static june1.vgen.open.common.ConstantInfo.CODE_MEMBER;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {

    private final static String object = "MemberService";

    private final MemberRepository memberRepository;
    private final CompanyRepository companyRepository;
    private final RedisUserService redisUserService;

    /**
     * 관리자 직원이 회사의 소속 직원들 목록을 조회하기
     *
     * @param user
     * @param pageable
     * @return
     */
    public MemberListResDto list(JwtUserInfo user, Pageable pageable) {
        //소속 회사 정보를 구해오기
        Long seq = user.getCompanySeq();
        Company company = seq != null ? companyRepository.findById(seq).orElse(null) : null;
        if (seq == null || company == null) {
            log.error("[{}]사용자는 소속된 [{}]회사가 존재하지 않습니다.",
                    user.getUserId(), user.getCompanySeq());
            throw NoSuchCompanyException.builder()
                    .code(CODE_COMPANY)
                    .message("회사가 존재하지 않습니다.")
                    .object(object)
                    .field("list().user")
                    .rejectedValue(String.valueOf(seq))
                    .build();
        }

        //해당 회사의 직원들 정보를 조회하기
        Page<Member> r = memberRepository.findByCompany_Id(seq, pageable);
        IncreaseNoUtil no = new IncreaseNoUtil();
        List<MemberListResDto.MemberDto> list = r.getContent()
                .stream()
                .map(m -> MemberListResDto.MemberDto.builder()
                        .no(no.get())
                        .seq(m.getId())
                        .memberId(m.getMemberId())
                        .memberName(m.getMemberName())
                        .email(m.getEmail())
                        .phoneNum(m.getPhoneNum())
                        .role(m.getRole().name())
                        .build())
                .collect(toList());

        //응답 데이터 생성하기
        return MemberListResDto.builder()
                .pageInfo(PageInfo.by(r))
                .list(list)
                .build();
    }

    /**
     * 회원 조회하기
     *
     * @param user
     * @return
     */
    public QueryMemberResDto query(JwtUserInfo user) {
        //자신의 고유번호로 자신의 정보를 조회(권한 확인)
        Member m = getMember(user.getSeq());
        return QueryMemberResDto.builder()
                .seq(m.getId())
                .memberId(m.getMemberId())
                .memberName(m.getMemberName())
                .email(m.getEmail())
                .phoneNum(m.getPhoneNum())
                .role(m.getRole().name())
                .companySeq(m.getCompany() != null ? m.getCompany().getId() : null)
                .build();
    }

    /**
     * 회원 수정하기
     *
     * @param user
     * @param dto
     * @return
     */
    @Transactional
    public MemberResDto modify(JwtUserInfo user, ModifyMemberReqDto dto) {
        //자신의 고유번호로 자신의 정보를 조회(권한 확인)
        Member m = getMember(user.getSeq());

        //교체하려는 회사의 정보를 조회
        Company company = companyRepository
                .findById(dto.getCompanySeq())
                .orElse(null);

        //만약 권한이 변경된 경우 토큰을 사용 불가 상태로 변경
        if (!m.getRole().name().equals(dto.getRole().name())) {
            redisUserService.dropToken(m.getId());
        }

        //자신의 정보를 수정하고 저장
        m = memberRepository.save(m
                .memberName(dto.getMemberName())
                .email(dto.getEmail())
                .phoneNum(dto.getPhoneNum())
                .role(Role.valueOf(dto.getRole().name()))
                .company(company));

        //자신의 고유번호를 반환
        return MemberResDto.builder()
                .seq(m.getId())
                .build();
    }

    /**
     * 고유번호로 회원 조회, 존재하지 않으면 예외 발생
     */
    private Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[{}] 고유번호의 회원은 존재하지 않아..", id);
                    throw NoSuchMemberException.builder()
                            .code(CODE_MEMBER)
                            .message("그런 회원은 존재하지 않아..")
                            .object(object)
                            .field("getMember().id")
                            .rejectedValue(id.toString())
                            .build();
                });
    }
}
