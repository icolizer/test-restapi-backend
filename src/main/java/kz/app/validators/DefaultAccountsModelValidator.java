package kz.app.validators;

import kz.app.data.AccountModel;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.Optional;

@Singleton
public class DefaultAccountsModelValidator implements AccountsModelValidator {
    @Override
    public Optional<BrokenRule> checkRulesWithoutId(AccountModel accountModel) {
        if (accountModel.getCurrency() == null)
            return Optional.of(new NullBrokenRule("currency"));
        if (accountModel.getCurrency().length() != 3)
            return Optional.of(new LengthBrokenRule(
                    "currency", accountModel.getCurrency().length(), "3"));
        if (accountModel.getUserId() == null)
            return Optional.of(new NullBrokenRule("user id"));
        if (accountModel.getBalance() == null)
            return Optional.of(new NullBrokenRule("balance"));
        if (accountModel.getBalance().compareTo(BigDecimal.ZERO) < 0)
            return Optional.of(new NullBrokenRule("balance"));
        if (accountModel.getBalance().scale() > 2)
            return Optional.of(new BiggerScaleBrokenRule("balance", 2));
        return Optional.empty();
    }

    @Override
    public Optional<BrokenRule> checkRules(final AccountModel accountModel) {
        if (accountModel.getId() == null)
            return Optional.of(new NullBrokenRule("id"));
        return checkRulesWithoutId(accountModel);
    }
}
