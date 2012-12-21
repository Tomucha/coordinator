<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="can" uri="/WEB-INF/permissions.tld" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags"

%><h2><s:message code="header.importUsers"/></h2>

<sf:form enctype="multipart/form-data" action="${root}/admin/import" modelAttribute="importFileForm">

    <sf:errors cssClass="alert alert-error" element="div" />

    <div class="importTablePanel">
        <sf:hidden path="eventId"/>
        <sf:hidden path="organizationId"/>
        <input type="file" name="csvFile"/>
    </div>
    <div>
        <button class="btn" type="submit"><s:message code="button.submit"/></button>
    </div>
</sf:form>
