<%@
        attribute name="field" required="true" %><%@
        attribute name="modelAttribute" required="false" %><%@
        attribute name="captionCode" required="false" %><%@
        attribute name="caption" required="false" %><%@
        attribute name="styleClass" required="false" %><%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form"
%><div class="input <c:out value="${styleClass}"/>">
    <label for="${field}">
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
    <div class="input-field">
        <jsp:doBody />
        <sf:errors path="${field}" delimiter="; " cssClass="error field-error" />
    </div>
    <div class="clear" ></div>
</div>