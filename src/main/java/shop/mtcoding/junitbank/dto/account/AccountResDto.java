package shop.mtcoding.junitbank.dto.account;

import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.junitbank.domain.account.Account;

public class AccountResDto {
    @Getter
    @Setter
    public static class AccountSaveResDto {
        private Long id;
        private Long number;
        private Long balance;

        public AccountSaveResDto(Account account) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.balance = account.getBalance();
        }
    }
}
