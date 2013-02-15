<%--@elvariable id="userInEvent" type="cz.clovekvtisni.coordinator.server.domain.UserInEventEntity"--%>
<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@
        taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@
        taglib prefix="can" uri="/WEB-INF/permissions.tld" %>
<%@
        taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@
        taglib prefix="tags" tagdir="/WEB-INF/tags"
        %>
<h4><img src="${root}/images/icons/male-2.png" style="width: 1.2em;"/><c:out value="${user.userEntity.fullName}"/>
${userInEvent.userEntity.fullName}</h4>

<div class="btn-group">
    <a class="btn" href="<s:url value="${root}/admin/event/user/edit?userId=${userInEvent.userId}&eventId=${event.id}"/>"><s:message code="button.edit"/></a>
</div>
