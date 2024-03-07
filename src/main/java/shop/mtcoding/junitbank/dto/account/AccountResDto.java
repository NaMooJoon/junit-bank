package shop.mtcoding.junitbank.dto.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.junitbank.domain.account.Account;
import shop.mtcoding.junitbank.domain.transaction.Transaction;
import shop.mtcoding.junitbank.domain.user.User;
import shop.mtcoding.junitbank.util.CustomDateUtil;

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

    @Getter
    @Setter
    public static class AccountListResDto {
        private String fullname;
        private List<AccountDto> accountDtos = new ArrayList<>();

        public AccountListResDto(User user, List<Account> accounts) {
            this.fullname = user.getFullname();
            this.accountDtos = accounts.stream()
                    .map(AccountDto::new)
                    .collect(Collectors.toList());
        }

        @Getter
        @Setter
        public class AccountDto {
            private Long id;
            private Long number;
            private Long balance;

            public AccountDto(Account account) {
                this.id = account.getId();
                this.number = account.getNumber();
                this.balance = account.getBalance();
            }
        }
    }

    @Getter
    @Setter
    public static class AccountWithdrawResDto {
        private Long id;
        private Long number;
        private Long balance;
        private TransactionDto transaction;

        public AccountWithdrawResDto(Account account, Transaction transaction) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.balance = account.getBalance();
            this.transaction = new TransactionDto(transaction);
        }

        @Getter
        @Setter
        public class TransactionDto {
            private Long id;
            private String gubun;
            private String sender;
            private String receiver;
            private Long amount;
            private String createdAt;

            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.gubun = transaction.getGubun().getValue();
                this.sender = transaction.getSender();
                this.receiver = transaction.getReceiver();
                this.amount = transaction.getAmount();
                this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
            }
        }
    }

    @Getter
    @Setter
    public static class AccountDepositResDto {
        private Long id;
        private Long number;
        private TransactionDto transaction;

        public AccountDepositResDto(Account account, Transaction transaction) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.transaction = new TransactionDto(transaction);
        }

        @Getter
        @Setter
        public class TransactionDto {
            private Long id;
            private String gubun;
            private String sender;
            private String reciver;
            private Long amount;
            @JsonIgnore
            private Long depositAccountBalance; // 클라이언트에게 전달 x => service 단에서 테스트 용도
            private String tel;
            private String createdAt;

            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.gubun = transaction.getGubun().getValue();
                this.sender = transaction.getSender();
                this.reciver = transaction.getReceiver();
                this.amount = transaction.getAmount();
                this.depositAccountBalance = transaction.getDepositAccountBalance();
                this.tel = transaction.getTel();
                this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
            }
        }
    }
}
