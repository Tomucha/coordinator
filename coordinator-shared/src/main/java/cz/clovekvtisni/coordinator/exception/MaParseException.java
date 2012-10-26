package cz.clovekvtisni.coordinator.exception;


/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/5/11
 * Time: 12:29 PM
 */
public class MaParseException extends MaException {

    protected MaParseException(ErrorCode code, String... params) {
        super(code, params);
    }

    public static MaParseException wrongRequestParams() {
        return new MaParseException(ErrorCode.PARSE_JSON, "request params");
    }
}
