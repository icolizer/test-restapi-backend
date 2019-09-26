package kz.app.helpers;

import kz.app.data.TransferModel;

import java.math.BigDecimal;

public class TransferModelHelper {
    public static TransferModel create(Long accFrom, Long accTo, BigDecimal amount, BigDecimal rate) {
        TransferModel transferModel = new TransferModel();
        transferModel.setAccountFrom(accFrom);
        transferModel.setAccountTo(accTo);
        transferModel.setAmount(amount);
        transferModel.setRate(rate);
        return transferModel;
    }
}
