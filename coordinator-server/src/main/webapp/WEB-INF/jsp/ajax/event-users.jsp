<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@
        taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@
        taglib prefix="can" uri="/WEB-INF/permissions.tld" %>
<%@
        taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@
        taglib prefix="tags" tagdir="/WEB-INF/tags"
        %>
<%--
<script type="text/javascript">
    function onDataSubmit() {
        switch ($("#inputSelectedAction").val()) {
            case "SUSPEND":
                $('#suspendReasonModal').modal({});
                return false;

            case "ADD_TO_GROUP":
                $('#addToUserGroupModal').modal({});
                return false;

            default:
                return true;
        }
    }
</script>
<h2><s:message code="header.userList"/></h2>

&lt;%&ndash;@elvariable id="config" type="cz.clovekvtisni.coordinator.server.domain.CoordinatorConfig"&ndash;%&gt;

<div class="mainPanel">
    <div class="btn-toolbar">
        <c:choose>
            <c:when test="${can:hasRole('BACKEND')}">

                <button accesskey="f" class="btn" onclick="\$('#searchFormPanel').show();"><s:message code="button.filterList"/> <span class="caret"></span></button>

                <a class="btn" href="<s:url value="/admin/event/user/edit?eventId=${params.eventId}"/>"><s:message
                        code="button.addNewUser"/></a>
                <a class="btn" href="<s:url value="/admin/event/user-group/edit?eventId=${params.eventId}"/>"><s:message
                        code="button.addNewUserGroup"/></a>

                <c:if test="${!empty loggedUser.organizationId}">
                    <a class="btn"
                       href="<s:url value="/admin/import?eventId=${params.eventId}&organizationId=${loggedUser.organizationId}"/>"><s:message
                            code="button.import"/></a>
                </c:if>


            </c:when>
        </c:choose>

    </div>
--%>

    <c:choose>
        <c:when test="${!empty userInEvents}">
                <div class="eventListTable">
                    <tags:hiddenEvent/>
                    <table class="table table-striped">
                        <thead>
                        <tr>
                            <tags:eventUserList renderHeader="true"/>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${userInEvents}" var="userInEvent" begin="0" step="1" varStatus="i">
                            <tr onclick="

                            \$('#assignedUsers').load('${root}/admin/event/user/assigned?eventId=${event.id}&poiId='+\$('#cwInputPlaceId').val()+'&ajax=true&delete=false&userId='+${userInEvent.userId});

                            ">
                                <tags:eventUserList renderHeader="false" user="${userInEvent}"/>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
        </c:when>
        <c:otherwise>
            <p><s:message code="msg.noUsersFound"/></p>
        </c:otherwise>

    </c:choose>
