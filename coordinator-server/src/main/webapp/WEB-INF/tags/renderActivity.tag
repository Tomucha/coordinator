<%@
        attribute name="activity" type="cz.clovekvtisni.coordinator.server.domain.ActivityEntity" %><%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags"
        %>
<a href="<s:url value="${root}/admin/event/poi/edit?eventId=${activity.poiEntity.eventId}&poiId=${activity.poiEntity.id}"/>">${activity.poiEntity.name}</a>
${activity.type}
<a href="<s:url value="${root}/admin/event/user/edit?eventId=${activity.poiEntity.eventId}&userId=${activity.userEntity.id}"/>">${activity.userEntity.fullName}</a>
