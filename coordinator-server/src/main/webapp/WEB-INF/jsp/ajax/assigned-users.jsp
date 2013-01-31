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
<c:forEach items="${assignedUsers}" var="userInEvent" begin="0" step="1" varStatus="i">
<span
    onclick="
    \$('#assignedUsers').load('${root}/admin/event/user/assigned?eventId=${event.id}&poiId='+\$('#cwInputPlaceId').val()+'&ajax=true&delete=true&userId='+${userInEvent.userId});
    "
        >${userInEvent.userEntity.fullName} <i class="icon-remove"></i></span>
</c:forEach>

