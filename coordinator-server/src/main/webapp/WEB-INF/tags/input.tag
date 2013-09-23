<%@
        attribute name="field" required="true" %><%@
        attribute name="modelAttribute" required="false" %><%@
        attribute name="captionCode" required="false" %><%@
        attribute name="caption" required="false" %><%@
        attribute name="styleClass" required="false" %><%@
        attribute name="fluid" required="false" type="java.lang.Boolean" %><%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form"
%><div class="input ${fluid ? "row-fluid" : ""}<c:out value="${styleClass}"/>" <c:if test="${not empty field}">id="input-${field}"</c:if> >
    <label for="${field}" class="${fluid ? "span3" : ""}">
        <c:choose>
            <c:when test="${!empty captionCode}">
                <s:message code="${captionCode}" />
            </c:when>
            <c:when test="${!empty caption}">
                <c:out value="${caption}" />
            </c:when>
            <c:when test="${!empty modelAttribute}">
                <s:message code="${modelAttribute}.${field}" />
            </c:when>
        </c:choose>
    </label>
    <div class="input-field ${fluid ? "span7" : ""}">
        <jsp:doBody />
        <sf:errors path="${field}" delimiter="; " cssClass="alert alert-error" />
    </div>
    <div class="clear" ></div>
</div>