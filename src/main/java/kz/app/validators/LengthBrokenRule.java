package kz.app.validators;

public class LengthBrokenRule implements BrokenRule {
    private final String fieldName;
    private final int actual;
    private final String expected;

    public LengthBrokenRule(final String fieldName, final int actual, final String expected) {
        this.fieldName = fieldName;
        this.actual = actual;
        this.expected = expected;
    }

    public String getMessage() {
        return String.format("Field %s was %d but expected %s", fieldName, actual, expected);
    }
}
