package cz.clovekvtisni.coordinator.exception;


/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/5/11
 * Time: 12:29 PM
 */
public class MaPermissionDeniedException extends MaException {

    private static final long serialVersionUID = -2810516566719980648L;

    protected MaPermissionDeniedException(ErrorCode code, String... params) {
        super(code, params);
    }


    public static MaPermissionDeniedException wrongSignature(String signature, String tag) {
        return new MaPermissionDeniedException(ErrorCode.WRONG_SIGNATURE, signature, tag);
    }

    public static MaPermissionDeniedException wrongCredentials() {
        return new MaPermissionDeniedException(ErrorCode.WRONG_CREDENTIALS);
    }
}
