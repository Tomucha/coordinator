<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
        taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %><%@
        taglib prefix="tags" tagdir="/WEB-INF/tags"
%><h2><s:message code="${form.new ? 'header.poiCreate' : 'header.poiEdit'}"/></h2>

<div class="mainPanel">
    <div class="eventForm">
        <sf:form method="POST" action="${root}/admin/event/user-group/edit" modelAttribute="form">

            <sf:errors />

            <div>
                <sf:hidden path="id"/>
                <sf:hidden path="eventId"/>
                <sf:hidden path="organizationId"/>
            </div>
                
            <div>
                <tags:input field="name" modelAttribute="form" captionCode="UserGroupEntity.name">
                    <sf:input path="name"/>
                </tags:input>
            </div>

            <div>
                <tags:input field="roleId" modelAttribute="form" captionCode="UserGroupEntity.role">
                    <sf:select path="roleId">
                        <sf:option value=""/>
                        <sf:options  items="${config.roleList}" itemLabel="name" itemValue="id"/>
                    </sf:select>
                </tags:input>
            </div>

            <div class="buttonPanel">
                <sf:button><s:message code="button.save"/></sf:button>
            </div>

        </sf:form>
    </div>
</div>
<script type="text/javascript">
    $(function() {
        if ($("#workflowSelect").val() == "")
            $("#workflowStateBox").hide();
    });
</script>
