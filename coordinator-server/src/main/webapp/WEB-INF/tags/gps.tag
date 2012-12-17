<%@
        attribute name="longitude" required="true" type="java.lang.Double" %><%@
        attribute name="latitude" required="true" type="java.lang.Double" %><%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="app" uri="/WEB-INF/www.tld" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form"
%><app:format value="${latitude}" pattern="#.###"/>N <app:format value="${longitude}" pattern="#.###"/>E