<%@ page contentType="text/html; charset=UTF-8" language="java" %><%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
        taglib prefix="can" uri="/WEB-INF/permissions.tld" %><%@
        taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %><%@
        taglib prefix="tags" tagdir="/WEB-INF/tags"
        %>
<c:choose>
    <c:when test="${notExist}">
        <div class="alert alert-danger">
            <s:message code="error.msg.userByEmailNotFound"/> <a onclick="sentLostPassword();return false" class="clickable"><s:message code="label.sentLostPasswordRetry"/></a>
        </div>
    </c:when>
    <c:otherwise>
        <div class="alert alert-success">
            <s:message code="msg.lostPasswordEmailSent"/>
        </div>
    </c:otherwise>
</c:choose>