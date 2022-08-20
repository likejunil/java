package june1.db.service;

import june1.db.controller.dto.*;

public interface MemberService {

    MemberDto create(MemberReqDto dto);

    MemberDto query(String name);

    MembersDto list();

    TransferResDto transfer(TransferReqDto dto);
}
