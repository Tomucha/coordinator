<%@
        attribute name="url" required="true" %><%@
        attribute name="labelCode" required="true" %><%@
        attribute name="urlParams" required="false" %><%@
        attribute name="visible" type="java.lang.Boolean" %><%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%@
        taglib prefix="app" uri="/WEB-INF/www.tld" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="can" uri="/WEB-INF/permissions.tld" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form"
%>
<c:if test="${empty visible or visible}">
    <li class="${requestScope['javax.servlet.forward.request_uri'] == (fn:indexOf(url, '?') == -1 ? url : fn:substring(url, 0, fn:indexOf(url, '?'))) ? 'active' : ''}">
        <a href="<s:url value="${url}?tag=1${!empty event.id ? '&eventId=' : ''}${event.id}${urlParams}"/>"><s:message code="${labelCode}"/></a>
    </li>
</c:if>