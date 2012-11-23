package cz.clovekvtisni.coordinator.exception;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 23.11.12
 */
public class ValidationError extends MaException {

    protected ValidationError(ErrorCode code, String... params) {
        super(code, params);
    }

    public static ValidationError entityInvalid() {
        return new ValidationError(ErrorCode.VALIDATION_ERROR);
    }
}
