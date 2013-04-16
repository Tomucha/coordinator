<%@ attribute name="latitude" %>
<%@ attribute name="longitude" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>

<button class="btn pull-right" onclick='CoordinatorMap.goTo(${longitude}, ${latitude});'>
    <s:message code="label.find"/> <span class="icon-arrow-right "></span>
</button>
