<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
        taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %><%@
        taglib prefix="tags" tagdir="/WEB-INF/tags"
%>
<h2><s:message code="header.eventCreate"/></h2>

<div class="eventForm">
    <sf:form method="POST" action="${root}/admin/event/edit" modelAttribute="form">
        <div>
            <sf:hidden path="id"/>
            <sf:label path="eventId"><s:message code="label.eventId"/></sf:label>
            <sf:input path="eventId"/>
        </div>

        <div>
            <sf:label path="name"><s:message code="label.name"/></sf:label>
            <sf:input path="name"/>
        </div>

        <div>
            <sf:label path="description"><s:message code="label.description"/></sf:label>
            <sf:textarea path="description" cols="72" rows="7"/>
        </div>

        <div class="buttonPanel">
            <sf:button><s:message code="button.save"/></sf:button>
        </div>
    </sf:form>
</div>

<div class="mapPanel">
    map
</div>