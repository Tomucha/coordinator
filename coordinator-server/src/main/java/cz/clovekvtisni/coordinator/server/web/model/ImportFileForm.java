package cz.clovekvtisni.coordinator.server.web.model;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.InputStream;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 21.12.12
 */
public class ImportFileForm {

    @NotNull
    private Long eventId;

    @NotNull
    private String organizationId;

    private String charset;

    private InputStream cvsFile;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public InputStream getCvsFile() {
        return cvsFile;
    }

    public void setCvsFile(InputStream cvsFile) {
        this.cvsFile = cvsFile;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
}
