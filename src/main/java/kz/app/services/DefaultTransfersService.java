package kz.app.services;

import io.micronaut.data.model.Pageable;
import io.micronaut.spring.tx.annotation.Transactional;
import kz.app.data.TransferModel;
import kz.app.entities.Account;
import kz.app.entities.Transfer;
import kz.app.exceptions.BadRequestException;
import kz.app.exceptions.NotFoundException;
import kz.app.factories.TransferFactory;
import kz.app.repositories.AccountsRepository;
import kz.app.repositories.TransfersRepository;
import kz.app.utils.RateCalculator;
import org.springframework.transaction.annotation.Isolation;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class DefaultTransfersService implements TransfersService {
    private final TransfersRepository transfersRepository;
    private final AccountsRepository accountsRepository;
    private final TransferFactory transferFactory;
    private final RateCalculator rateCalculator;

    @Inject
    public DefaultTransfersService(final TransfersRepository transfersRepository,
                                   final AccountsRepository accountsRepository,
                                   final TransferFactory transferFactory,
                                   final RateCalculator rateCalculator) {
        this.transfersRepository = transfersRepository;
        this.accountsRepository = accountsRepository;
        this.transferFactory = transferFactory;
        this.rateCalculator = rateCalculator;
    }

    @Override
    @Transactional(noRollbackFor = { BadRequestException.class, NotFoundException.class })
    public Transfer create(TransferModel transferModel) {
        if (transferModel.getAccountFrom().equals( transferModel.getAccountTo()) )
            throw new BadRequestException("Accounts is the same");
        Account accountFrom =
                accountsRepository.findById(transferModel.getAccountFrom()).orElseThrow(NotFoundException::new);
        if (balanceNotEnough(accountFrom, transferModel.getAmount()))
            throw new BadRequestException(
                    String.format("Balance not enough: actual %s transfer amount %s",
                            accountFrom.getBalance(), transferModel.getAmount()));
        Account accountTo =
                accountsRepository.findById(transferModel.getAccountTo()).orElseThrow(NotFoundException::new);
        if (accountFrom.getCurrency().equals(accountTo.getCurrency()) &&
                transferModel.getRate().compareTo(BigDecimal.ONE) != 0)
            throw new BadRequestException("Incorrect rate value for same currencies");
        accountFrom.setBalance(accountFrom.getBalance().subtract(transferModel.getAmount()));
        BigDecimal amountTo = rateCalculator.calc(6, transferModel.getAmount(), transferModel.getRate());
        accountTo.setBalance(accountTo.getBalance().add(amountTo));
        Transfer transfer = transferFactory.create(transferModel, accountFrom, accountTo, amountTo);
        accountsRepository.update(accountFrom.getId(), accountFrom.getBalance());
        accountsRepository.update(accountTo.getId(), accountTo.getBalance());
        return transfersRepository.save(transfer);
    }

    @Override
    @Transactional(noRollbackFor = { NotFoundException.class })
    public void delete(TransferModel transferModel) {
        Transfer transfer = transfersRepository.findById(UUID.fromString(transferModel.getId()))
                .orElseThrow(() -> { throw new NotFoundException(); });
        transfersRepository.delete(transfer);
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Optional<Transfer> getById(String id) {
        return transfersRepository.findById(UUID.fromString(id));
    }

    @Override
    public List<Transfer> list(int limit, int offset, String order) {
        switch (order.toLowerCase()) {
            case "asc":
                return transfersRepository.listOrderByDate(Pageable.from(offset, limit));
            case "desc":
                return transfersRepository.listOrderByDateDesc(Pageable.from(offset, limit));
            default:
                throw new BadRequestException("Order value is not correct");
        }
    }

    private boolean balanceNotEnough(Account accountFrom, BigDecimal amount) {
        return accountFrom.getBalance().compareTo(amount) < 0;
    }
}
