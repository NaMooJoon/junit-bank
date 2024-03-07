package shop.mtcoding.junitbank.temp;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.regex.Pattern;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.CsvSource;

public class RegexTest {

    @Test
    void 한글만_된다() {
        final String REGEX = "^[ㄱ-ㅎㅏ-ㅣ가-힣]+$";
        String value1 = "가나";
        String value2 = "가나12";
        boolean result1 = Pattern.matches(REGEX, value1);
        boolean result2 = Pattern.matches(REGEX, value2);


        assertThat(result1).isEqualTo(true);
        assertThat(result2).isEqualTo(false);
    }

    @Test
    void 한글은_안된다() {
        final String REGEX = "^[^ㄱ-ㅎㅏ-ㅣ가-힣]+$";
        String value1 = "";
        String value2 = "ab12";
        boolean result1 = Pattern.matches(REGEX, value1);
        boolean result2 = Pattern.matches(REGEX, value2);

        assertThat(result1).isEqualTo(false);
        assertThat(result2).isEqualTo(true);
    }

    @Test
    void 영어만_된다() {
        final String REGEX = "^[a-zA-Z]+$";
        String value1 = "ssar";
        String value2 = "ab12";
        boolean result1 = Pattern.matches(REGEX, value1);
        boolean result2 = Pattern.matches(REGEX, value2);

        assertThat(result1).isEqualTo(true);
        assertThat(result2).isEqualTo(false);
    }

    @Test
    void 영어는_안된다() {
        final String REGEX = "^[^a-zA-Z]+$";
        String value1 = "ssar";
        String value2 = "가12";
        boolean result1 = Pattern.matches(REGEX, value1);
        boolean result2 = Pattern.matches(REGEX, value2);

        assertThat(result1).isEqualTo(false);
        assertThat(result2).isEqualTo(true);
    }

    @Test
    void 영어와_숫자만_된다() {
        final String REGEX = "^[a-zA-Z0-9]+$";
        String value1 = "ssar11";
        String value2 = "가12";
        boolean result1 = Pattern.matches(REGEX, value1);
        boolean result2 = Pattern.matches(REGEX, value2);

        assertThat(result1).isEqualTo(true);
        assertThat(result2).isEqualTo(false);
    }

    @Test
    void 영어만_되고_길이는_최소2_최대4_이다() {
        final String REGEX = "^[a-zA-Z]{2,4}$";
        String value1 = "ssar";
        String value2 = "ab";
        String value3 = "absss";
        boolean result1 = Pattern.matches(REGEX, value1);
        boolean result2 = Pattern.matches(REGEX, value2);
        boolean result3 = Pattern.matches(REGEX, value3);

        assertThat(result1).isEqualTo(true);
        assertThat(result2).isEqualTo(true);
        assertThat(result3).isEqualTo(false);
    }

    @Test
    void user_username_test() {
        String username = "ssar";
        String username2 = "ssar가";
        boolean result = Pattern.matches("^[a-zA-Z0-9]{2,20}$", username);
        boolean result2 = Pattern.matches("^[a-zA-Z0-9]{2,20}$", username2);
        assertThat(result).isEqualTo(true);
        assertThat(result2).isEqualTo(false);
    }

    @Test
    void user_fullname_test() {
        String username = "ssar";
        String username2 = "ssar가";
        boolean result = Pattern.matches("^[a-zA-Z가-힣]{1,20}$", username);
        boolean result2 = Pattern.matches("^[a-zA-Z가-힣]{1,20}$", username2);
        assertThat(result).isEqualTo(true);
        assertThat(result2).isEqualTo(true);
    }

    @Test
    void user_email_test() {
        final String REGEX = "^[a-zA-Z0-9]{2,6}@[a-zA-Z0-9]{2,8}\\.[a-zA-Z]{2,3}$";
        String username = "ssar@....";
        String username2 = "ssar@naver.com";
        boolean result = Pattern.matches(REGEX, username);
        boolean result2 = Pattern.matches(REGEX, username2);
        assertThat(result).isEqualTo(false);
        assertThat(result2).isEqualTo(true);
    }

    @Test
    void account_gubun_test() {
        String gubun = "DEPOSIT";
        boolean result = Pattern.matches("^(DEPOSIT)$", gubun);
        assertThat(result).isEqualTo(true);
    }

    @Test
    void account_gubun_test2() {
        String gubun = "TRANSFER";
        boolean result = Pattern.matches("^(DEPOSIT|TRANSFER)$", gubun);
        assertThat(result).isEqualTo(true);
    }

    @Test
    void account_tel_test() {
        String tel = "01033334444";
        boolean result = Pattern.matches("^[0-9]{3}[0-9]{4}[0-9]{4}", tel);
        assertThat(result).isEqualTo(true);
    }
}
