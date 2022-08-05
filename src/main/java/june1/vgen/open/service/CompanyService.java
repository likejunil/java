package june1.vgen.open.service;

import june1.vgen.open.common.exception.auth.WrongAuthoritiesException;
import june1.vgen.open.common.exception.client.NoSuchCompanyException;
import june1.vgen.open.common.jwt.JwtUserInfo;
import june1.vgen.open.common.util.IncreaseNoUtil;
import june1.vgen.open.controller.company.dto.*;
import june1.vgen.open.domain.Company;
import june1.vgen.open.repository.CompanyRepository;
import june1.vgen.open.service.common.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static june1.vgen.open.common.ConstantInfo.CODE_AUTH;
import static june1.vgen.open.common.ConstantInfo.CODE_COMPANY;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CompanyService {

    private final static String object = "CompanyService";
    private final CompanyRepository companyRepository;
    private final RedisUserService redisUserService;

    /**
     * 회사 등록하기
     *
     * @param dto
     * @return
     */
    @Transactional
    public CompanyResDto register(RegisterCompanyReqDto dto) {
        //회사 생성 및 저장
        Company company = companyRepository.save(Company.builder()
                .regiNum(dto.getRegiNum())
                .companyType(dto.getCompanyType())
                .companyName(dto.getCompanyName())
                .ceoName(dto.getCeoName())
                .contactNum(dto.getContactNum())
                .zipCode(dto.getZipCode())
                .address(dto.getAddress())
                .addressDetail(dto.getAddressDetail())
                .build());

        //응답 데이터 생성, 회사 고유번호 반환
        return CompanyResDto.builder()
                .seq(company.getId())
                .build();
    }

    /**
     * 회사 조회하기
     *
     * @param id
     * @return
     */
    public QueryCompanyResDto query(Long id) {
        //회사 조회하기
        Company c = getCompany(id);

        //응답 데이터 생성
        return QueryCompanyResDto.builder()
                .regiNum(c.getRegiNum())
                .companyType(c.getCompanyType())
                .companyName(c.getCompanyName())
                .ceoName(c.getCeoName())
                .contactNum(c.getContactNum())
                .zipCode(c.getZipCode())
                .address(c.getAddress())
                .addressDetail(c.getAddressDetail())
                .build();
    }

    /**
     * 회사 정보 수정하기
     *
     * @param dto
     * @return
     */
    @Transactional
    public CompanyResDto modify(JwtUserInfo user, ModifyCompanyReqDto dto) {
        //회사 정보를 수정할 권한이 있는지 확인
        Long companySeq = user.getCompanySeq();
        if (companySeq == null || !companySeq.equals(dto.getSeq())) {
            log.error("[{}]사용자에게는 [{}]회사 정보를 수정할 권한이 없음",
                    user.getSeq(), dto.getSeq());
            throw WrongAuthoritiesException.builder()
                    .code(CODE_AUTH)
                    .message("회사 정보를 수정할 권한이 없습니다.")
                    .object(object)
                    .field("modify().dto.seq")
                    .rejectedValue(dto.getSeq().toString())
                    .build();
        }

        //해당 회사가 존재하는지 확인
        Company c = getCompany(dto.getSeq());

        //회사의 타입이 바뀌면 토큰 사용 불가 적용
        if (!c.getCompanyType().name().equals(dto.getCompanyType().name())) {
            redisUserService.dropToken(user.getSeq());
        }

        //해당 회사의 정보를 수정하고 저장
        c = companyRepository.save(c
                .companyType(dto.getCompanyType())
                .companyName(dto.getCompanyName())
                .ceoName(dto.getCeoName())
                .contactNum(dto.getContactNum())
                .zipCode(dto.getZipCode())
                .address(dto.getAddress())
                .addressDetail(dto.getAddressDetail()));

        //응답 데이터 생성, 회사 고유번호 반환
        return CompanyResDto.builder()
                .seq(c.getId())
                .build();
    }

    /**
     * 회사 목록을 반환
     *
     * @param pageable
     * @return
     */
    public CompanyListResDto list(Pageable pageable) {

        //회사 목록 전체 조회
        Page<Company> ret = companyRepository.findAll(pageable);

        //..
        IncreaseNoUtil no = new IncreaseNoUtil();
        List<CompanyListResDto.CompanyDto> list = ret
                .stream()
                .map(m -> CompanyListResDto.CompanyDto.builder()
                        .no(no.get())
                        .seq(m.getId())
                        .companyName(m.getCompanyName())
                        .companyType(m.getCompanyType())
                        .contactNum(m.getContactNum())
                        .regiNum(m.getRegiNum())
                        .ceoName(m.getCeoName())
                        .zipCode(m.getZipCode())
                        .address(m.getAddress())
                        .addressDetail(m.getAddressDetail())
                        .build())
                .collect(toList());

        //응답 데이터 생성 및 반환
        return CompanyListResDto.builder()
                .pageInfo(PageInfo.by(ret))
                .list(list)
                .build();
    }

    private Company getCompany(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[{}] 고유번호의 company 는 존재하지 않습니다.", id);
                    return NoSuchCompanyException.builder()
                            .code(CODE_COMPANY)
                            .message("해당 회사가 존재하지 않습니다.")
                            .object(object)
                            .field("getCompany().id")
                            .rejectedValue(id.toString())
                            .build();
                });
    }
}
