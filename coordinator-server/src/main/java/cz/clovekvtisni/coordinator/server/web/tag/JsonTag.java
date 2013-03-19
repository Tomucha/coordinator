package cz.clovekvtisni.coordinator.server.web.tag;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import javax.servlet.jsp.JspWriter;

@SuppressWarnings("NonSerializableFieldInSerializableClass")
public class JsonTag extends RequestContextAwareTag {

    private static final long serialVersionUID = 3998623339291692630L;

    private Object value;

    @Autowired
    private ObjectMapper jsonObjectMapper;

    @SuppressWarnings("unchecked")
    @Override
    protected int doStartTagInternal() throws Exception {

        if (jsonObjectMapper == null) {
            getRequestContext().getWebApplicationContext().getAutowireCapableBeanFactory().autowireBean(this);
        }

        JspWriter out = pageContext.getOut();
        out.write(jsonObjectMapper.writeValueAsString(value));
        return SKIP_BODY;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
