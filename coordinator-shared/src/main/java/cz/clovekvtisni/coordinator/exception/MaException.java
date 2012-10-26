package cz.clovekvtisni.coordinator.exception;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/26/12
 * Time: 4:49 PM
 */
public class MaException extends RuntimeException {

    private static final long serialVersionUID = 8989830268218888101L;

    private String code;

    private String[] params;

    private String localizedMessage;

    protected MaException() {
    }

    protected MaException(String code, String... params) {
        this.code = code;
        this.params = params;
    }

    protected MaException(Throwable cause, String code, String... params) {
        super(cause);
        this.code = code;
        this.params = params;
    }

    public String getCode() {
        return code;
    }

    public String[] getParams() {
        return params;
    }

    @Override
    public String getMessage() {
        if (localizedMessage != null) {
            return localizedMessage;
        }
        String msg = code;
        if (params != null && params.length > 0) {
            msg += ": ";
            for (int i = 0; i < params.length; i++) {
                if (i > 1) msg += ", ";
                msg += params[i];
            }
        }
        return msg;
    }

    public static MaException internal(String description) {
        return new MaException("error.internal.ext", description);
    }

    public static MaException internal(String description, Throwable throwable) {
        return new MaException(throwable, "error.internal.ext", description);
    }

    public void setLocalizedMessage(String localizedMessage) {
        this.localizedMessage = localizedMessage;
    }

    protected static String toString(String value) {
        return value == null ? "NULL" : value;
    }

    protected static String toString(Long value) {
        return value == null ? "NULL" : Long.toString(value);
    }

    protected static String toString(Integer value) {
        return value == null ? "NULL" : Integer.toString(value);
    }

    protected static String toString(Double value) {
        return value == null ? "NULL" : Double.toString(value);
    }

    protected static String toString(Float value) {
        return value == null ? "NULL" : Float.toString(value);
    }

    protected static String toString(Object value) {
        return value == null ? "NULL" : value.toString();
    }

}
