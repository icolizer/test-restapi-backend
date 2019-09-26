package kz.app.services;

import kz.app.data.AccountModel;
import kz.app.entities.Account;

import java.util.List;
import java.util.Optional;

public interface AccountsService {
    Account create(AccountModel accountModel);
    List<Account> all();
    Account update(AccountModel accountModel);
    void delete(AccountModel accountModel);
    Optional<Account> getById(Long id);
    List<Account> list(Integer max, Integer offset, String order);
}
