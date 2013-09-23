<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="poi" type="cz.clovekvtisni.coordinator.server.domain.PoiEntity" %>
<h2>
<c:out value="${poi.name}"/></h2>
<p><strong><c:out value="${poi.poiCategory.name}"/>
<c:if test="${not empty poi.subCategoryId}">&gt; ${poi.poiCategory.subCategoriesMap[poi.subCategoryId].name}</c:if></strong></p>
<p><c:out value="${poi.description}"/></p>
