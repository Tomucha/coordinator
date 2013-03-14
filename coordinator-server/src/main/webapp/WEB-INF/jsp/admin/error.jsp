<%@ page contentType="text/html; charset=UTF-8" language="java" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %><%@
        taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%--@elvariable id="exceptionMessage" type="java.lang.String"--%>
<%--@elvariable id="exceptionStack" type="java.lang.String"--%>
<%--@elvariable id="exceptionCode" type="java.lang.String"--%>
<%--@elvariable id="exceptionCause" type="java.lang.String"--%>
<%--@elvariable id="exceptionCauseMessage" type="java.lang.String"--%>

<div class="error">
    <h2>${exceptionMessage}</h2>
    <p><strong><c:out value="${exceptionCode}"/></strong></p>
    <p>${exceptionCause}: ${exceptionCauseMessage}</p>

    <textarea cols="80" rows="20" class="help" style="width: 100%">
        <c:out value="${exceptionStack}"/>
    </textarea>
</div>
