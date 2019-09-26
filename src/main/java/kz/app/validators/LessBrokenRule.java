package kz.app.validators;

public class LessBrokenRule implements BrokenRule {
    private final String fieldName;
    private final String lessThan;

    public LessBrokenRule(final String fieldName, final String lessThan) {
        this.fieldName = fieldName;
        this.lessThan = lessThan;
    }

    @Override
    public String getMessage() {
        return String.format("Field %s is less than %s", fieldName, lessThan);
    }
}
