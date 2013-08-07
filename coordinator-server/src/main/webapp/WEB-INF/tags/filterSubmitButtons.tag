<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%@
        taglib prefix="app" uri="/WEB-INF/www.tld" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="can" uri="/WEB-INF/permissions.tld" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form"
%>
<p>
    <button type="submit" class="btn btn-primary"><s:message code="button.filterList"/></button>
    <button type="button" class="btn" onclick="resetFilterForm(this)"><s:message code="button.resetFilter"/></button>
</p>
