package kz.app.helpers;

import kz.app.data.AccountModel;

import java.math.BigDecimal;

public class AccountModelHelper {
    public static AccountModel create(BigDecimal balance, String currency, long userId) {
        var accountModel = new AccountModel();
        accountModel.setBalance(balance);
        accountModel.setCurrency(currency);
        accountModel.setUserId(userId);
        return accountModel;
    }
}
