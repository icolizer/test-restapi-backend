package kz.app.validators;

import kz.app.data.TransferModel;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.Optional;

@Singleton
public class DefaultTransfersValidator implements TransfersModelValidator {
    @Override
    public Optional<BrokenRule> checkRulesWithoutId(TransferModel transferModel) {
        if (transferModel.getAccountFrom() == null || transferModel.getAccountTo() == null)
            return Optional.of(new NullBrokenRule("account"));
        if (transferModel.getAmount() == null)
            return Optional.of(new NullBrokenRule("amount"));
        if (transferModel.getRate() == null)
            return Optional.of(new NullBrokenRule("rate"));
        if (transferModel.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            return Optional.of(new LessBrokenRule("amount", "0"));
        if (transferModel.getAmount().scale() > 2)
            return Optional.of(new BiggerScaleBrokenRule("amount", 2));
        if (transferModel.getRate().compareTo(new BigDecimal(100)) >= 0)
            return Optional.of(new BiggerBrokenRule("rate", "100"));
        if (transferModel.getRate().scale() > 4)
            return Optional.of(new BiggerScaleBrokenRule("rate", 4));
        return Optional.empty();
    }
}
