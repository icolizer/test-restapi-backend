package kz.app.validators;

public class NullBrokenRule implements BrokenRule {
    private final String fieldName;

    public NullBrokenRule(final String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String getMessage() {
        return String.format("Field %s is NULL", fieldName);
    }
}
