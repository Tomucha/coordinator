<%@ page contentType="text/html; charset=UTF-8" language="java" %><%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
        taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %><%@
        taglib prefix="tags" tagdir="/WEB-INF/tags"
%>
<sf:form modelAttribute="user" method="POST">

    <sf:errors />

    <tags:input field="email" modelAttribute="user">
        <sf:input path="email" />
    </tags:input>

    <tags:input field="password" modelAttribute="user">
        <sf:password path="password" />
    </tags:input>

    <input type="submit" value="<s:message code="button.login"/>" class="btn btn-large btn-primary"/>

</sf:form>