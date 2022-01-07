package june1.web.service;

import june1.domain.Member;
import june1.web.dto.MemberReqDto;
import june1.web.dto.MemberResDto;
import june1.web.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberResDto saveOne(MemberReqDto memberDto) {
        Member saved = memberRepository.save(memberDto.toMember());
        Member member = memberRepository.findById(saved.getId())
                .orElseThrow(() -> {
                    log.error("{} id 의 사용자가 존재하지 않음", saved.getId());
                    return new RuntimeException("error");
                });

        return MemberResDto.fromMember(member);
    }

    public List<MemberResDto> getAllAgeGoe(Integer age) {
        return memberRepository.findAllByAgeGoe(age).stream()
                .map(m -> MemberResDto.builder()
                        .id(m.getId())
                        .name(m.getName())
                        .age(m.getAge())
                        .build())
                .collect(toList());
    }
}
