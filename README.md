###1. 회원 등급  
- 회원은 관리자, 매니저, 일반 3등급으로 나뉜다.
- 회사에 속하지 않은 관리자만이 회사를 생성할 수 있다.
- 관리자만이 회사의 정보를 수정할 수 있다.
- 관리자는 회사를 떠날 수 없다.
- 관리자만이 관리자의 권한을 양도할 수 있다.
- 관리자가 관리자의 권한을 양도하면 매니저가 된다.
- 관리자는 매니저의 권한을 포함한다.

- 매니저는 유저들의 권한을 변경할 수 있다. (관리자 권한 제외)
- 매니저는 일반 유저의 권한을 포함한다.

- 일반 유저는 자신의 정보를 수정하는 것 외에는(권한 제외) 조회만 할 수 있다.

- 회원이 아니더라도 회사 목록을 조회할 수 있다.

###2. 회원 가입  
- 회원은 가입할 때 회사를 선택할 수도 있고 선택하지 않을 수도 있다.
- 회사를 선택할 경우 자동으로 일반 유저가 된다.
- 회사를 선택하지 않을 경우 자동으로 관리자 유저가 된다.

###3. 회사  
- 회사는 단 한 명의 관리자를 갖는다.
- 관리자만이 회사의 정보를 수정할 수 있다.
- 관리자는 회사를 떠날 수 없다.

- 다른 회사에 합류하기 전에 반드시 회사를 떠나야 한다.
- 다른 회사에 합류하면 일반 유저가 된다.


