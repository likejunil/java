package june1.db.basic.service;

import june1.db.basic.controller.dto.*;

public interface MemberService {

    MemberDto create(MemberReqDto dto);

    MemberDto query(String name);

    MembersDto list();

    TransferResDto transfer(TransferReqDto dto);
}
