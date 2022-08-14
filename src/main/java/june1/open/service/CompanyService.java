package june1.open.service;

import june1.open.common.exception.client.CompanyExistException;
import june1.open.common.exception.client.DenyAdminExistException;
import june1.open.common.exception.client.NoSuchCompanyException;
import june1.open.common.jwt.JwtUserInfo;
import june1.open.common.util.IncreaseNoUtil;
import june1.open.controller.company.dto.*;
import june1.open.domain.Company;
import june1.open.domain.Member;
import june1.open.domain.enumeration.Role;
import june1.open.domain.redis.RedisUser;
import june1.open.repository.CompanyRepository;
import june1.open.repository.MemberRepository;
import june1.open.repository.dto.SearchCompanyCond;
import june1.open.service.common.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static june1.open.common.ConstantInfo.*;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CompanyService {

    private final static String object = "CompanyService";
    private final MemberService memberService;
    private final RedisUserService redisUserService;
    private final CompanyRepository companyRepository;
    private final MemberRepository memberRepository;

    /**
     * 회사 목록을 반환
     * 누구나 조회 가능..
     *
     * @param pageable
     * @return
     */
    public CompanyListResDto list(CompanyListReqDto dto, Pageable pageable) {
        //회사 목록 전체 조회
        Page<Company> ret = getCompany(null, null, dto.getName(), pageable);

        //DTO 생성
        IncreaseNoUtil no = new IncreaseNoUtil();
        List<CompanyListResDto.CompanyDto> list = ret
                .getContent()
                .stream()
                .map(m -> CompanyListResDto.CompanyDto.builder()
                        .no(no.get())
                        .seq(m.getId())
                        .companyType(m.getCompanyType())
                        .companyName(m.getCompanyName())
                        .build())
                .collect(toList());

        //응답 데이터 생성 및 반환
        return CompanyListResDto.builder()
                .pageInfo(PageInfo.by(ret))
                .list(list)
                .build();
    }

    /**
     * 회사 상세 정보를 조회하기
     * 누구나 조회할 수 있음..
     *
     * @param seq
     * @return
     */
    public QueryCompanyResDto query(Long seq) {
        //회사 조회하기
        //조회하려는 회사가 존재하지 않을 경우 예외 발생..
        Company c = getCompany(seq, true).getContent().get(0);

        //응답 데이터 생성
        return QueryCompanyResDto.builder()
                .companyType(c.getCompanyType())
                .regiNum(c.getRegiNum())
                .companyName(c.getCompanyName())
                .ceoName(c.getCeoName())
                .email(c.getEmail())
                .contactNum(c.getContactNum())
                .zipCode(c.getZipCode())
                .address(c.getAddress())
                .addressDetail(c.getAddressDetail())
                .build();
    }

    /**
     * 회사 등록하기
     * 회사에 소속되지 않은 관리자만이 회사를 생성할 수 있음..
     *
     * @param dto
     * @return
     */
    @Transactional
    public CompanyResDto register(JwtUserInfo user, RegisterCompanyReqDto dto) {
        //이미 회사에 소속되어 있는지 확인..
        Member m = memberService
                .getMember(user.getSeq(), null, true)
                .getContent()
                .get(0);

        if (m.getCompany() != null) {
            log.error("[{}]사용자는 이미 [{}]회사를 소유하고 있음",
                    user.getUserId(), m.getCompany().getCompanyName());
            throw CompanyExistException.builder()
                    .code(CODE_COMPANY)
                    .message("사용자는 이미 회사에 등록되어 있습니다.")
                    .object(object)
                    .field("register().user.companySeq")
                    .rejectedValue(user.getCompanySeq().toString())
                    .build();
        }

        //해당 회사의 유니크 속성인 회사 등록번호 중복 여부 검사
        companyRepository.findByRegiNum(dto.getRegiNum())
                .ifPresent(c -> {
                    log.error("회사 등록번호[{}]는 이미 [{}]에서 사용하고 있음 ",
                            c.getRegiNum(), c.getCompanyName());
                    throw CompanyExistException.builder()
                            .code(CODE_COMPANY)
                            .message("이미 사용 중인 회사 등록번호 입니다.")
                            .object(object)
                            .field("register().dto.regiNum")
                            .rejectedValue(dto.getRegiNum())
                            .build();
                });

        //회사 생성 및 저장
        Company c = companyRepository.save(Company.builder()
                .companyType(dto.getCompanyType())
                .regiNum(dto.getRegiNum())
                .companyName(dto.getCompanyName())
                .ceoName(dto.getCeoName())
                .email(dto.getEmail())
                .contactNum(dto.getContactNum())
                .zipCode(dto.getZipCode())
                .address(dto.getAddress())
                .addressDetail(dto.getAddressDetail())
                .build());

        //관리자를 회사에 등록
        m = memberRepository.save(m.company(c).role(Role.ROLE_ADMIN));

        //redis 서버에 등록
        redisUserService.changeCompany(m, c);

        //응답 데이터 생성, 회사 고유번호 반환
        return CompanyResDto.builder()
                .companySeq(c.getId())
                .build();
    }

    /**
     * 회사 정보 수정하기
     * 관리자만이 자신의 회사 정보를 수정할 수 있음..
     *
     * @param dto
     * @return
     */
    @Transactional
    public CompanyResDto modify(JwtUserInfo user, ModifyCompanyReqDto dto) {
        //관리자가 소유한 회사가 존재하지 않음..
        if (user.getCompanySeq() == null) {
            log.error("[{}]사용자는 회사를 소유하고 있지 않음", user.getUserId());
            throw NoSuchCompanyException.builder()
                    .code(CODE_COMPANY)
                    .message("소속된 회사가 존재하지 않습니다.")
                    .object(object)
                    .field("modify().user.companySeq")
                    .rejectedValue(String.valueOf(user.getCompanySeq()))
                    .build();
        }

        //변경할 회사를 조회
        Company c = memberService
                .getMember(user.getSeq(), user.getCompanySeq(), true)
                .getContent()
                .get(0)
                .getCompany();

        //회사 정보를 수정하여 저장
        c = companyRepository.save(c
                .companyName(dto.getCompanyName())
                .ceoName(dto.getCeoName())
                .email(dto.getEmail())
                .contactNum(dto.getContactNum())
                .zipCode(dto.getZipCode())
                .address(dto.getAddress())
                .addressDetail(dto.getAddressDetail()));

        //응답 데이터 생성, 회사 고유번호 반환
        return CompanyResDto.builder()
                .companySeq(c.getId())
                .build();
    }

    /**
     * 회사에 가입한다.
     *
     * @param user
     * @param seq
     * @return
     */
    @Transactional
    public CompanyResDto join(JwtUserInfo user, Long seq) {
        //이미 가입되어 있는 회사에 가입하려는 경우..
        if (user.getCompanySeq() != null) {
            if (user.getCompanySeq().equals(seq)) {
                return CompanyResDto.builder()
                        .companySeq(user.getCompanySeq())
                        .build();
            }
        }

        //이미 가입되어 있는 회사가 있는지 확인
        Member m = memberService
                .getMember(user.getSeq(), null, true)
                .getContent()
                .get(0);

        if (m.getCompany() != null) {
            log.error("[{}]사용자는 이미 [{}]회사에 가입되어 있음",
                    user.getUserId(), user.getCompanySeq());
            throw CompanyExistException.builder()
                    .code(CODE_COMPANY)
                    .message("이미 소속된 회사가 존재합니다.")
                    .object(object)
                    .field("join().user.companySeq")
                    .rejectedValue(user.getCompanySeq().toString())
                    .build();
        }

        //가입할 회사를 조회하고..
        //회사에 가입한다.(일반 유저)
        Company c = getCompany(seq, true).getContent().get(0);
        m = memberRepository.save(m.company(c).changeGrade(Role.ROLE_USER));

        //소속된 회사 정보가 변경되었으므로 redis 서버에 반영한다.
        RedisUser u = redisUserService.changeCompany(m, c);

        //응답 데이터를 생성하고 반환한다.
        return CompanyResDto.builder()
                .companySeq(u.getCompanySeq())
                .build();
    }

    /**
     * 회사에서 탈퇴한다.
     *
     * @param user
     * @return
     */
    @Transactional
    public CompanyResDto resign(JwtUserInfo user) {
        //소속된 회사가 존재하는지 확인
        Member m = memberService
                .getMember(user.getSeq(), null, true)
                .getContent()
                .get(0);

        Company c = m.getCompany();
        if (c == null) {
            log.error("[{}]사용자는 소속된 회사가 존재하지 않음", user.getUserId());
            throw CompanyExistException.builder()
                    .code(CODE_COMPANY)
                    .message("탈퇴할 회사가 존재하지 않습니다.")
                    .object(object)
                    .field("resign().user.seq")
                    .rejectedValue(user.getSeq().toString())
                    .build();
        }

        //관리자는 회사를 사임할 수 없다.
        if (m.getRole().equals(Role.ROLE_ADMIN)) {
            log.error("[{}]사용자는 관리자이므로 탈퇴할 수 없음", user.getUserId());
            throw DenyAdminExistException.builder()
                    .code(CODE_MEMBER)
                    .message("관리자는 탈퇴할 수 없습니다.")
                    .object(object)
                    .field("resign().user.seq")
                    .rejectedValue(user.getSeq().toString())
                    .build();
        }

        //회사를 사임하고 USER 권한으로 변경
        m = memberRepository.save(m.resign());

        //소속된 회사 정보가 변경되었으므로 redis 서버에 반영한다.
        RedisUser u = redisUserService.changeCompany(m, null);

        //응답 데이터 생성 및 반환
        return CompanyResDto.builder()
                .companySeq(u.getCompanySeq())
                .build();
    }

    public Page<Company> getCompany(Long companySeq) {
        return getCompany(companySeq, false);
    }

    public Page<Company> getCompany(Long companySeq, Long memberSeq) {
        return getCompany(companySeq, memberSeq, false);
    }

    public Page<Company> getCompany(Long companySeq, Long memberSeq, String regexCompanyName) {
        return getCompany(companySeq, memberSeq, regexCompanyName, false);
    }

    public Page<Company> getCompany(Long companySeq, Long memberSeq, String regexCompanyName, Pageable pageable) {
        return getCompany(companySeq, memberSeq, regexCompanyName, pageable, false);
    }

    public Page<Company> getCompany(Long companySeq, boolean e) {
        return getCompany(companySeq, null, e);
    }

    public Page<Company> getCompany(Long companySeq, Long memberSeq, boolean e) {
        return getCompany(companySeq, memberSeq, null, e);
    }

    public Page<Company> getCompany(Long companySeq, Long memberSeq, String regexCompanyName, boolean e) {
        return getCompany(companySeq, memberSeq, regexCompanyName, PageRequest.of(0, QUERY_SIZE_LIMIT), e);
    }

    public Page<Company> getCompany(
            Long companySeq, Long memberSeq, String regexCompanyName, Pageable pageable, boolean e) {
        //검색할 조건을 생성
        SearchCompanyCond cond = SearchCompanyCond.builder()
                .memberSeq(memberSeq)
                .companySeq(companySeq)
                .regexCompanyName(regexCompanyName)
                .build();

        //회사를 검색한 후 회원 조건 필터링
        Page<Company> l = companyRepository.findByCond(cond, pageable);

        //파라미터 조건에 따라 예외 발생 선택
        if (l.getContent().isEmpty()) {
            log.error("사용자=[{}] 회사=[{}] 조건의 회사가 존재하지 않음", memberSeq, companySeq);
            if (e) {
                throw NoSuchCompanyException.builder()
                        .code(CODE_COMPANY)
                        .message("해당 회사가 존재하지 않습니다.")
                        .object(object)
                        .field("getCompany().companySeq")
                        .rejectedValue(companySeq.toString())
                        .build();
            }
        }

        return l;
    }
}
