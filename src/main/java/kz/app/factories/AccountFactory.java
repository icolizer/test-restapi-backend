package kz.app.factories;

import kz.app.data.AccountModel;
import kz.app.entities.Account;

public interface AccountFactory {
    Account create(AccountModel accountModel);
}
