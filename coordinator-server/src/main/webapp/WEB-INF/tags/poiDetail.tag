<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="poi" type="cz.clovekvtisni.coordinator.server.domain.PoiEntity" %>
<h2><c:out value="${poi.name}"/></h2>
<p><c:out value="${poi.description}"/></p>
