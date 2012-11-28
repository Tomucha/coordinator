package cz.clovekvtisni.coordinator.server.web.util;

import cz.clovekvtisni.coordinator.server.web.model.FilterParams;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 19.11.12
 */
public class Breadcrumb {

    private String url;

    private String labelCode;

    private String[] roles;

    private FilterParams filterParams;

    public Breadcrumb(String url, String labelCode, String... roles) {
        this.url = url;
        this.labelCode = labelCode;
        this.roles = roles;
    }

    public Breadcrumb(FilterParams params, String url, String labelCode, String... roles) {
        this.filterParams = params;
        this.url = url;
        this.labelCode = labelCode;
        this.roles = roles;
    }

    public String getUrl() {
        return url;
    }

    public String getLinkUrl() {
        String url = this.url;
        if (filterParams != null) {
            url = url + "?";
            for (Map.Entry<String, String> entry : filterParams.toMap().entrySet()) {
                try {
                    url = url + URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8") + "&";
                } catch (UnsupportedEncodingException e) {
                    LoggerFactory.getLogger(this.getClass()).error(e.getMessage());
                }
            }
        }

        return url;
    }

    public String getLabelCode() {
        return labelCode;
    }

    public List<String> isVisibleFor() {
        return roles != null ? Arrays.asList(roles) : new ArrayList<String>(0);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLabelCode(String labelCode) {
        this.labelCode = labelCode;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public FilterParams getFilterParams() {
        return filterParams;
    }

    public void setFilterParams(FilterParams filterParams) {
        this.filterParams = filterParams;
    }
}
