<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${not empty event.id}">
<input type="hidden" name="eventId" value="${event.id}"/>
</c:if>