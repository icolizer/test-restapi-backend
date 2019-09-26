package kz.app.services;

import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import io.micronaut.spring.tx.annotation.Transactional;
import kz.app.data.AccountModel;
import kz.app.entities.Account;
import kz.app.exceptions.BadRequestException;
import kz.app.exceptions.MethodNotAllowedException;
import kz.app.exceptions.NotFoundException;
import kz.app.factories.AccountFactory;
import kz.app.repositories.AccountsRepository;
import org.springframework.transaction.annotation.Isolation;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

@Singleton
public class DefaultAccountsService implements AccountsService {
    private final AccountsRepository accountsRepository;
    private final AccountFactory accountFactory;

    @Inject
    public DefaultAccountsService(final AccountsRepository accountsRepository,
                                  final AccountFactory accountFactory) {
        this.accountsRepository = accountsRepository;
        this.accountFactory = accountFactory;
    }

    @Override
    @Transactional
    public Account create(AccountModel accountModel) {
        Optional<Account> existingAccount
                = accountsRepository.findByUserIdAndCurrency(accountModel.getUserId(), accountModel.getCurrency());
        if (existingAccount.isPresent())
            throw new MethodNotAllowedException("Duplication constraints on user id and currency");
        Account newAccount = accountFactory.create(accountModel);
        return accountsRepository.save(newAccount);
    }

    @Override
    public List<Account> all() {
        return (List<Account>) accountsRepository.findAll();
    }

    @Override
    @Transactional(noRollbackFor = { BadRequestException.class })
    public Account update(AccountModel accountModel) {
        Account account = accountsRepository.findById(accountModel.getId())
                .orElseThrow(() -> new BadRequestException("Not found by id"));
        accountsRepository.update(account.getId(), accountModel.getBalance(),
                accountModel.getCurrency(), accountModel.getUserId());
        return accountsRepository.findById(accountModel.getId()).get();
    }

    @Override
    @Transactional(noRollbackFor = { NotFoundException.class })
    public void delete(AccountModel accountModel) {
        Account account = accountsRepository.findById(accountModel.getId())
                .orElseThrow(() -> { throw new NotFoundException(); });
        accountsRepository.delete(account);
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Optional<Account> getById(Long id) {
        return accountsRepository.findById(id);
    }

    @Override
    public List<Account> list(Integer max, Integer offset, String order) {
        switch (order.toLowerCase()) {
            case "asc":
                return accountsRepository.listOrderById(Pageable.from(offset, max));
            case "desc":
                return accountsRepository.listOrderByIdDesc(Pageable.from(offset, max));
            default:
                throw new BadRequestException("Order value is not correct");
        }
    }
}
