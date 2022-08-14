package june1.open.common.util;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static june1.open.common.ConstantInfo.PASSWORD_MAX_LENGTH;
import static june1.open.common.ConstantInfo.PASSWORD_MIN_LENGTH;

@Slf4j
public class PasswordUtil {

    private static final char[] s0 = new char[]{'!', '@', '#', '$', '%', '^', '&', '*', '(', ')'};
    private static final char[] s1 = new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
    private static final char[] s2 = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    private static final char[] s3 = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    public static String generatePassword() {
        return generatePassword(PASSWORD_MIN_LENGTH, PASSWORD_MAX_LENGTH);
    }

    public static String generatePassword(int min, int max) {
        List<char[]> list = List.of(s0, s1, s2, s3);

        if (min < 1 || min > max)
            return null;

        int len = min + (int) (Math.random() * (max - min));
        StringBuilder sb;
        String password;

        do {
            sb = new StringBuilder(len);
            for (int i = 0; i < len; i++) {
                char[] s = list.get((int) (Math.random() * list.size()));
                int index = (int) (s.length * Math.random());
                sb.append(s[index]);
            }
            password = sb.toString();
        } while (!validatePassword(password));

        return password;
    }

    public static boolean validatePassword(String password) {
        //1. 최소 길이 최대 길이 조건 검사
        if (password.length() < PASSWORD_MIN_LENGTH
                || password.length() > PASSWORD_MAX_LENGTH) {
            log.error("패스워드 길이를 확인하여 주십시오.[{}]", password.length());
            return false;
        }

        //2. 특수문자 포함 여부 검사
        int i;
        for (i = 0; i < s0.length; i++) {
            if (password.contains(String.valueOf(s0[i]))) {
                break;
            }
        }
        if (i == s0.length) {
            log.error("패스워드에 특수문자를 포함하여 주십시오");
            return false;
        }

        //3. 숫자 포함 여부 검사
        if (!password.matches("(.*)[0-9]+(.*)")) {
            log.error("패스워드에 숫자를 포함하여 주십시오");
            return false;
        }

        //4. 문자 포함 여부 검사
        if (!password.matches("(.*)[a-zA-Z]+(.*)")) {
            log.error("패스워드에 문자를 포함하여 주십시오");
            return false;
        }

        return true;
    }
}
