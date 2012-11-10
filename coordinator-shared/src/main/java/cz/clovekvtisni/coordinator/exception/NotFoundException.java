package cz.clovekvtisni.coordinator.exception;


/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/5/11
 * Time: 12:29 PM
 */
public class NotFoundException extends MaException {

    protected NotFoundException(ErrorCode code, String... params) {
        super(code, params);
    }

    public static NotFoundException idNotExist() {
        return new NotFoundException(ErrorCode.NOT_FOUND, "id not found");
    }
}
