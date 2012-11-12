<%@ tag import="cz.clovekvtisni.coordinator.util.RenderTool" %><%@
        attribute name="time" required="true" type="java.util.Date" %><%@
        attribute name="now" required="true" type="java.lang.Long" %><%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form"
%><c:set var="diff" value="${((now - time.time) / 60000)}"/><c:choose>
    <c:when test="${diff < 10.0}"><s:message code="humanAgo.justNow"/></c:when>
    <c:when test="${diff < 30.0}"><s:message code="humanAgo.min" arguments="${((diff / 5) * 5)}"/></c:when>
    <c:when test="${diff < 120.0}"><s:message code="humanAgo.min" arguments="${((diff / 10) * 10)}"/></c:when>
    <c:when test="${diff < 1440.0}"><s:message code="humanAgo.hours" arguments="${(diff / 60)}"/></c:when>
    <c:otherwise><s:message code="humanAgo.days" arguments="${(diff / (24 * 60))}"/></c:otherwise>
</c:choose>
