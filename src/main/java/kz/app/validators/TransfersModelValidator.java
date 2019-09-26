package kz.app.validators;

import kz.app.data.TransferModel;

import java.util.Optional;

public interface TransfersModelValidator {
    Optional<BrokenRule> checkRulesWithoutId(TransferModel transferModel);
}
