<%@ page contentType="text/html; charset=UTF-8" language="java" %><%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
        taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %><%@
        taglib prefix="tags" tagdir="/WEB-INF/tags"
%>
<%--@elvariable id="event" type="cz.clovekvtisni.coordinator.server.domain.EventEntity"--%>
<p class="lead"><c:out value="${event.name}"/></p>
home page admin