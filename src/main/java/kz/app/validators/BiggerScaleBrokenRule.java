package kz.app.validators;

public class BiggerScaleBrokenRule implements BrokenRule {
    private final String fieldName;
    private final int than;

    public BiggerScaleBrokenRule(final String fieldName, final int than) {
        this.fieldName = fieldName;
        this.than = than;
    }

    public String getMessage() {
        return String.format("Field %s scale bigger than %d", fieldName, than);
    }
}
