<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
        taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %><%@
        taglib prefix="tags" tagdir="/WEB-INF/tags"
%>
<h2>
    <s:message code="header.massMail"/>
</h2>

<div class="mainPanel">

    <div class="mailForm">
        <sf:form method="POST" action="${root}/superadmin/mail" modelAttribute="params">

            <sf:errors />

            <tags:input field="subject" modelAttribute="params" captionCode="label.subject">
                <sf:input path="subject"/>
            </tags:input>

            <tags:input field="body" modelAttribute="form" captionCode="label.body">
                <sf:textarea path="body" cols="142" rows="7" cssStyle="width:50%"/>
            </tags:input>

            <div class="buttonPanel">
                <button type="submit" class="btn btn-primary"><span class="icon-ok icon-white"></span> <s:message code="button.send"/></button>
            </div>

        </sf:form>
    </div>

</div>

