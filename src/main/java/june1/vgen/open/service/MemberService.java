package june1.vgen.open.service;

import june1.vgen.open.common.exception.auth.IllegalChangeGradeException;
import june1.vgen.open.common.exception.client.NoSuchCompanyException;
import june1.vgen.open.common.exception.client.NoSuchMemberException;
import june1.vgen.open.common.jwt.JwtUserInfo;
import june1.vgen.open.common.util.FileUtil;
import june1.vgen.open.common.util.IncreaseNoUtil;
import june1.vgen.open.controller.auth.dto.MemberResDto;
import june1.vgen.open.controller.member.dto.*;
import june1.vgen.open.domain.AttachmentFile;
import june1.vgen.open.domain.Member;
import june1.vgen.open.domain.RedisUser;
import june1.vgen.open.domain.enumeration.Role;
import june1.vgen.open.repository.AttachmentFileRepository;
import june1.vgen.open.repository.MemberRepository;
import june1.vgen.open.repository.RedisUserRepository;
import june1.vgen.open.repository.dto.SearchMemberCond;
import june1.vgen.open.service.common.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static june1.vgen.open.common.ConstantInfo.*;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {

    private final static String object = "MemberService";
    private final MemberRepository memberRepository;
    private final RedisUserRepository redisUserRepository;
    private final AttachmentFileRepository attachmentFileRepository;
    private final FileUtil fileUtil;

    /**
     * 자신이 속한 회사의 소속 직원들 목록을 조회하기
     * 모든 유저가 조회 가능..
     *
     * @param user
     * @param pageable
     * @return
     */
    public MemberListResDto list(JwtUserInfo user, Pageable pageable) {
        //회사에 소속되어 있는지 확인한다.
        //소속된 회사가 없다면 예외를 발생시킨다.
        checkInCompany(user);

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
     * 같은 회사의 사람만 조회가 가능..
     *
     * @param user
     * @param seq
     * @return
     */
    public QueryMemberResDto query(JwtUserInfo user, Long seq) {
        //자기 자신을 조회하는 것이 아니라며..
        //회사에 소속되어 있는지 확인한다.
        //소속된 회사가 없다면 예외를 발생시킨다.
        if (!user.getSeq().equals(seq)) {
            checkInCompany(user);
        }

        //같은 회사 소속의 모든 회원들 중에서 특정 회원 검색
        //조회하는 회원이 존재하지 않는다면 예외 발생..
        Page<Member> r = getMember(seq, user.getCompanySeq(), true);

        //응답 데이터 생성 및 반환
        Member m = r.getContent().get(0);
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
    public MemberResDto modify(JwtUserInfo user, ModifyMemberReqDto dto) throws IOException {
        //자신의 고유번호로 자신의 정보를 조회(권한 확인)
        //존재하지 않는 회원이면 예외 발생..
        Member m = getMember(user.getSeq(), null, true).getContent().get(0);

        //새로운 사진을 올렸다면.. 기존의 사진을 지우고 새로운 사진으로 대체..
        //아무것도 올리지 않았다면.. 기존의 사진을 유지..
        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            AttachmentFile f = attachmentFileRepository.save(fileUtil
                    .saveFile(dto.getImage(), MEMBER_IMAGE_FILE_PATH)
                    .toAttachmentFile());

            log.info("사용자[{}]의 파일을 새롭게 교체했습니다.[{}]=>[{}]",
                    user.getUserId(), m.getImage().getId(), f.getId());

            //이미지 교체
            AttachmentFile old = m.getImage();
            if (old != null) {
                attachmentFileRepository.save(old.delete());
            }
            m.changeImage(f);
        }

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
     *
     * @param user
     * @param seq
     * @return
     */
    @Transactional
    public MemberResDto handOver(JwtUserInfo user, Long seq) {
        //자기가 자기 자신에게 이양하는 경우
        //아무것도 하지 않는다.
        if (user.getSeq().equals(seq)) {
            MemberResDto.builder().seq(seq).build();
        }

        //회사에 소속되어 있는지 확인한다.
        //소속된 회사가 없다면 예외를 발생시킨다.
        checkInCompany(user);

        //같은 회사 소속의 회원들 중에서 관리자의 권한을 넘겨줄 회원 정보 조회
        //존재하지 않는 회원이면 예외 발생..
        Member from = getMember(user.getSeq(), user.getCompanySeq(), true).getContent().get(0);
        Member to = getMember(seq, user.getCompanySeq(), true).getContent().get(0);

        //관리자의 권한을 양도..
        memberRepository.saveAll(List.of(to.changeGrade(Role.ROLE_ADMIN), from.handOver()));

        //redis 서버 내용 변경
        handOverInRedis(to, from);

        return MemberResDto.builder().seq(seq).build();
    }

    /**
     * 매니저가 일반 유저의 등급을 변경하기
     * 반드시 회사에 등록되어 있어야만 가능..
     *
     * @param user
     * @param dto
     * @return
     */
    @Transactional
    public ChangeGradeResDto changeGrade(JwtUserInfo user, ChangeGradeReqDto dto) {
        //회사에 소속되어 있는지 확인한다.
        //소속된 회사가 없다면 예외를 발생시킨다.
        checkInCompany(user);

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

        //redis 서버 내용 변경
        changeGradeInRedis(m);

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

    private void checkInCompany(JwtUserInfo user) {
        //소속된 회사가 존재하는지 확인
        if (user.getCompanySeq() == null) {
            log.error("[{}]사용자는 소속된 회사가 존재하지 않음", user.getUserId());
            throw NoSuchCompanyException.builder()
                    .code(CODE_COMPANY)
                    .message("소속된 회사가 존재하지 않습니다.")
                    .object(object)
                    .field("checkInCompany().user")
                    .rejectedValue(String.valueOf(user.getCompanySeq()))
                    .build();
        }
    }

    private void handOverInRedis(Member to, Member from) {
        List<RedisUser> l = new ArrayList<>();
        redisUserRepository
                .findAllById(List.of(to.getId(), from.getId()))
                .forEach(m -> {
                    //관리자 권한을 받음
                    if (m.getId().equals(to.getId())) {
                        l.add(m.role(Role.ROLE_ADMIN));
                    }
                    //관리자 권한을 건네줌
                    else if (m.getId().equals(from.getId())) {
                        l.add(m.handOver());
                    }
                });

        redisUserRepository.saveAll(l);
    }

    private void changeGradeInRedis(Member m) {
        redisUserRepository
                .findById(m.getId())
                .ifPresent(m1 -> redisUserRepository
                        .save(m1.role(m.getRole())));
    }
}
