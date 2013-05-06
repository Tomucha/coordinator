<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="can" uri="/WEB-INF/permissions.tld" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
        taglib prefix="tags" tagdir="/WEB-INF/tags" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags"

%><h2><s:message code="header.importUsers"/></h2>

<c:choose>
    <c:when test="${empty isValid or isValid}">
        <sf:form enctype="multipart/form-data" action="${root}/admin/import" modelAttribute="importFileForm">

            <sf:errors cssClass="alert alert-error" element="div" />

            <div class="importTablePanel">
                <tags:hiddenEvent/>
                <c:if test="${empty event.id}">
                    <sf:hidden path="eventId"/>
                </c:if>
                <sf:hidden path="organizationId"/>

                <p><s:message code="application.admin.importInfo"/></p>

                <input type="file" name="csvFile"/>
            </div>
            <div>
                <button class="btn btn-primary" type="submit"><s:message code="button.submit"/></button>
            </div>
        </sf:form>
    </c:when>
    <c:otherwise>
        <p class="alert alert-error"><s:message code="error.NO_ORGANIZATION_OR_EVENT_ID"/></p>
    </c:otherwise>
</c:choose>
