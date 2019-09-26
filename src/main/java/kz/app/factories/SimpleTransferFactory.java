package kz.app.factories;

import kz.app.data.TransferModel;
import kz.app.entities.Account;
import kz.app.entities.Transfer;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Singleton
public class SimpleTransferFactory implements TransferFactory {
    public Transfer create(TransferModel transferModel, Account accountFrom, Account accountTo, BigDecimal amountTo) {
        Transfer transfer = new Transfer();
        transfer.setAccountFrom(accountFrom);
        transfer.setAccountTo(accountTo);
        transfer.setAmount(transferModel.getAmount());
        transfer.setRate(transferModel.getRate());
        transfer.setCurrencyFrom(accountFrom.getCurrency());
        transfer.setCurrencyTo(accountTo.getCurrency());
        transfer.setDate(LocalDateTime.now(ZoneOffset.UTC));
        return transfer;
    }
}
