<%@ page contentType="text/html; charset=UTF-8" language="java" %><%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
        taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %><%@
        taglib prefix="tags" tagdir="/WEB-INF/tags"
%>
<div class="well">
<sf:form modelAttribute="user" method="POST">

    <sf:errors cssClass="alert alert-error" element="div" />

    <tags:input field="email" modelAttribute="user">
        <sf:input path="email" cssClass="lostPasswordEmailVal"/>
    </tags:input>

    <tags:input field="password" modelAttribute="user">
        <sf:password path="password" />
    </tags:input>

    <div>
        <input type="submit" value="<s:message code="button.login"/>" class="btn btn-large btn-primary"/>
    </div>

    <div class="lostPassword">
        <div class="lostPasswordButton">
            <a onclick="sentLostPassword();return false" class="clickable"><s:message code="label.sentLostPassword"/></a>
        </div>

        <div class="lostPasswordProgress" style="display: none">
            <img src="${root}/images/icons/throbber.gif" alt=""/> <s:message code="msg.lostPasswordEmailSending"/>
        </div>

        <div class="lostPasswordResult"></div>
    </div>

    </sf:form>
</div>