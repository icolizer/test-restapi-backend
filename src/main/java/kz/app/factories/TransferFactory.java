package kz.app.factories;

import kz.app.data.TransferModel;
import kz.app.entities.Account;
import kz.app.entities.Transfer;

import java.math.BigDecimal;

public interface TransferFactory {
    Transfer create(TransferModel transferModel, Account accountFrom, Account accountTo, BigDecimal amountTo);
}
