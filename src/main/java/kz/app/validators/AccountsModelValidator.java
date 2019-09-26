package kz.app.validators;

import kz.app.data.AccountModel;

import java.util.Optional;

public interface AccountsModelValidator {
    Optional<BrokenRule> checkRulesWithoutId(AccountModel accountModel);
    Optional<BrokenRule> checkRules(AccountModel accountModel);
}
