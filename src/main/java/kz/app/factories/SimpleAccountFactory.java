package kz.app.factories;

import kz.app.data.AccountModel;
import kz.app.entities.Account;

import javax.inject.Singleton;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Singleton
public class SimpleAccountFactory implements AccountFactory {
    @Override
    public Account create(AccountModel accountModel) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        Account account = new Account();
        account.setCurrency(accountModel.getCurrency());
        account.setUserId(accountModel.getUserId());
        account.setCreationDate(now);
        account.setUpdateDate(now);
        account.setBalance(accountModel.getBalance());
        return account;
    }
}
