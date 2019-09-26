package kz.app.validators;

public class BiggerBrokenRule implements BrokenRule {
    private final String fieldName;
    private final String biggerThan;

    public BiggerBrokenRule(final String fieldName, final String biggerThan) {
        this.fieldName = fieldName;
        this.biggerThan = biggerThan;
    }

    @Override
    public String getMessage() {
        return String.format("Field %s is bigger than %s", fieldName, biggerThan);
    }
}
