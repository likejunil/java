package june1.vgen.open;

import june1.vgen.open.domain.enumeration.Role;
import org.junit.jupiter.api.Test;

public class Playground {

    @Test
    void 삼항연사자가_연산을_낭비하는지_확인() {
        boolean b = false;
        String ret = b == true ? getTrue() : getFalse();
        System.out.println(ret);
    }

    private String getTrue() {
        System.out.println("진실을 얻는 과정..");
        return "<<< 진실 >>>";
    }

    private String getFalse() {
        System.out.println("거짓을 얻는 과정..");
        return "<<< 거짓 >>>";
    }

    @Test
    void Enum_을_DTO_에서_사용하기() {
        Role role = Role.ROLE_ADMIN;
        Role[] values = role.values();
        for (Role r : values) {
            System.out.println("1. " + r.name());
            System.out.println("2. " + r.getValue());
        }
    }
}
