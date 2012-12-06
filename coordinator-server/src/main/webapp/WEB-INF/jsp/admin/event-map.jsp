<%@
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
    taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
    taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
    taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %><%@
    taglib prefix="tags" tagdir="/WEB-INF/tags"
%><script type="text/javascript">
    function init() {
        <c:if test="${!empty userInEventList}">
            <c:forEach items="${userInEventList}" var="userInEvent">
                <c:if test="${!empty userInEvent.id and !empty userInEvent.lastLocationLatitude and !empty userInEvent.lastLocationLongitude}">
                    CoordinatorMap.addPoint({
                        type: TYPE_USER,
                        userId: <c:out value="${userInEvent.userId}"/>,
                        name: "<c:out value="${userInEvent.userEntity.fullName}"/>",
                        longitude: <c:out value="${userInEvent.lastLocationLongitude}"/>,
                        latitude: <c:out value="${userInEvent.lastLocationLatitude}"/>
                    })
                </c:if>
            </c:forEach>
        </c:if>

        <c:if test="${!empty placeList}">
        <c:forEach items="${placeList}" var="place">
        <c:if test="${!empty place.id and !empty place.latitude and !empty place.longitude}">
        CoordinatorMap.addPoint({
            type: TYPE_POI,
            placeId: <c:out value="${place.id}" />,
            description: "<c:out value="${place.poiCategory.name}"/>",
            longitude: <c:out value="${place.longitude}"/>,
            latitude: <c:out value="${place.latitude}"/>
        })
        </c:if>
        </c:forEach>
        </c:if>
    }
</script>

<div>
    <tags:osm
            width="90%"
            height="450px"
            latitude="${event.firstEventLocation.latitude}"
            longitude="${event.firstEventLocation.longitude}"
            onLoad="init()"
            />
</div>