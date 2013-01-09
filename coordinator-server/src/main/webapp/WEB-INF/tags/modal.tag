<%@
        attribute name="id" required="true" %><%@
        attribute name="titleCode" required="false" %><%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form"
%><div id="${id}" class="modal hide fade">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h3><s:message code="${titleCode}"/></h3>
    </div>
    <div class="modal-body">
        <jsp:doBody/>
    </div>
    <div class="modal-footer">
        <a href="#" class="btn" data-dismiss="modal" aria-hidden="true"><s:message code="button.cancel"/></a>
        <button type="submit" class="btn btn-primary"><s:message code="button.saveChanges"/></button>
    </div>
</div>