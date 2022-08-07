package june1.vgen.open.service;

import june1.vgen.open.common.exception.auth.IllegalChangeGradeException;
import june1.vgen.open.common.exception.client.NoSuchMemberException;
import june1.vgen.open.common.jwt.JwtUserInfo;
import june1.vgen.open.common.util.IncreaseNoUtil;
import june1.vgen.open.controller.auth.dto.MemberResDto;
import june1.vgen.open.controller.member.dto.*;
import june1.vgen.open.domain.Member;
import june1.vgen.open.domain.enumeration.Role;
import june1.vgen.open.repository.MemberRepository;
import june1.vgen.open.repository.dto.SearchMemberCond;
import june1.vgen.open.service.common.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static june1.vgen.open.common.ConstantInfo.CODE_MEMBER;
import static june1.vgen.open.common.ConstantInfo.QUERY_SIZE_LIMIT;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {

    private final static String object = "MemberService";
    private final MemberRepository memberRepository;

    /**
     * 자신이 속한 회사의 소속 직원들 목록을 조회하기
     * 모든 유저가 조회 가능..
     *
     * @param user
     * @param pageable
     * @return
     */
    public MemberListResDto list(JwtUserInfo user, Pageable pageable) {
        //해당 회사에 소속된 회원들을 모두 검색
        Page<Member> r = getMember(null, user.getCompanySeq(), pageable);

        //해당 회사의 직원들 정보를 조회하기
        //사용 중지된 회원들까지 모두 조회..
        IncreaseNoUtil no = new IncreaseNoUtil();
        List<MemberListResDto.MemberDto> l = r.getContent()
                .stream()
                .map(m -> MemberListResDto.MemberDto.builder()
                        .no(no.get())
                        .seq(m.getId())
                        .inUse(m.getInUse())
                        .memberId(m.getMemberId())
                        .role(m.getRole().name())
                        .build())
                .collect(toList());

        //응답 데이터 생성하기
        return MemberListResDto.builder()
                .pageInfo(PageInfo.by(r))
                .list(l)
                .build();
    }

    /**
     * 특정 회원 조회하기
     * 모든 유저가 조회 가능..
     *
     * @param user
     * @param seq
     * @return
     */
    public QueryMemberResDto query(JwtUserInfo user, Long seq) {
        //같은 회사 소속의 모든 회원들 중에서 특정 회원 검색
        Page<Member> r = getMember(null, user.getCompanySeq());
        List<Member> l = r.getContent()
                .stream()
                .filter(m -> m.getId().equals(seq))
                .collect(toList());

        //조회하려는 고유번호의 회원이 없다면..
        if (l.isEmpty()) {
            return QueryMemberResDto.builder().build();
        }

        //응답 데이터 생성 및 반환
        Member m = l.get(0);
        return QueryMemberResDto.builder()
                .seq(m.getId())
                .memberId(m.getMemberId())
                .memberName(m.getMemberName())
                .email(m.getEmail())
                .phoneNum(m.getPhoneNum())
                .role(m.getRole().name())
                .build();
    }

    /**
     * 자신의 정보를 수정하기
     *
     * @param user
     * @param dto
     * @return
     */
    @Transactional
    public MemberResDto modify(JwtUserInfo user, ModifyMemberReqDto dto) {
        //자신의 고유번호로 자신의 정보를 조회(권한 확인)
        //존재하지 않는 회원이면 예외 발생..
        Member m = getMember(user.getSeq(), null, true)
                .getContent()
                .get(0);

        //자신의 정보를 수정하고 저장
        m = memberRepository.save(m
                .memberName(dto.getMemberName())
                .email(dto.getEmail())
                .phoneNum(dto.getPhoneNum()));

        //자신의 고유번호를 반환
        return MemberResDto.builder()
                .seq(m.getId())
                .build();
    }

    /**
     * 관리자의 권한을 넘겨주기
     * 변경된 권한은 재로그인 후..
     *
     * @param user
     * @param seq
     * @return
     */
    @Transactional
    public MemberResDto handOver(JwtUserInfo user, Long seq) {
        //같은 회사 소속의 회원들 중에서 관리자의 권한을 넘겨줄 회원 정보 조회
        //존재하지 않는 회원이면 예외 발생..
        Member u = getMember(seq, user.getCompanySeq(), true).getContent().get(0);
        Member a = getMember(user.getSeq(), user.getCompanySeq(), true).getContent().get(0);

        //관리자의 권한을 양도..
        memberRepository.saveAll(List.of(u.changeGrade(Role.ROLE_ADMIN), a.handOver()));

        return MemberResDto.builder().seq(seq).build();
    }

    /**
     * 매니저가 일반 유저의 등급을 변경하기
     * 변경된 권한은 재로그인 후..
     *
     * @param user
     * @param dto
     * @return
     */
    @Transactional
    public ChangeGradeResDto changeGrade(JwtUserInfo user, ChangeGradeReqDto dto) {
        //관리자의 권한을 부여할 수 없다.
        if (dto.getRole().equals(Role.ROLE_ADMIN)) {
            throw IllegalChangeGradeException.builder()
                    .code(CODE_MEMBER)
                    .message("관리자 권한을 부여할 수 없습니다.")
                    .object(object)
                    .field("changeGrade().dto.role")
                    .rejectedValue(dto.getRole().name())
                    .build();
        }

        //관리자의 권한을 변경할 수 없다.
        Member m = getMember(dto.getSeq(), user.getCompanySeq(), true).getContent().get(0);
        if (m.getRole().equals(Role.ROLE_ADMIN)) {
            throw IllegalChangeGradeException.builder()
                    .code(CODE_MEMBER)
                    .message("관리자의 권한을 변경할 수 없습니다.")
                    .object(object)
                    .field("changeGrade().dto.seq")
                    .rejectedValue(dto.getSeq().toString())
                    .build();
        }

        //권한을 변경한다.
        m = memberRepository.save(m.changeGrade(dto.getRole()));

        //응답 데이터 생성 및 반환
        return ChangeGradeResDto.builder()
                .seq(m.getId())
                .memberId(m.getMemberId())
                .role(m.getRole())
                .build();
    }

    public Page<Member> getMember(Long memberSeq, Long companySeq) {
        return getMember(memberSeq, companySeq, PageRequest.of(0, QUERY_SIZE_LIMIT), false);
    }

    public Page<Member> getMember(Long memberSeq, Long companySeq, Pageable pageable) {
        return getMember(memberSeq, companySeq, pageable, false);
    }

    public Page<Member> getMember(Long memberSeq, Long companySeq, boolean e) {
        return getMember(memberSeq, companySeq, PageRequest.of(0, QUERY_SIZE_LIMIT), e);
    }

    public Page<Member> getMember(Long memberSeq, Long companySeq, Pageable pageable, boolean e) {
        //검색할 조건을 생성
        SearchMemberCond cond = SearchMemberCond.builder()
                .memberSeq(memberSeq)
                .companySeq(companySeq)
                .build();

        //회사를 검색한 후 파라미터 조건에 따라 예외 발생 선택
        Page<Member> l = memberRepository.findByCondWithCompany(cond, pageable);
        if (l.getContent().isEmpty()) {
            log.error("사용자=[{}] 회사=[{}] 조건의 회원이 존재하지 않음", memberSeq, companySeq);
            if (e) {
                throw NoSuchMemberException.builder()
                        .code(CODE_MEMBER)
                        .message("해당 회원이 존재하지 않습니다.")
                        .object(object)
                        .field("getMember().memberSeq")
                        .rejectedValue(memberSeq.toString())
                        .build();
            }
        }

        return l;
    }

}
