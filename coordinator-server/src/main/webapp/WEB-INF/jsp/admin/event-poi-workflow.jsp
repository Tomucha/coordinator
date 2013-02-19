<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
        taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %><%@
        taglib prefix="tags" tagdir="/WEB-INF/tags"
%>
<%--@elvariable id="poi" type="cz.clovekvtisni.coordinator.server.domain.PoiEntity"--%>

<script type="text/javascript">

    function loadUsers() {
        $('#userList').load('${root}/admin/event/user/list?eventId=${event.id}&ajax=true&groupId='+$("#groupId").val()+"&userFulltext="+$("#userFulltext").val());
        return false;
    }

    function loadAssignedUsers() {
        $("#assignedUsers").load("${root}/admin/event/user/assigned?eventId=${event.id}&poiId="+${poi.id}+"&ajax=true");
    }

    function onUserClick(userId) {
        $('#assignedUsers').load('${root}/admin/event/user/assigned?eventId=${event.id}&poiId='+${poi.id}+'&ajax=true&delete=false&userId='+userId);
    }

    function onAssignedUserClick(userId) {
        $('#assignedUsers').load('${root}/admin/event/user/assigned?eventId=${event.id}&poiId='+${poi.id}+'&ajax=true&delete=true&userId='+userId);
    }

    $(document).ready(function() {
        loadUsers();
        loadAssignedUsers();
    });

</script>

<div class="mainPanel">
    <div class="eventForm">

        <sf:errors />

        <tags:poiDetail poi="${poi}"/>

        <h3><s:message code="label.workflowTransition"/></h3>


        <p><c:out value="${poi.workflow.name}"/></p>


        <p><c:out value="${poi.workflowState.name}"/> <span class="icon-arrow-right"></span></p>

        <div style="padding-left: 9em;">
        <c:forEach items="${poi.workflowState.transitions}" var="trans">
           <p><span class="icon-arrow-right"></span> <a class="btn" href="/admin/event/poi/workflow/transition?poiId=${poi.id}&eventId=${event.id}&transitionId=${trans.id}"><c:out value="${trans.name}"/></a><br/>
                <small><c:out value="${trans.description}"/></small>
           </p>
        </c:forEach>
        </div>

        <hr/>

        <!-- USER PICKER -->

        <h3><s:message code="label.assignedUsers"/></h3>

        <div class="row-fluid">

            <div class="pull-right well well-small" style="width: 50%; height: 10em;">
                <h4><s:message code="label.assignedUsers"/></h4>
                <div id="assignedUsers">
                    <tags:loading/>
                </div>
            </div>

            <div class="searchFormPanel" id="searchFormPanel">
                <form action="javascript:void(0);" method="get">
                    <tags:hiddenEvent/>
                    <label><s:message code="label.group"/>:</label>
                    <select id="groupId">
                        <option></option>
                        <c:forEach items="${userGroups}" var="group">
                            <option value="${group.id}"><c:out value="${group.name}"/></option>
                        </c:forEach>
                    </select>
                    <label><s:message code="label.name"/>:</label> <input type="text" id="userFulltext"/>

                    <p>
                        <button onclick="return loadUsers();" class="btn"><s:message code="button.filterList"/></button>
                    </p>
                </form>
            </div>

        </div>


        <div id="userList">
            <tags:loading/>
        </div>


    </div>
</div>